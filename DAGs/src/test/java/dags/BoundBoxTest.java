package dags;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BoundBoxTest {

  private static final double MAX_X = Double.MAX_VALUE;
  private static final double MAX_Y = Double.MAX_VALUE;
  private static final double MIN_X = Double.MIN_VALUE;
  private static final double MIN_Y = Double.MIN_VALUE;
  private static BoundBox boundBox;

  @BeforeAll
  static void beforeAll() {
    boundBox = new BoundBox(MAX_X, MAX_Y, MIN_X, MIN_Y);
  }

  @Test
  void boundBoxIncorrectInitialization() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> new BoundBox(-1, 1, 1, 1));
    assertSame(IllegalArgumentException.class, throwable.getClass());
  }


  @Test
  void boundBoxGetters() {
    assertEquals(MAX_X, boundBox.getMaxX());
    assertEquals(MAX_Y, boundBox.getMaxY());
    assertEquals(MIN_X, boundBox.getMinX());
    assertEquals(MIN_Y, boundBox.getMinY());
  }

  @Test
  void boundBoxCreators() {
    var maxPoint = boundBox.createMaxPoint();
    assertEquals(maxPoint.getPosition().x(), MAX_X);
    assertEquals(maxPoint.getPosition().y(), MAX_Y);

    var minPoint = boundBox.createMinPoint();
    assertEquals(minPoint.getPosition().x(), MIN_X);
    assertEquals(minPoint.getPosition().y(), MIN_Y);
  }
}
