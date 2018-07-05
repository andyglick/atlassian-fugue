package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryFailureTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private final Integer ANOTHER_VALUE = 99;

  private class TestException extends RuntimeException {
    TestException(final String message) {
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

  private static final String MESSAGE = "known exception message";
  private final Try<Integer> t = Checked.now(() -> {
    throw new TestException(MESSAGE);
  });
  private final Function<Exception, String> fThrows = x -> {
    throw new TestException(MESSAGE);
  };

  private final Function<Exception, Try<Integer>> fTryThrows = x -> {
    throw new RuntimeException();
  };

  @Test public void map() {
    assertThat(t.map(x -> true), is(t));
  }

  @Test public void isFailure() {
    assertThat(t.isFailure(), is(true));
  }

  @Test public void isSuccess() {
    assertThat(t.isSuccess(), is(false));
  }

  @Test public void flatMap() {
    assertThat(t.map(x -> true), is(t));
  }

  @Test public void recover() {
    assertThat(t.recover(x -> 0), is(Checked.now(() -> 0)));
  }

  @Test public void recoverMatchingException() {
    assertThat(t.recover(TestException.class, x -> 0), is(t.recover(x -> 0)));
  }

  @Test public void recoverMismatchingException() {
    assertThat(t.recover(IllegalStateException.class, x -> 0), is(t));
  }

  @Test public void recoverWith() {
    assertThat(t.recoverWith(x -> Checked.now(() -> 0)), is(Checked.now(() -> 0)));
  }

  @Test public void recoverWithMatchingException() {
    assertThat(t.recoverWith(TestException.class, x -> Checked.now(() -> 0)), is(t.recoverWith(x -> Checked.now(() -> 0))));
  }

  @Test public void recoverWithMismatchingException() {
    assertThat(t.recoverWith(IllegalStateException.class, x -> Checked.now(() -> 0)), is(t));
  }

  @Test public void recoverWithPassedThrowingFunctionThrows() {
    thrown.expect(RuntimeException.class);

    t.recoverWith(fTryThrows);
  }

  @Test public void getOrElse() {
    assertThat(t.getOrElse(() -> 0), is(0));
  }

  @Test public void fold() {
    Exception e = t.fold(identity(), v -> {
      throw new RuntimeException();
    });

    assertThat(e, instanceOf(TestException.class));
    assertThat(e.getMessage(), is(MESSAGE));
  }

  @Test public void foldPassedThrowingExceptionsThrows() {
    thrown.expect(TestException.class);

    t.fold(fThrows, x -> "x");
  }

  @Test public void toEither() {
    final Either<Exception, Integer> e = t.toEither();

    assertThat(e.isLeft(), is(true));
    assertThat(e.left().get(), instanceOf(TestException.class));
    assertThat(e.left().get().getMessage(), is(MESSAGE));
  }

  @Test public void toOption() {
    assertThat(t.toOption(), is(Option.none()));
  }

  @Test public void liftingFunctionThatThrowsReturnsFailure() {
    Try<Integer> result = Checked.<String, Integer, TestException> lift(x -> {
      throw new TestException(MESSAGE);
    }).apply("test");

    assertThat(result.isFailure(), is(true));

    final Exception e = result.fold(identity(), x -> {
      throw new NoSuchElementException();
    });
    assertThat(e, instanceOf(TestException.class));
    assertThat(e.getMessage(), is(MESSAGE));
  }

  @Test public void toOptional() {
    assertThat(t.toOption(), is(Option.none()));
  }

  @Test public void toStream() {
    final Stream<Integer> stream = t.toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), emptyIterable());
  }

  @Test public void forEach() {
    final AtomicBoolean invoked = new AtomicBoolean(false);
    t.forEach(v -> invoked.set(true));

    assertThat(invoked.get(), is(false));
  }

  @Test public void orElseSuccessInstance() {
    Try<Integer> successful = Try.successful(ANOTHER_VALUE);
    final Try<Integer> orElse = t.orElse(successful);
    assertThat(orElse, is(successful));
  }

  @Test public void orElseSuccessSupplier() {
    Try<Integer> successful = Try.successful(ANOTHER_VALUE);
    final Try<Integer> orElse = t.orElse(() -> successful);
    assertThat(orElse, is(successful));
  }

  @Test public void orElseFailureInstance() {
    Try<Integer> failure = Try.failure(new TestException("Failure instance"));
    final Try<Integer> orElse = t.orElse(failure);
    assertThat(orElse, is(failure));
  }

  @Test public void orElseFailureSupplier() {
    Try<Integer> failure = Try.failure(new TestException("Failure instance"));
    final Try<Integer> orElse = t.orElse(() -> failure);
    assertThat(orElse, is(failure));
  }

  @Test public void filterOrElseTrue() {
    final Try<Integer> filter = t.filterOrElse(value -> true, () -> new TestException("this is a different error"));
    assertThat(filter, is(t));
  }

  @Test public void filterOrElseFalseFailure() {
    final Try<Integer> filter = t.filterOrElse(value -> false, () -> new TestException("this is a different error"));
    assertThat(filter, is(t));
  }

}