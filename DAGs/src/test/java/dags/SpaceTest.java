package dags;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpaceTest {


  private static final double MAX_X = 5;
  private static final double MAX_Y = 10;
  private static final double MIN_X = -5;
  private static final double MIN_Y = -10;
  private static final Coord2D MAX_COORD = new Coord2D(MAX_X, MAX_Y);
  private static final Coord2D MIN_COORD = new Coord2D(MIN_X, MIN_Y);
  private static final Coord2D CENTR_COORD = new Coord2D(0, 0);

  private static Space space;
  private static Set<Point> pointsSet;


  @BeforeEach
  void beforeAll() {
    pointsSet = new HashSet<>();
    space = new Space(CENTR_COORD);

    pointsSet.add(new Point(MAX_COORD));
    pointsSet.add(new Point(MIN_COORD));
  }


  @Test
  void spaceInitialization() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> new Space(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());

    var zeroSpace = new Space();
    assertEquals(zeroSpace.getPosition(), new Coord2D(0, 0));
  }

  @Test
  void setChildrenNull() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> space.setChildren(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());
  }

  @Test
  void getChildren() {
    var children = space.getChildren();
    assertTrue(children.isEmpty());
  }

  @Test
  void getPosition() {
    assertEquals(new Space(new Coord2D(-4, -4)).getPosition(), new Coord2D(-4, -4));
  }

  @Test
  void setPosition() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> space.setPosition(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());
    assertEquals(throwable.getMessage(), "Position must be not null!");
    space.setPosition(MAX_COORD);
    assertEquals(space.getPosition(), MAX_COORD);
  }

  @Test
  void getBoundBox() {
    var set = new HashSet<Origin>();
    var origin = new Origin(CENTR_COORD);
    var internalOrigin = new Origin(new Coord2D(2, 2));
    try {
      origin.setChildren(pointsSet);
      internalOrigin.setChildren(pointsSet);
    } catch (DAGConstraintException ignored) {
    }

    set.add(internalOrigin);

    try {
      space.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }

    var boundBoxOptional = space.getBoundBox();
    assertFalse(boundBoxOptional.isEmpty());
    var boundBox = boundBoxOptional.get();

    assertEquals(boundBox.getMaxX(), internalOrigin.getPosition().x() + MAX_COORD.x());
    assertEquals(boundBox.getMinX(), internalOrigin.getPosition().x() + MIN_COORD.x());
    assertEquals(boundBox.getMaxY(), internalOrigin.getPosition().y() + MAX_COORD.y());
    assertEquals(boundBox.getMinY(), internalOrigin.getPosition().y() + MIN_COORD.y());
  }
}