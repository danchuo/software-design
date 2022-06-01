package com.danchuo.jigsawclient;

import javafx.application.Platform;

public class Timer {

  public static final int SECONDS = 60;
  private final Thread timerThread;
  private final Runnable addSecondEvent;
  private volatile boolean isTimerOn;
  private volatile int secondsPassed;

  public Timer(Runnable addSecondEvent) {
    this.addSecondEvent = addSecondEvent;
    timerThread =
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
  }

  public boolean isTimerOn() {
    return isTimerOn;
  }

  public void setTimerOn(boolean timerOn) {
    isTimerOn = timerOn;
  }

  public int getSecondsPassed() {
    return secondsPassed;
  }

  private void addSecond() {
    ++secondsPassed;
    if (addSecondEvent != null) {
      Platform.runLater(addSecondEvent);
    }
  }

  public void startTimer() {
    timerThread.start();
  }
}
