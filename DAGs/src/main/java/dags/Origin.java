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

    checkForCycles(children);

    this.children = new HashSet<>(children);
  }

  @Override
  public Optional<BoundBox> getBoundBox() {
    Optional<BoundBox> returnValue = Optional.empty();

    if (!children.isEmpty()) {
      var maxX = Double.MIN_VALUE;
      var maxY = Double.MIN_VALUE;
      var minX = Double.MAX_VALUE;
      var minY = Double.MAX_VALUE;
      boolean isSomeoneFinded = false;

      for (var child : children) {
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
        returnValue = Optional.of(new BoundBox(maxX + getPosition().x(), maxY + getPosition().y(),
            minX + getPosition().x(), minY + getPosition().x()));
      }
    }

    return returnValue;
  }

  private void checkForCycles(Set<Point> setToCheck) throws DAGConstraintException {
    try {
      for (var point : setToCheck) {
        if (point instanceof Origin origin) {
          origin.checkForCyclesRecurive(this);
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
