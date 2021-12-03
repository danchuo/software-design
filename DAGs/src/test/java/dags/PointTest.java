package dags;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PointTest {

  private static Coord2D coordFirst;
  private static Coord2D coordSecond;

  @BeforeAll
  static void beforeAll() {
    coordFirst = new Coord2D(Double.MAX_VALUE, Double.MAX_VALUE);
    coordSecond = new Coord2D(Double.MIN_VALUE, Double.MIN_VALUE);
  }


  @Test
  void pointInitialization() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> new Point(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());

    var point = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
    assertEquals(point.getPosition(), coordFirst);
  }

  @Test
  void pointGetters() {
    var point = new Point(coordFirst);
    assertEquals(point.getPosition(), coordFirst);

    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> point.setPosition(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());

    point.setPosition(coordSecond);
    assertEquals(point.getPosition(), coordSecond);
  }

  @Test
  void pointGetBoundBox() {
    var point = new Point(coordFirst);

    var boundBoxOptional = point.getBoundBox();
    assertTrue(boundBoxOptional.isPresent());

    var boundBox = boundBoxOptional.get();

    assertEquals(boundBox.getMaxX(), point.getPosition().x());
    assertEquals(boundBox.getMinX(), point.getPosition().x());

    assertEquals(boundBox.getMinY(), point.getPosition().y());
    assertEquals(boundBox.getMinY(), point.getPosition().y());
  }
}
