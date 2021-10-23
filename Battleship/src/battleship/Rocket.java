package battleship;

public class Rocket extends Coordinate {

  private final boolean isTorpedo;

  Rocket(int x, int y, boolean isTorpedo) {
    super(x, y);
    this.isTorpedo = isTorpedo;
  }

  public boolean isTorpedo() {
    return isTorpedo;
  }
}
