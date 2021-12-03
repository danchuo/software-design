package dags;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DAGConstraintExceptionTest {

  @Test
  void dagConstraintException() {
    var exception = new DAGConstraintException("Exception.");
    assertEquals(exception.getMessage(), "Exception.");
  }
}