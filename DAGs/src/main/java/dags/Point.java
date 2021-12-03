package dags;

import java.util.Optional;

public class Point {

  private Coord2D position;

  Point(Coord2D position) {
    if (position == null) {
      throw new IllegalArgumentException("Position must be not null!");
    }

    this.position = position;
  }

  Point(double x, double y) {
    position = new Coord2D(x, y);
  }

  public final Coord2D getPosition() {
    return position;
  }

  public final void setPosition(Coord2D position) {
    if (position == null) {
      throw new IllegalArgumentException("Position must be not null!");
    }

    this.position = position;
  }

  public Optional<BoundBox> getBoundBox() {
    return Optional.of(new BoundBox(position.x(), position.y(), position.x(), position.y()));
  }
}
