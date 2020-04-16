package io.atlassian.fugue;

import java.util.Objects;

/**
 * @author glick
 */
public class TestException extends RuntimeException {

  public TestException() {
    super();
  }

  public TestException(final String message) {
    super(message);
  }

  @Override public int hashCode() {
    return Objects.hashCode(getMessage());
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TestException)) {
      return false;
    }

    TestException other = (TestException) obj;
    return Objects.equals(getMessage(), other.getMessage());
  }
}
