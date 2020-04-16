package io.atlassian.fugue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class TrySuccessTest {

  private static final Integer STARTING_VALUE = 1;
  private static final Integer ANOTHER_VALUE = 99;
  private final Try<Integer> t = Checked.now(() -> STARTING_VALUE);
  private final Function<Integer, String> f = Object::toString;
  private final Function<String, Integer> g = Integer::valueOf;
  private final Checked.Function<Integer, String, Exception> fChecked = Object::toString;

  private final Function<Integer, String> fThrows = x -> {
    throw new TestException();
  };

  private final Function<Integer, Try<String>> fTryThrows = x -> {
    throw new TestException();
  };

  @Test public void isFailure() {
    assertThat(t.isFailure(), is(false));
  }

  @Test public void isSuccess() {
    assertThat(t.isSuccess(), is(true));
  }

  @Test public void map() {
    assertThat(t.map(f).map(g), is(t));
  }

  @Test public void mapThrowingFunctionRetunsFailure() {

    Try<Integer> result = t.map(fThrows).map(g);

    assertThat(result.isFailure(), is(true));
  }

  @Test public void flatMap() {
    Try<String> t2 = t.flatMap(i -> Checked.now(() -> fChecked.apply(i)));

    assertThat(t2, is(Checked.now(() -> "1")));
  }

  @Ignore @Test public void flatMapThrowingFunctionThrows() {

    ThrowingRunnable tr = new ThrowingRunnableImpl() {};

    org.junit.Assert.assertThrows(RuntimeException.class, tr);

    t.flatMap(fTryThrows);
  }

  @Test public void recover() {
    assertThat(t.recover(e -> 1), is(t));
  }

  @Test public void recoverExceptionType() {
    assertThat(t.recover(Exception.class, e -> 1), is(t));
  }

  @Test public void recoverWith() {
    assertThat(t.recoverWith(e -> Checked.now(() -> 1)), is(t));
  }

  @Test public void recoverWithExceptionType() {
    assertThat(t.recoverWith(Exception.class, e -> Checked.now(() -> 1)), is(t));
  }

  @Test public void getOrElse() {
    assertThat(t.getOrElse(() -> 1), is(STARTING_VALUE));
  }

  @Test public void fold() {
    Integer i = t.fold(v -> {
      throw new TestException();
    }, identity());

    assertThat(i, is(STARTING_VALUE));
  }

  @Ignore @Test public void foldPassedThrowingFunctionThrows() {

    ThrowingRunnable tr = new ThrowingRunnableImpl() {};

    org.junit.Assert.assertThrows(RuntimeException.class, tr);

    t.fold(x -> "x", fThrows);
  }

  @Test public void toEither() {
    assertThat(t.toEither(), is(Either.right(STARTING_VALUE)));
  }

  @Test public void toOption() {
    assertThat(t.toOption(), is(Option.some(STARTING_VALUE)));
  }

  @Test public void liftingFunctionReturnsSuccessIfNoExceptionThrow() {
    Try<Integer> result = Checked.lift(String::length).apply("test");

    assertThat(result.isSuccess(), is(true));

    final int val = result.fold(f -> {
      throw new NoSuchElementException();
    }, identity());
    assertThat(val, is(4));
  }

  @Test public void toOptional() {
    assertThat(t.toOptional(), is(Optional.of(STARTING_VALUE)));
  }

  @Test public void toStream() {
    final Stream<Integer> stream = t.toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), contains(STARTING_VALUE));
  }

  @Test public void forEach() {
    final AtomicInteger invoked = new AtomicInteger(STARTING_VALUE);
    t.forEach(v -> invoked.set(ANOTHER_VALUE + v));

    assertThat(invoked.get(), is(STARTING_VALUE + ANOTHER_VALUE));
  }

  @Test public void orElseSuccessInstance() {
    final Try<Integer> orElse = t.orElse(Try.successful(ANOTHER_VALUE));
    assertThat(orElse, is(t));
  }

  @Test public void orElseSuccessSupplier() {
    final Try<Integer> orElse = t.orElse(() -> Try.successful(ANOTHER_VALUE));
    assertThat(orElse, is(t));
  }

  @Test public void orElseFailureInstance() {
    final Try<Integer> orElse = t.orElse(Try.failure(new TestException()));
    assertThat(orElse, is(t));
  }

  @Test public void orElseFailureSupplier() {
    final Try<Integer> orElse = t.orElse(() -> Try.failure(new TestException()));
    assertThat(orElse, is(t));
  }

  @Test public void filterOrElseTrueSuccessful() {
    final Try<Integer> filter = t.filterOrElse(value -> Objects.equals(value, STARTING_VALUE), TestException::new);
    assertThat(filter, is(t));
  }

  @Test public void filterOrElseFalseFailure() {
    TestException testException = new TestException();
    final Try<Integer> filter = t.filterOrElse(value -> !Objects.equals(value, STARTING_VALUE), () -> testException);
    assertThat(filter, is(Try.failure(testException)));
  }

  @Test public void iteratorNotEmpty() {
    Iterator<Integer> iterator = t.iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), is(STARTING_VALUE));
    assertThat(iterator.hasNext(), is(false));
  }
}
