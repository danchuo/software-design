package battleship;

public class Coordinate {

  private int x;
  private int y;

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean areCoordinatesEqual(Coordinate other) {
    return other != null && x == other.x && y == other.y;
  }
}
