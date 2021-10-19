package battleship;

public class Coordinate {

  private final int x;
  private final int y;

  Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean areCoordinatesEqual(Coordinate other) {
    if (other == null) {
      return false;
    }

    return this.x == other.x && this.y == other.y;
  }
}
