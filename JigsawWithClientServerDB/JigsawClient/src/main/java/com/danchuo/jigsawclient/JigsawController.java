package com.danchuo.jigsawclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class JigsawController {

  public static final int SERVER_PORT = 5000;
  public static final int OFFSET_X = 45;
  public static final int OFFSET_Y = 65;
  public static final int MAX_LOGIN_LENGTH = 32;
  private static final char DELIMITER = '%';
  private static final int SQUARE_SIZE = 67;
  private static final Pattern COMPILE = Pattern.compile("[a-zA-Z]+");
  @FXML private GridPane childGrid;
  @FXML private Button endGameButton;
  @FXML private TextField inputText;
  @FXML private Button showTopGamesButton;
  @FXML private TextField opponentName;
  @FXML private GridPane mainGrid;
  @FXML private Button registerButton;
  @FXML private TextField timerField;
  @FXML private TextArea textArea;
  @FXML private TextField maxDurationText;
  private int maxDuration;
  private JigsawClient jigsawClient;
  private JigsawModel currentJigsawModel;

  private static String convertIntToStringTime(int allSeconds) {
    var minutes = allSeconds / Timer.SECONDS;
    var seconds = allSeconds % Timer.SECONDS;
    return (minutes > 9 ? "" : "0") + minutes + ':' + (seconds > 9 ? "" : "0") + seconds;
  }

  private static String generateTopTableFromAnswer(String answer) {
    var table = answer.split(String.valueOf(DELIMITER));
    if (table.length < 1) {
      return "no recorded games";
    }
    var top = new StringBuilder(10);
    top.append("top 10 winners:\n");
    int parameterIndex = 0;

    for (var parameter : table) {
      switch (parameterIndex) {
        case 0 -> {
          top.append("player ");
          top.append(parameter);
        }
        case 1 -> {
          var date = LocalDateTime.parse(parameter).atZone(ZoneId.systemDefault()).toLocalDateTime();
          DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");
          OffsetDateTime timeUtc = date.atOffset(ZoneOffset.UTC);
          OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(OffsetDateTime.now().getOffset());
          top.append(", game ended at ");
          top.append(formatter2.format(offsetTime));
        }
        case 2 -> {
          top.append(", figures paced: ");
          top.append(parameter);
        }
        default -> {
          top.append(", time spent ");
          top.append(convertIntToStringTime(Integer.parseInt(parameter)));
          top.append("\n");
          parameterIndex = -1;
        }
      }
      ++parameterIndex;
    }

    return top.toString();
  }

  private static boolean isNodeAlreadyPlacedOnGrid(GridPane gridPane, int x, int y){
    for (var node : gridPane.getChildren()) {
      Integer currentX = GridPane.getColumnIndex(node);
      Integer currentY = GridPane.getRowIndex(node);
      if (currentX != null && currentY != null && currentX == x && currentY == y){
        return true;
      }

    }

    return false;
  }

  @FXML
  protected void onChildGridMouseDragged(MouseEvent event){
    if(!childGrid.getChildren().isEmpty()) {
      childGrid.setTranslateX(childGrid.getTranslateX() + event.getX() - childGrid.getWidth()/2);
      childGrid.setTranslateY(childGrid.getTranslateY() + event.getY() - childGrid.getHeight()/2);
    }
  }

  @FXML
  protected void onChildGridMouseReleased(MouseEvent event){
    if (!childGrid.getChildren().isEmpty()) {
      childGrid.setTranslateX(0);
      childGrid.setTranslateY(0);
      if (tryPlaceFigureToMainGrid(event)) {
         onMouseClickedPlaceFigureButton(null);
      }
    }
  }

  protected boolean tryPlaceFigureToMainGrid(MouseEvent event){
    if (!childGrid.getChildren().isEmpty()){
      int x = ((int)event.getSceneX() - OFFSET_X) / SQUARE_SIZE;
      int y = ((int)event.getSceneY() - OFFSET_Y) / SQUARE_SIZE;

      if (x > 8 || y > 8 || x < 0 || y < 0) {
        return false;
      }

      for (var point : childGrid.getChildren()) {
        int offsetX = x + GridPane.getColumnIndex(point) - 1;
        int offsetY = y + GridPane.getRowIndex(point) - 1;

        if (offsetX > 8 || offsetY > 8 || offsetX < 0 || offsetY < 0 || isNodeAlreadyPlacedOnGrid(mainGrid, offsetX, offsetY)) {
            return false;
        }
      }

      for (var point : childGrid.getChildren()) {
        int offsetX = x + GridPane.getColumnIndex(point) - 1;
        int offsetY = y + GridPane.getRowIndex(point) - 1;

        var rect = new StackPane();
        rect.setPrefHeight(SQUARE_SIZE);
        rect.setPrefWidth(SQUARE_SIZE);
        rect.setStyle("-fx-background-color:#7b68ee;");
        GridPane.setConstraints(rect, offsetX, offsetY);
        mainGrid.getChildren().add(rect);
      }

      return true;
    }

    return false;
  }

  @FXML
  protected void onMouseClickedShowTopButton(MouseEvent event) throws IOException {
    appendText(generateTopTableFromAnswer(jigsawClient.sendMessageAndGetAnswer("t")));
  }

  @FXML
  protected void onMouseClickedEndGameButton(MouseEvent event) throws IOException {
    appendText("game is over, thank you");
    endGameButton.setDisable(true);
    showTopGamesButton.setDisable(true);
    currentJigsawModel.endGame();

    new Thread(
            () -> {
              try {
                appendText(
                    jigsawClient.sendMessageAndGetAnswer(
                        "e"
                            + DELIMITER
                            + currentJigsawModel.getPlacedFigures()
                            + DELIMITER
                            + currentJigsawModel.getSecondsPassed()));
                appendText("waiting for the rest of the clients to finish the game ");
                appendText(jigsawClient.getAnswer());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .start();
  }

  @FXML
  protected void onMouseClickedPlaceFigureButton(MouseEvent event) {
    appendText("figure placed " + currentJigsawModel.getFigureToPlace());
    currentJigsawModel.placeFigure();
    try {
      currentJigsawModel.setFigureToPlace(
          jigsawClient.sendMessageAndGetAnswer(
              "f" + DELIMITER + currentJigsawModel.getPlacedFigures()));
      placeFigureToChildGrid();
    } catch (IOException e) {
      appendText(e.getMessage());
    }

    appendText("received figure " + currentJigsawModel.getFigureToPlace());
  }

  private void placeFigureToChildGrid(){
    if (!childGrid.getChildren().isEmpty()){
        childGrid.getChildren().clear();
    }
    var figure = Integer.parseInt(currentJigsawModel.getFigureToPlace().split(" ")[0]);
    var orientation = Integer.parseInt(currentJigsawModel.getFigureToPlace().split(" ")[1]);

    var points= FigureCreator.getFigure(figure, orientation);

    for (var point : points) {
      var rec = new StackPane();
      rec.setPrefHeight(SQUARE_SIZE);
      rec.setPrefWidth(SQUARE_SIZE);
      rec.setStyle("-fx-background-color: #ff7f50;");
      GridPane.setConstraints(rec, point.x(), point.y());
      childGrid.getChildren().add(rec);
    }

    childGrid.setTranslateX(0);
    childGrid.setTranslateY(0);
  }

  @FXML
  protected void onMouseClickedRegisterButton(MouseEvent event) {
    if (Objects.equals(inputText.getText(), "") || !COMPILE.matcher(inputText.getText()).matches() || inputText.getText().length() > MAX_LOGIN_LENGTH) {
      appendText("string is empty or contains non-alphabets symbols or contains more than 32 symbols");
      return;
    }

    try {
      currentJigsawModel = new JigsawModel(this::updateTime, inputText.getText());
      appendText(jigsawClient.sendMessageAndGetAnswer("r " + currentJigsawModel.getName()));
      registerButton.setDisable(true);
      inputText.setDisable(true);

      CompletableFuture<String> completableFuture =
          CompletableFuture.supplyAsync(
                  () -> {
                    try {
                      return jigsawClient.getAnswer();
                    } catch (IOException e) {
                      appendText(e.getMessage());
                    }
                    return "register expetion";
                  })
              .whenCompleteAsync(
                  (result, exeption) -> {
                    textArea.clear();
                    handleStartGameCommand(result);
                  });

    } catch (IOException e) {
      appendText(e.getMessage());
    }
  }

  private void handleStartGameCommand(String command) {
    var parts = command.split(String.valueOf(DELIMITER));

    appendText(parts[0]);
    currentJigsawModel.setFigureToPlace(parts[1]);
    appendText("received figure " + currentJigsawModel.getFigureToPlace());
    opponentName.setText(parts[2]);

    maxDuration = Integer.parseInt(parts[3]);
    maxDurationText.setText(convertIntToStringTime(maxDuration));

    endGameButton.setDisable(false);
    showTopGamesButton.setDisable(false);

    currentJigsawModel.startGame();
    Platform.runLater(this::placeFigureToChildGrid);
  }

  @FXML
  protected void initialize() {
    try {
      textArea.clear();
      jigsawClient = new JigsawClient("localhost", SERVER_PORT);
      appendText(jigsawClient.getAnswer());
      timerField.setText("00:00");
    } catch (ConnectException ex) {
      appendText("server is offline, please start server and new client");
      registerButton.setDisable(true);
    } catch (Exception ex) {
      appendText("got exception: " + ex);
    }

    endGameButton.setDisable(true);
    showTopGamesButton.setDisable(true);
  }

  private void updateTime() {
    timerField.setText(convertIntToStringTime(currentJigsawModel.getSecondsPassed()));
    if (currentJigsawModel.getSecondsPassed() >= maxDuration) {
      appendText("your time is up");
      try {
        onMouseClickedEndGameButton(null);
      } catch (IOException e) {
        appendText(e.getMessage());
      }
    }
  }

  private void appendText(String text) {
    textArea.appendText(
        "["
            + LocalDateTime.now().getHour()
            + ":"
            + LocalDateTime.now().getMinute()
            + "]"
            + ' '
            + text
            + '\n');
  }
}
