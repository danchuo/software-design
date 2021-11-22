package dags;

import java.util.HashSet;

public class Space {
  public static void main(String[] args) {
    var point1 = new Point(new Coord2D(0, 0));
    var point2 = point1.clone();
    var origin1 = new Origin(new Coord2D(100, 100));
    var origin2 = new Origin(new Coord2D(-100, -100));

    var hash = new HashSet<Point>();
    hash.add(origin1);
    hash.add(point1);
    origin2.setChildren(hash);

    var origin3 = origin2.clone();

    for (var or : origin3.getChildren()) {
      for (var or2 : origin2.getChildren()) {
        System.out.println(or == or2);
      }
    }
  }
}
