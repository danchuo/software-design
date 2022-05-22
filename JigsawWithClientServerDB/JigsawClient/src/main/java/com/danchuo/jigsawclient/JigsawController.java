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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JigsawController {

  private static final char DELIMITER = '%';
  public static final int SECONDS = 60;
  public static final int SERVER_PORT = 5000;

  @FXML private GridPane childGrid;
  @FXML private Button endGameButton;
  @FXML private TextField inputText;
  @FXML private TextField opponentName;
  @FXML private GridPane mainGrid;
  @FXML private Button placeFigureButton;
  @FXML private Button registerButton;
  @FXML private TextField timerField;
  @FXML private TextArea textArea;
  private JigsawClient jigsawClient;
  private String figureToPlace;
  private int placedFigures;
  private volatile int secondsPassed;
  private volatile boolean isTimerOn;

  @FXML
  protected void onMouseClickedEndGameButton(MouseEvent event) {
    appendText("game is over, thank you.\nwaiting for the rest of the clients to finish the game ");
    placeFigureButton.setDisable(true);
    endGameButton.setDisable(true);
    isTimerOn = false;

    CompletableFuture<String> completableFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  try {
                    return jigsawClient.sendMessageAndGetAnswer("e" + DELIMITER + placedFigures);
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                })
            .whenCompleteAsync(
                (result, exeption) -> {
                  appendText(result);
                });
  }

  @FXML
  protected void onMouseClickedPlaceFigureButton(MouseEvent event) {
    appendText("figure placed " + figureToPlace);
    ++placedFigures;
    try {
      figureToPlace = jigsawClient.sendMessageAndGetAnswer("f" + DELIMITER + placedFigures);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    appendText("received figure " + figureToPlace);
  }

  @FXML
  protected void onMouseClickedRegisterButton(MouseEvent event) {
    if (Objects.equals(inputText.getText(), "")) {
      appendText("string is empty");
      return;
    }

    try {
      appendText(jigsawClient.sendMessageAndGetAnswer("r " + inputText.getText()));
      registerButton.setDisable(true);
      inputText.setDisable(true);

      CompletableFuture<String> completableFuture =
          CompletableFuture.supplyAsync(
                  () -> {
                    try {
                      return jigsawClient.getAnswer();
                    } catch (IOException e) {
                      throw new RuntimeException(e);
                    }
                  })
              .whenCompleteAsync(
                  (result, exeption) -> {
                    textArea.clear();
                    handleStartGameCommand(result);
                  });

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleStartGameCommand(String command) {
    var parts = command.split(String.valueOf(DELIMITER));

    appendText(parts[0]);
    figureToPlace = parts[1];
    appendText("received figure " + figureToPlace);
    opponentName.setText(parts[2]);

    placeFigureButton.setDisable(false);
    endGameButton.setDisable(false);

    Thread timerThread =
        new Thread(
            () -> {
              while (isTimerOn) {
                addSecond();
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                  return;
                }
              }
            });
    timerThread.start();
  }

  private void addSecond() {
    ++secondsPassed;

    var minutes = secondsPassed / SECONDS;
    var seconds = secondsPassed % SECONDS;
    timerField.setText(String.valueOf(minutes) + ':' + seconds);
  }

  @FXML
  protected void initialize() {
    try {
      isTimerOn = true;
      jigsawClient = new JigsawClient("localhost", SERVER_PORT);
      appendText(jigsawClient.getAnswer());
      timerField.setText("00:00");
    } catch (ConnectException ex) {
      appendText("server is offline, please start server and new client");
    } catch (Exception ex) {
      appendText("got exception: " + ex);
    }

    placeFigureButton.setDisable(true);
    endGameButton.setDisable(true);
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
