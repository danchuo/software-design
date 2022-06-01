package com.danchuo.jigsawclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class JigsawController {

  public static final int OFFSET_X = 45;
  public static final int OFFSET_Y = 65;
  public static final int MAX_LOGIN_LENGTH = 32;
  private static final char DELIMITER = '%';
  private static final int SQUARE_SIZE = 67;
  private static final Pattern COMPILE = Pattern.compile("[a-zA-Z]+");
  public static JigsawClient jigsawClient = null;
  @FXML private GridPane childGrid;
  @FXML private Button endGameButton;
  @FXML private TextField inputText;
  @FXML private Button showTopGamesButton;
  @FXML private Button newGameButton;
  @FXML private TextField opponentName;
  @FXML private GridPane mainGrid;
  @FXML private Button registerButton;
  @FXML private TextField timerField;
  @FXML private TextArea textArea;
  @FXML private TextField maxDurationText;
  private int maxDuration;
  private JigsawModel currentJigsawModel;

  public static void exitApplication() throws Exception {
    if (jigsawClient != null) {
      jigsawClient.close();
    }
  }

  @FXML
  protected void onChildGridMouseDragged(MouseEvent event) {
    if (!childGrid.getChildren().isEmpty()) {
      childGrid.setTranslateX(childGrid.getTranslateX() + event.getX() - childGrid.getWidth() / 2);
      childGrid.setTranslateY(childGrid.getTranslateY() + event.getY() - childGrid.getHeight() / 2);
    }
  }

  @FXML
  protected void onChildGridMouseReleased(MouseEvent event) {
    if (!childGrid.getChildren().isEmpty()) {
      childGrid.setTranslateX(0);
      childGrid.setTranslateY(0);
      if (tryPlaceFigureToMainGrid(event)) {
        placeFigure();
      }
    }
  }

  protected boolean tryPlaceFigureToMainGrid(MouseEvent event) {
    if (!childGrid.getChildren().isEmpty()) {
      int x = ((int) event.getSceneX() - OFFSET_X) / SQUARE_SIZE;
      int y = ((int) event.getSceneY() - OFFSET_Y) / SQUARE_SIZE;

      if (x > 8 || y > 8 || x < 0 || y < 0) {
        return false;
      }

      for (var point : childGrid.getChildren()) {
        int offsetX = x + GridPane.getColumnIndex(point) - 1;
        int offsetY = y + GridPane.getRowIndex(point) - 1;

        if (offsetX > 8
            || offsetY > 8
            || offsetX < 0
            || offsetY < 0
            || JigsawUtils.isNodeAlreadyPlacedOnGrid(mainGrid, offsetX, offsetY)) {
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
  protected void onMouseClickedShowTopButton(MouseEvent event) {
    appendTextToLog(
        JigsawUtils.generateTopTableFromAnswer(
            jigsawClient.sendMessageAndGetAnswer("t"), DELIMITER));
  }

  @FXML
  protected void onMouseClickedNewGameButton(MouseEvent event) throws Exception {
    initialize();
  }

  @FXML
  protected void onMouseClickedEndGameButton(MouseEvent event) {
    appendTextToLog("game is over, thank you");
    endGameButton.setDisable(true);
    showTopGamesButton.setDisable(true);
    currentJigsawModel.endGame();
    childGrid.getChildren().clear();

    new Thread(
            () -> {
              appendTextToLog(
                  jigsawClient.sendMessageAndGetAnswer(
                      "e"
                          + DELIMITER
                          + currentJigsawModel.getPlacedFigures()
                          + DELIMITER
                          + currentJigsawModel.getSecondsPassed()));
              appendTextToLog("waiting for the rest of the clients to finish the game ");
              appendTextToLog(jigsawClient.getAnswer());
              newGameButton.setVisible(true);
              appendTextToLog("do you wanna start new game?");
            })
        .start();
  }

  @FXML
  protected void placeFigure() {
    appendTextToLog("figure placed " + currentJigsawModel.getFigureToPlace());
    currentJigsawModel.placeFigure();
    currentJigsawModel.setFigureToPlace(
        jigsawClient.sendMessageAndGetAnswer(
            "f" + DELIMITER + currentJigsawModel.getPlacedFigures()));
    placeFigureToChildGrid();

    appendTextToLog("received figure " + currentJigsawModel.getFigureToPlace());
  }

  private void placeFigureToChildGrid() {
    if (!childGrid.getChildren().isEmpty()) {
      childGrid.getChildren().clear();
    }
    int figure = 0;
    int orientation = 0;
    try {
      figure = Integer.parseInt(currentJigsawModel.getFigureToPlace().split(" ")[0]);
      orientation = Integer.parseInt(currentJigsawModel.getFigureToPlace().split(" ")[1]);
    } catch (Exception ignored) {
    }

    var points = FigureCreator.getFigure(figure, orientation);

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
    if (Objects.equals(inputText.getText(), "")
        || !COMPILE.matcher(inputText.getText()).matches()
        || inputText.getText().length() > MAX_LOGIN_LENGTH) {
      appendTextToLog(
          "string is empty or contains non-English characters or contains more than 32 characters");
      inputText.clear();
      return;
    }

    currentJigsawModel = new JigsawModel(this::updateUITime, inputText.getText());
    appendTextToLog(jigsawClient.sendMessageAndGetAnswer("r " + currentJigsawModel.getName()));
    registerButton.setDisable(true);
    inputText.setDisable(true);

    CompletableFuture<String> completableFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  return jigsawClient.getAnswer();
                })
            .whenCompleteAsync(
                (result, exeption) -> {
                  textArea.clear();
                  handleStartGameCommand(result);
                });
  }

  private void handleStartGameCommand(String command) {
    var parts = command.split(String.valueOf(DELIMITER));

    appendTextToLog(parts[0]);
    currentJigsawModel.setFigureToPlace(parts[1]);
    appendTextToLog("received figure " + currentJigsawModel.getFigureToPlace());
    opponentName.setText(parts[2]);

    maxDuration = Integer.parseInt(parts[3]);
    maxDurationText.setText(JigsawUtils.convertIntToStringTime(maxDuration));

    endGameButton.setDisable(false);
    showTopGamesButton.setDisable(false);

    currentJigsawModel.startGame();
    Platform.runLater(this::placeFigureToChildGrid);
  }

  @FXML
  protected void initialize() throws Exception {
    if (jigsawClient != null) {
      jigsawClient.close();
    }

    try {
      textArea.clear();
      jigsawClient =
          new JigsawClient(
              () -> {
                onMouseClickedEndGameButton(null);
              });
      appendTextToLog(jigsawClient.getAnswer());
      timerField.setText("00:00");
      registerButton.setDisable(false);
    } catch (ConnectException ex) {
      appendTextToLog("server is offline, please start server and new client");
      registerButton.setDisable(true);
      inputText.setEditable(false);
    } catch (Exception ex) {
      appendTextToLog("got exception: " + ex);
    }

    endGameButton.setDisable(true);
    showTopGamesButton.setDisable(true);
    newGameButton.setVisible(false);
    inputText.setDisable(false);
    inputText.clear();
    Node lines = mainGrid.getChildren().get(0);
    mainGrid.getChildren().clear();
    mainGrid.getChildren().add(lines);
    mainGrid.setGridLinesVisible(true);
    opponentName.clear();
  }

  private void updateUITime() {
    timerField.setText(JigsawUtils.convertIntToStringTime(currentJigsawModel.getSecondsPassed()));
    if (currentJigsawModel.getSecondsPassed() >= maxDuration) {
      appendTextToLog("your time is up");
      onMouseClickedEndGameButton(null);
    }
  }

  private void appendTextToLog(String text) {
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
