package dags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Origin extends Point {

  private Set<Point> children;

  Origin(Coord2D position) {
    super(position);
    children = new HashSet<>();
  }

  public Set<Point> getChildren() {
    return Collections.unmodifiableSet(children);
  }

  public void setChildren(Set<Point> children) {
    var newChildren = new HashSet<Point>();

    for (var child : children) {
      newChildren.add(child.clone());
    }
    this.children = newChildren;
  }

  @Override
  public BoundBox getBoundBox() {
    throw new NotImplementedException("boundbox");
  }

  @Override
  public Origin clone() {
    Origin result = new Origin(getPosition());
    for (var point : children) {
      result.children.add(point.clone());
    }
    return result;
  }
}
