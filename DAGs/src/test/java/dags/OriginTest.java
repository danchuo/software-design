package dags;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OriginTest {

  private static final double MAX_X = 5;
  private static final double MAX_Y = 10;
  private static final double MIN_X = -5;
  private static final double MIN_Y = -10;
  private static final Coord2D MAX_COORD = new Coord2D(MAX_X, MAX_Y);
  private static final Coord2D MIN_COORD = new Coord2D(MIN_X, MIN_Y);
  private static final Coord2D CENTR_COORD = new Coord2D(0, 0);

  private static Origin origin;
  private static Set<Point> set;


  @BeforeEach
  void beforeAll() {
    set = new HashSet<>();
    origin = new Origin(CENTR_COORD);

    set.add(new Point(MAX_COORD));
    set.add(new Point(MIN_COORD));
  }


  @Test
  void originInitialization() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> new Origin(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());
  }

  @Test
  void getChildren() {
    var children = origin.getChildren();
    assertTrue(children.isEmpty());
  }

  @Test
  void setChildrenNull() {
    Throwable throwable = assertThrows(IllegalArgumentException.class,
        () -> origin.setChildren(null));
    assertSame(IllegalArgumentException.class, throwable.getClass());
  }

  @Test
  void setChildrenWithEqualLinks() {
    set.add(origin);
    var internalOrigin = new Origin(new Coord2D(2, 2));

    try {
      internalOrigin.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }

    set.add(internalOrigin);

    try {
      origin.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }

    Throwable dagConstraintException = assertThrows(DAGConstraintException.class,
        () -> origin.setChildren(set));
    assertSame(DAGConstraintException.class, dagConstraintException.getClass());
  }

  @Test
  void getBoundBoxOfWithoutCycles() {
    var internalOrigin = new Origin(new Coord2D(2, 2));
    try {
      internalOrigin.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }

    set.add(internalOrigin);

    try {
      origin.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }

    var boundBoxOptional = origin.getBoundBox();
    assertFalse(boundBoxOptional.isEmpty());
    var boundBox = boundBoxOptional.get();

    assertEquals(boundBox.getMaxX(), internalOrigin.getPosition().x() + MAX_COORD.x());
    assertEquals(boundBox.getMinX(), origin.getPosition().x() + MIN_COORD.x());
    assertEquals(boundBox.getMaxY(), internalOrigin.getPosition().y() + MAX_COORD.y());
    assertEquals(boundBox.getMinY(), origin.getPosition().y() + MIN_COORD.y());
  }


  @Test
  void getBoundBoxEmpty() {
    var boundBoxOptional = origin.getBoundBox();
    assertTrue(boundBoxOptional.isEmpty());
  }

  void getBoundBoxOnlyPoints() {
    try {
      origin.setChildren(set);
    } catch (DAGConstraintException ignored) {
    }
    var boundBoxOptional = origin.getBoundBox();
    assertFalse(boundBoxOptional.isEmpty());

    var boundBox = boundBoxOptional.get();

    assertEquals(boundBox.createMaxPoint().getPosition(), MAX_COORD);
    assertEquals(boundBox.createMinPoint().getPosition(), MIN_COORD);
  }

  @Test
  void checkForCycles() {
    var firstInternalOrigin = new Origin(CENTR_COORD);
    var secondInternalOrigin = new Origin(CENTR_COORD);

    var firstSet = new HashSet<Point>();
    firstSet.add(firstInternalOrigin);

    var secondSet = new HashSet<Point>();
    secondSet.add(secondInternalOrigin);

    try {
      firstInternalOrigin.setChildren(new HashSet<>(secondSet));
    } catch (DAGConstraintException ignored) {
    }

    try {
      secondInternalOrigin.setChildren(new HashSet<>(firstSet));
    } catch (DAGConstraintException ignored) {
    }

    set.add(firstInternalOrigin);

    Throwable dagConstraintException = assertThrows(DAGConstraintException.class,
        () -> origin.setChildren(set));
    assertSame(DAGConstraintException.class, dagConstraintException.getClass());
  }
}