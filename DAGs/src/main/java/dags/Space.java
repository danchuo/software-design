package dags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Space {

  private Set<Origin> children;

  private Coord2D position;

  Space(Coord2D position) {
    if (position == null) {
      throw new IllegalArgumentException("Position must be not null!");
    }
    this.position = position;
    children = new HashSet<>();
  }

  Space() {
    this(new Coord2D(0, 0));
  }

  public void setChildren(Set<Origin> children) throws DAGConstraintException {
    if (children == null) {
      throw new IllegalArgumentException("Children must be not null!");
    }

    Origin.checkForCycles(null, new HashSet<>(children));

    this.children = new HashSet<>(children);
  }

  public Set<Origin> getChildren() {
    return Collections.unmodifiableSet(children);
  }

  public Coord2D getPosition() {
    return position;
  }

  public void setPosition(Coord2D position) {
    if (position == null) {
      throw new IllegalArgumentException("Position must be not null!");
    }
    this.position = position;
  }

  public Optional<BoundBox> getBoundBox() {
    return Origin.getBoundBoxOf(position, new HashSet<>(children));
  }
}
