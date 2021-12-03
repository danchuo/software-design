package dags;

public class BoundBox {

  private final Coord2D maxCoord2D;
  private final Coord2D minCoord2D;

  BoundBox(double maxX, double maxY, double minX, double minY) {
    if (minX > maxX || minY > maxY) {
      throw new IllegalArgumentException("Invalid coordinates!");
    }
    maxCoord2D = new Coord2D(maxX, maxY);
    minCoord2D = new Coord2D(minX, minY);
  }

  public Point createMaxPoint() {
    return new Point(maxCoord2D);
  }

  public Point createMinPoint() {
    return new Point(minCoord2D);
  }

  public double getMaxX() {
    return maxCoord2D.x();
  }

  public double getMaxY() {
    return maxCoord2D.y();
  }

  public double getMinX() {
    return minCoord2D.x();
  }

  public double getMinY() {
    return minCoord2D.y();
  }
}
