package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Objects;

import static io.atlassian.fugue.Serializer.toBytes;
import static io.atlassian.fugue.Serializer.toObject;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class TrySerializationTest {
  @Test public void serializeFailure() throws IOException {
    final Try<String> t = Try.failure(new EqualityException("Error message"));
    assertThat(Serializer.toObject(toBytes(t)), equalTo(t));
  }

  @Test public void serializeSuccess() throws IOException {
    final Try<Integer> t = Try.successful(1);
    assertThat(Serializer.toObject(toBytes(t)), equalTo(t));
  }

  @Test public void serializeDelayedFailure() throws IOException {
    final Try<String> t = Try.delayed(() -> Try.failure(new EqualityException("Delay This Error")));
    final Try<String> deserialized = Serializer.toObject(toBytes(t));

    assertThat("Try.Delayed has not implemented .equals() so deserialized object should not be equal", deserialized, not(equalTo(t)));
    assertThat("Evaluating a Try.Delayed into Either will have equal content", deserialized.toEither(), equalTo(t.toEither()));
    assertThat("Evaluated Try.Delayed still not actually equal", deserialized, not(equalTo(t)));
  }

  @Test public void serializeDelayedSuccess() throws IOException {
    final Try<String> t = Try.delayed(() -> Try.successful("Delay This Message"));
    final Try<String> deserialized = Serializer.toObject(toBytes(t));

    assertThat("Try.Delayed has not implemented .equals() so deserialized object should not be equal", deserialized, not(equalTo(t)));
    assertThat("Evaluating a Try.Delayed into Either will have equal content", deserialized.toEither(), equalTo(t.toEither()));
    assertThat("Evaluated Try.Delayed still not actually equal", deserialized, not(equalTo(t)));
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
