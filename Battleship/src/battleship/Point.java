package battleship;

public class Point {
  private final int x;
  private final int y;
  private boolean isHitten;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
    isHitten = false;
  }

  public boolean isHitten() {
    return isHitten;
  }

  public void setHitten(boolean hitten) {
    isHitten = hitten;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
