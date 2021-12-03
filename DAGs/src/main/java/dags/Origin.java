package dags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.management.openmbean.KeyAlreadyExistsException;

public class Origin extends Point {

  private Set<Point> children;

  private boolean isBlack;

  Origin(Coord2D position) {
    super(position);
    isBlack = true;
    children = new HashSet<>();
  }

  public Set<Point> getChildren() {
    return Collections.unmodifiableSet(children);
  }

  public void setChildren(Set<Point> children) throws DAGConstraintException {
    if (children == null) {
      throw new IllegalArgumentException("Children must be not null!");
    }

    checkForCycles(this, children);

    this.children = new HashSet<>(children);
  }

  public static Optional<BoundBox> getBoundBoxOf(Coord2D startPosition, Set<Point> set) {
    Optional<BoundBox> returnValue = Optional.empty();

    if (!set.isEmpty()) {
      var maxX = Double.MIN_VALUE;
      var maxY = Double.MIN_VALUE;
      var minX = Double.MAX_VALUE;
      var minY = Double.MAX_VALUE;
      boolean isSomeoneFinded = false;

      for (var child : set) {
        var boundBox = child.getBoundBox();
        if (boundBox.isPresent()) {
          maxX = Double.max(maxX, boundBox.get().getMaxX());
          maxY = Double.max(maxY, boundBox.get().getMaxY());
          minX = Double.min(minX, boundBox.get().getMinX());
          minY = Double.min(minY, boundBox.get().getMinY());
          isSomeoneFinded = true;
        }
      }

      if (isSomeoneFinded) {
        returnValue = Optional.of(new BoundBox(maxX + startPosition.x(), maxY + startPosition.y(),
            minX + startPosition.x(), minY + startPosition.x()));
      }
    }

    return returnValue;
  }

  @Override
  public Optional<BoundBox> getBoundBox() {
    return getBoundBoxOf(getPosition(), children);
  }

  public static void checkForCycles(Origin originToSet, Set<Point> setToCheck)
      throws DAGConstraintException {
    try {
      for (var point : setToCheck) {
        if (point instanceof Origin origin) {
          origin.checkForCyclesRecurive(originToSet);
        }
      }
    } catch (KeyAlreadyExistsException ignored) {
      throw new DAGConstraintException("Cyclicity is noticed!");
    }
  }

  private void checkForCyclesRecurive(Origin originToSet) {
    if (this == originToSet) {
      throw new KeyAlreadyExistsException("Cyclicity is noticed!");
    }
    isBlack = false;

    for (var point : children) {
      if (point instanceof Origin origin) {
        if (origin.isBlack) {
          origin.checkForCyclesRecurive(this);
        } else {
          throw new KeyAlreadyExistsException("Cyclicity is noticed!");
        }
      }
    }

    isBlack = true;
  }
}
