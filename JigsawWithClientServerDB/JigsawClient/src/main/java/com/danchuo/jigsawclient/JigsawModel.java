package com.danchuo.jigsawclient;

public class JigsawModel {

  private final Timer timer;
  private final String name;
  private String figureToPlace;
  private int placedFigures;

  public JigsawModel(Runnable addSecondEvent, String name) {
    this.name = name;
    placedFigures = 0;
    timer = new Timer(addSecondEvent);
  }

  public String getName() {
    return name;
  }

  public int getPlacedFigures() {
    return placedFigures;
  }

  public int getSecondsPassed() {
    return timer.getSecondsPassed();
  }

  public String getFigureToPlace() {
    return figureToPlace;
  }

  public void setFigureToPlace(String figureToPlace) {
    this.figureToPlace = figureToPlace;
  }

  public void placeFigure() {
    ++placedFigures;
  }

  public void startGame() {
    timer.setTimerOn(true);
    timer.startTimer();
  }

  public void endGame() {
    timer.setTimerOn(false);
  }
}
