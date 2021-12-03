package dags;

import java.io.Serial;

public class DAGConstraintException extends Exception {

  DAGConstraintException(String message) {
    super(message);
  }

  @Serial
  private static final long serialVersionUID = -8503389845293388237L;
}
