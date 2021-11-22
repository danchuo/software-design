package dags;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Point implements Cloneable {

  private Coord2D position;

  Point(Coord2D position) {
    this.position = position;
  }

  public final Coord2D getPosition() {
    return position;
  }

  public final void setPosition(Coord2D position) {
    this.position = position;
  }

  public BoundBox getBoundBox() {
    throw new NotImplementedException("boundbox");
  }

  @Override
  public Point clone() {
    return new Point(position);
  }
}
