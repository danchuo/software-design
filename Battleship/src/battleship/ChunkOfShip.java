package battleship;

public class ChunkOfShip extends Coordinate {

  private boolean isHit;

  public ChunkOfShip(int x, int y) {
    super(x, y);
    isHit = false;
  }

  public boolean isHit() {
    return isHit;
  }

  public void setHit(boolean hit) {
    isHit = hit;
  }
}
