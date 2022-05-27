package com.danchuo.jigsawclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JigsawController {

  public static final int SERVER_PORT = 5000;
  private static final char DELIMITER = '%';
  @FXML private GridPane childGrid;
  @FXML private Button endGameButton;
  @FXML private TextField inputText;
  @FXML private Button showTopGamesButton;
  @FXML private TextField opponentName;
  @FXML private GridPane mainGrid;
  @FXML private Button placeFigureButton;
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
    var top = new StringBuilder(10);
    var table = answer.split(String.valueOf(DELIMITER));
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

  @FXML
  protected void onMouseClickedShowTopButton(MouseEvent event) throws IOException {
    appendText(generateTopTableFromAnswer(jigsawClient.sendMessageAndGetAnswer("t")));
  }

  @FXML
  protected void onMouseClickedEndGameButton(MouseEvent event) throws IOException {
    appendText("game is over, thank you");
    placeFigureButton.setDisable(true);
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
    } catch (IOException e) {
      appendText(e.getMessage());
    }

    appendText("received figure " + currentJigsawModel.getFigureToPlace());
  }

  @FXML
  protected void onMouseClickedRegisterButton(MouseEvent event) {
    if (Objects.equals(inputText.getText(), "")) {
      appendText("string is empty");
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

    placeFigureButton.setDisable(false);
    endGameButton.setDisable(false);
    showTopGamesButton.setDisable(false);

    currentJigsawModel.startGame();
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

    placeFigureButton.setDisable(true);
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
