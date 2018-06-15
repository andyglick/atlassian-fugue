package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static io.atlassian.fugue.Unit.Unit;
import static io.atlassian.fugue.hamcrest.EitherMatchers.isLeft;
import static io.atlassian.fugue.hamcrest.EitherMatchers.isRight;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestEitherSteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final int INTEGER = 123;
  private static final Long LONGLONG = 123456123456L;

  @Test public void test_1_step_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).yield(Long::new);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_1_step_failure() {
    Either<AnError, Long> stepped = Steps.begin(TestEitherSteps.<String> error(AnError.FIRST)).yield(Long::new);

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_2_step_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).yield((str, integer) -> new Long(str + integer));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_2_step_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(str -> error(AnError.SECOND)).yield((str, integer) -> new Long(str + integer));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_3_step_success() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(false))
      .yield((string, integer, boo) -> string + integer + boo);

    assertThat(stepped, isRight(is(STRING + LONG + false)));
  }

  @Test public void test_3_step_failure() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> error(AnError.THIRD))
      .yield((string, integer, boo) -> string + integer + boo);

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_4_step_success() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> ok(STRING_UPPERED.toLowerCase())).yield((string, integer, boo, string2) -> string + integer + boo + string2);

    assertThat(stepped, isRight(is(STRING + LONG + true + STRING_LOWERED)));
  }

  @Test public void test_4_step_failure() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> error(AnError.FOURTH)).yield((string, integer, boo, string2) -> string + integer + boo + string2);

    assertThat(stepped, isLeft(is(AnError.FOURTH)));
  }

  @Test public void test_5_step_success() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> ok(STRING_UPPERED)).then((string, aLong, aBoolean, string2) -> ok(LONG / 2))
      .yield((string, integer, boo, string2, ll) -> string + integer + boo + string2 + ll);

    assertThat(stepped, isRight(is(STRING + LONG + true + STRING_UPPERED + (LONG / 2))));
  }

  @Test public void test_5_step_failure() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> ok(STRING_UPPERED)).then((string, aLong, aBoolean, string2) -> error(AnError.FIFTH))
      .yield((string, integer, boo, string2, ll) -> string + integer + boo + string2 + ll);

    assertThat(stepped, isLeft(is(AnError.FIFTH)));
  }

  @Test public void test_6_step_success() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> ok(STRING_LOWERED)).then((string, aLong, aBoolean, string2) -> ok(LONG / 2))
      .then((string, aLong, aBoolean, string2, long5) -> ok(INTEGER * 2))
      .yield((string, integer, boo, string2, ll, i) -> string + integer + boo + string2 + ll + i);

    assertThat(stepped, isRight(is(STRING + LONG + true + STRING_LOWERED + (LONG / 2) + (INTEGER * 2))));
  }

  @Test public void test_6_step_failure() {
    Either<AnError, Unit> stepped = Steps.begin(ok(STRING)).then(str -> ok(LONG)).then((string, aLong) -> ok(true))
      .then((string, aLong, aBoolean) -> ok(STRING_UPPERED)).then((string, aLong, aBoolean, string2) -> ok(LONG))
      .then((string, aLong, aBoolean, string2, long5) -> error(AnError.SIXTH)).yield((string, integer, boo, string2, ll, sixth) -> Unit());

    assertThat(stepped, isLeft(is(AnError.SIXTH)));
  }

  private static <T> Either<AnError, T> error(AnError anError) {
    return left(anError);
  }

  private static <T> Either<AnError, T> ok(T value) {
    return right(value);
  }

}
