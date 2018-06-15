package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Objects;

import static io.atlassian.fugue.Serializer.toBytes;
import static io.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TrySerializationTest {
  @Test public void serializeFailure() throws IOException {
    final Try<String> e = Try.failure(new EqualityException("Error message"));
    assertThat(Serializer.toObject(toBytes(e)), equalTo(e));
  }

  @Test public void serializeSuccess() throws IOException {
    final Try<Integer> e = Try.successful(1);
    assertThat(Serializer.toObject(toBytes(e)), equalTo(e));
  }

  @Test public void serializeDelayedFailure() throws IOException {
    final Try<String> e = Try.delayed(() -> Try.failure(new EqualityException("Delay This Error")));
    assertThat(Serializer.toObject(toBytes(e)), equalTo(e));
  }

  @Test public void serializeDelayedSuccess() throws IOException {
    final Try<String> e = Try.delayed(() -> Try.successful("Delay This Message"));
    assertThat(Serializer.toObject(toBytes(e)), equalTo(e));
  }

  @Test(expected = NotSerializableException.class) public void serializeSuccessNonSerializable() throws IOException {
    toObject(toBytes(Try.successful(Serializer.Unserializable.instance())));
  }

  @Test(expected = NotSerializableException.class) public void serializeDelayedSuccessNonSerializable() throws IOException {
    toObject(toBytes(Try.delayed(() -> Try.successful(Serializer.Unserializable.instance()))));
  }

  private static class EqualityException extends RuntimeException {
    private static final long serialVersionUID = 4644995396280333232L;

    EqualityException(String message) {
      super(message);
    }

    @Override public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      final EqualityException equalityException = (EqualityException) o;
      return Objects.equals(getMessage(), equalityException.getMessage());
    }

    @Override public int hashCode() {
      return getMessage().hashCode();
    }
  }
}
