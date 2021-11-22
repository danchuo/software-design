package dags;

public class BoundBox {

  private final Coord2D maxCoord2D;
  private final Coord2D mixCoord2D;

  BoundBox(int maxX, int maxY, int minX, int minY) {
    maxCoord2D = new Coord2D(maxX, maxY);
    mixCoord2D = new Coord2D(minX, minY);
  }

  public Point createMaxPoint() {
    return new Point(maxCoord2D);
  }

  public Point createMinPoint() {
    return new Point(mixCoord2D);
  }
}
