package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryFailureTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private class TestException extends RuntimeException {
    TestException(final String message) {
      super(message);
    }
  }

  private static final String MESSAGE = "known exception message";
  private final Try<Integer> t = Try.of(() -> {
    throw new TestException(MESSAGE);
  });
  private final Function<Exception, String> fThrows = x -> {
    throw new TestException(MESSAGE);
  };

  private final Function<Exception, Try<Integer>> fTryThrows = x -> {
    throw new RuntimeException();
  };

  @Test public void map() throws Exception {
    assertThat(t.map(x -> true), is(t));
  }

  @Test public void isFailure() throws Exception {
    assertThat(t.isFailure(), is(true));
  }

  @Test public void isSuccess() throws Exception {
    assertThat(t.isSuccess(), is(false));
  }

  @Test public void flatMap() throws Exception {
    assertThat(t.map(x -> true), is(t));
  }

  @Test public void recover() throws Exception {
    assertThat(t.recover(x -> 0), is(Try.of(() -> 0)));
  }

  @Test public void recoverWith() throws Exception {
    assertThat(t.recoverWith(x -> Try.of(() -> 0)), is(Try.of(() -> 0)));
  }

  @Test public void recoverWithPassedThrowingFunctionThrows() throws Exception {
    thrown.expect(RuntimeException.class);

    t.recoverWith(fTryThrows);
  }

  @Test public void getOrElse() throws Exception {
    assertThat(t.getOrElse(() -> 0), is(0));
  }

  @Test public void fold() throws Exception {
    Exception e = t.fold(identity(), v -> {
      throw new RuntimeException();
    });

    assertThat(e, instanceOf(TestException.class));
    assertThat(e.getMessage(), is(MESSAGE));
  }

  @Test public void foldPassedThrowingExceptionsThrows() throws Exception {
    thrown.expect(TestException.class);

    String s = t.fold(fThrows, x -> "x");
  }

  @Test public void toEither() throws Exception {
    final Either<Exception, Integer> e = t.toEither();

    assertThat(e.isLeft(), is(true));
    assertThat(e.left().get(), instanceOf(TestException.class));
    assertThat(e.left().get().getMessage(), is(MESSAGE));
  }

  @Test public void toOption() throws Exception {
    assertThat(t.toOption(), is(Option.none()));
  }

  @Test public void liftingFunctionThatThrowsReturnsFailure() {
    Try<Integer> result = Try.<String, Integer, TestException> lift(x -> {
      throw new TestException(MESSAGE);
    }).apply("test");

    assertThat(result.isFailure(), is(true));

    final Exception e = result.fold(identity(), x -> {
      throw new NoSuchElementException();
    });
    assertThat(e, instanceOf(TestException.class));
    assertThat(e.getMessage(), is(MESSAGE));
  }

}