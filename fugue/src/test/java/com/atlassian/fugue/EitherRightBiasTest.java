package com.atlassian.fugue;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.EitherRightProjectionTest.reverseToEither;
import static com.atlassian.fugue.UtilityFunctions.addOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class EitherRightBiasTest {
  private final Either<String, Integer> l = left("heyaa!");
  private final Either<String, Integer> r = right(12);

  @Test public void mapRight() {
    assertThat(Either.<String, Integer> right(3).map(addOne), is(Either.<String, Integer> right(4)));
  }

  @Test public void mapLeft() {
    assertThat(Either.<String, Integer> left("foo").map(addOne), is(Either.<String, Integer> left("foo")));
  }

  @Test public void flatMapRight() {
    assertThat(Either.<Integer, String> right("!foo").flatMap(reverseToEither), is(Either.<Integer, String> right("oof!")));
  }

  @Test public void flatMapLeft() {
    assertThat(Either.<Integer, String> left(5).flatMap(reverseToEither), is(Either.<Integer, String> left(5)));
  }

  @Test public void leftMapRight() {
    assertThat(Either.<Integer, String> right("foo").leftMap(addOne), is(Either.<Integer, String> right("foo")));
  }

  @Test public void leftMapLeft() {
    assertThat(Either.<Integer, String> left(3).leftMap(addOne), is(Either.<Integer, String> left(4)));
  }

  @Test public void getOrElseSupplierRight() {
    assertThat(r.getOrElse(Suppliers.ofInstance(1)), is(12));
  }

  @Test public void getOrElseSupplierLeft() {
    assertThat(l.getOrElse(Suppliers.ofInstance(1)), is(1));
  }

  @Test public void getOrElseRight() {
    assertThat(r.getOrElse(1), is(12));
  }

  @Test public void getOrElseLeft() {
    assertThat(l.getOrElse(1), is(1));
  }

  @Test public void getOrNullRight() {
    assertThat(r.getOrNull(), is(12));
  }

  @Test public void getOrNullLeft() {
    assertNull(l.getOrNull());
  }

  @Test public void getOrErrorRight() {
    assertThat(r.getOrError(Suppliers.ofInstance("Error message")), is(12));
  }

  @Test(expected = AssertionError.class) public void getOrErrorLeft() {
    l.getOrError(Suppliers.ofInstance("Error message"));
  }

  @Test public void getOrErrorLeftMessage() {
    try {
      l.getOrError(Suppliers.ofInstance("Error message"));
    } catch (final Error e) {
      assertThat(e.getMessage(), is("Error message"));
      return;
    }

    fail("No error thrown");
  }

  @Test public void getOrThrowRight() {
    assertThat(r.getOrThrow(Suppliers.ofInstance(new RuntimeException("Run Error"))), is(12));
  }

  private class CustomException extends RuntimeException {
    private static final long serialVersionUID = -633224822465345980L;
  }

  @Test(expected = CustomException.class) public void getOrThrowLeft() {
    l.getOrThrow(Suppliers.ofInstance(new CustomException()));
  }

  @Test public void existsRight() {
    assertThat(r.exists(x -> x == 12), is(true));
    assertThat(r.exists(x -> x == 11), is(false));
  }

  @Test public void existsLeft() {
    assertThat(l.exists(x -> x == 12), is(false));
  }

  @Test public void forallRight() {
    assertThat(r.forall(x -> x == 12), is(true));
    assertThat(r.forall(x -> x == 11), is(false));
  }

  @Test public void forallLeft() {
    assertThat(l.forall(x -> x == 12), is(true));
  }

  @Test public void foreachRight() {
    final AtomicBoolean called = new AtomicBoolean(false);
    final Effect<Integer> effect = integer -> called.set(true);

    r.foreach(effect);

    assertThat(called.get(), is(true));
  }

  @Test public void foreachLeft() {
    final AtomicBoolean called = new AtomicBoolean(false);
    final Effect<Integer> effect = integer -> called.set(true);

    l.foreach(effect);

    assertThat(called.get(), is(false));
  }

  @Test public void filterRight() {
    assertThat(r.filter(x -> x == 12), is(Option.some(r)));
    assertThat(r.filter(x -> x == 11), Matchers.is(Option.<Either<String, Integer>> none()));
  }

  @Test public void filterLeft() {
    assertThat(l.filter(x -> x == 12), Matchers.is(Option.<Either<String, Integer>> none()));
  }

  @Test public void orElseRightInstance() {
    assertThat(r.orElse(Either.<String, Integer> right(44)), is(r));
  }

  @Test public void orElseLeftInstance() {
    assertThat(l.orElse(Either.<String, Integer> right(44)), is(Either.<String, Integer> right(44)));
    assertThat(l.orElse(Either.<String, Integer> left("left")), is(Either.<String, Integer> left("left")));
  }

  @Test public void orElseRightSupplier() {
    assertThat(r.orElse(Suppliers.ofInstance(Either.<String, Integer> right(44))), is(r));
  }

  @Test public void orElseLeftSupplier() {
    assertThat(l.orElse(Suppliers.ofInstance(Either.<String, Integer> right(44))), is(Either.<String, Integer> right(44)));
    assertThat(l.orElse(Suppliers.ofInstance(Either.<String, Integer> left("left"))), is(Either.<String, Integer> left("left")));
  }

  @Test public void orElseChild() {
    class Parent {}
    class Child extends Parent {}

    final Parent p = new Parent();
    final Parent pp = right(p).orElse(Suppliers.ofInstance(Either.<Integer, Child> right(new Child()))).getOrNull();
    assertThat(pp, is(p));
  }

  @Test public void valueOrRight() {
    assertThat(r.valueOr(Functions.<String, Integer> constant(99)), is(12));
  }

  @Test public void valueOrLeft() {
    assertThat(l.valueOr(Functions.<String, Integer> constant(99)), is(99));
  }

  @Test public void flatMapSubTypesOnLeft() {
    class ErrorType {}
    class AnotherErrorType extends ErrorType {}

    final AnotherErrorType anotherErrorType = new AnotherErrorType();
    final Either<AnotherErrorType, Long> l = Either.left(anotherErrorType);

    final Either<ErrorType, Long> longEither = Either.<ErrorType, Integer> right(1).flatMap(input -> l);

    final ErrorType errorType = longEither.left().get();

    assertThat(errorType, Matchers.<ErrorType> is(anotherErrorType));
  }

  @Test public void flatMapWithUpcastAndSubtypesOnLeft() {
    class ErrorType {}
    class MyErrorType extends ErrorType {}
    class AnotherErrorType extends ErrorType {}

    final AnotherErrorType anotherErrorType = new AnotherErrorType();

    final Either<MyErrorType, Boolean> l = Either.right(true);
    final Either<AnotherErrorType, Long> l2 = Either.left(anotherErrorType);

    final Either<ErrorType, Long> either = Eithers.<ErrorType, MyErrorType, Boolean> upcastLeft(l).flatMap(input -> l2);

    final ErrorType errorType = either.left().get();

    assertThat(errorType, Matchers.<ErrorType> is(anotherErrorType));
  }
}
