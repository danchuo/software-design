package dags;

import java.io.Serializable;

public record Coord2D(double x, double y) implements Serializable {

  public Coord2D negate() {
    return new Coord2D(-x, -y);
  }

  public Coord2D add(Coord2D other) {
    return new Coord2D(Double.sum(x, other.x), Double.sum(y, other.y));
  }

  public Coord2D subtract(Coord2D other) {
    return new Coord2D(x - other.x, y - other.y);
  }
}
