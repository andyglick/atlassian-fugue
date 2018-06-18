package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import java.util.function.Supplier;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static io.atlassian.fugue.Unit.Unit;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse2;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse3;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse4;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse5;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysFalse6;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue2;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue3;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue4;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue5;
import static io.atlassian.fugue.extensions.step.TestUtils.alwaysTrue6;
import static io.atlassian.fugue.hamcrest.EitherMatchers.isLeft;
import static io.atlassian.fugue.hamcrest.EitherMatchers.isRight;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestEitherSteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final Long LONGLONG = 123456123456L;
  private static final Supplier<AnError> filterAnErrorSupplier = () -> AnError.FILTER;

  @Test public void test_1_step_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).yield(Long::new);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_1_step_failure() {
    Either<AnError, Unit> stepped = Steps.begin(error(AnError.FIRST)).yield(value1 -> Unit());

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_1_step_filter_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysTrue(), filterAnErrorSupplier).yield(Long::new);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_1_step_filter_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).yield(Long::new);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_2_step_success_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then((firstValue) -> ok(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_2_step_success_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_2_step_failure_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> error(AnError.SECOND)).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_2_step_failure_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> error(AnError.SECOND)).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_2_step_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(error(AnError.FIRST)).then(() -> ok(9)).yield((value1, value2) -> new Long(value2));

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_2_step_filter_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).filter(alwaysTrue2(), filterAnErrorSupplier).yield((v1, v2) -> v2);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_2_step_filter_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).filter(alwaysFalse2(), filterAnErrorSupplier).yield((v1, v2) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_2_step_filter_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).then(() -> ok(LONG)).yield((v1, v2) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_3_step_success_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((v1, v2) -> ok(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_3_step_success_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_3_step_failure_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((v1, v2) -> error(AnError.THIRD))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_3_step_failure_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> error(AnError.THIRD))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_3_step_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(error(AnError.FIRST)).then(() -> ok(STRING)).then((v1, v2) -> ok(LONG))
      .yield((value1, value2, value3) -> value3);

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_3_step_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> error(AnError.SECOND)).then((v1, v2) -> ok(LONG))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_3_step_filter_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).then(() -> ok(STRING_LOWERED))
      .filter(alwaysTrue3(), filterAnErrorSupplier).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_3_step_filter_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).then(() -> ok(STRING_LOWERED))
      .filter(alwaysFalse3(), filterAnErrorSupplier).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_3_step_filter_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).then(() -> ok(LONG))
      .then(() -> ok(STRING_LOWERED)).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_3_step_filter_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).filter(alwaysFalse2(), filterAnErrorSupplier)
      .then(() -> ok(STRING_LOWERED)).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_4_step_success_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_4_step_success_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_4_step_failure_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((s, s2) -> ok(STRING))
      .then((s, s2, s3) -> error(AnError.FOURTH)).yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.FOURTH)));
  }

  @Test public void test_4_step_failure_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING)).then(() -> error(AnError.FOURTH))
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isLeft(is(AnError.FOURTH)));
  }

  @Test public void test_4_step_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(error(AnError.FIRST)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_4_step_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> error(AnError.SECOND)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_4_step_failure_3() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> error(AnError.THIRD)).then(() -> ok(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value4));

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_4_step_filter_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).then(() -> ok(STRING_LOWERED)).then(() -> ok(true))
      .filter(alwaysTrue4(), filterAnErrorSupplier).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isRight(is(LONG)));
  }

  @Test public void test_4_step_filter_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).then(() -> ok(STRING_LOWERED)).then(() -> ok(true))
      .filter(alwaysFalse4(), filterAnErrorSupplier).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_4_step_filter_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).then(() -> ok(LONG))
      .then(() -> ok(STRING_LOWERED)).then(() -> ok(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_4_step_filter_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).filter(alwaysFalse2(), filterAnErrorSupplier)
      .then(() -> ok(STRING_LOWERED)).then(() -> ok(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_4_step_filter_failure_3() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(LONG)).then(() -> ok(STRING_LOWERED))
      .filter(alwaysFalse3(), filterAnErrorSupplier).then(() -> ok(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_5_step_success_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_5_step_success_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_5_step_failure_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> error(AnError.FIFTH))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FIFTH)));
  }

  @Test public void test_5_step_failure_supplier() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> error(AnError.FIFTH)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FIFTH)));
  }

  @Test public void test_5_step_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(error(AnError.FIRST)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_5_step_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> error(AnError.SECOND)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_5_step_failure_3() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> error(AnError.THIRD)).then(() -> ok(STRING))
      .then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value2));

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_5_step_failure_4() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> error(AnError.FOURTH))
      .then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FOURTH)));
  }

  @Test public void test_5_step_filter_success() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING)).filter(alwaysTrue5(), filterAnErrorSupplier)
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_5_step_filter_failure() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING)).filter(alwaysFalse5(), filterAnErrorSupplier)
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_5_step_filter_failure_1() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).then(v -> ok(STRING))
      .then((first, second) -> ok(first + second)).then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_5_step_filter_failure_2() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).filter(alwaysFalse2(), filterAnErrorSupplier)
      .then((first, second) -> ok(first + second)).then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_5_step_filter_failure_3() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .filter(alwaysFalse3(), filterAnErrorSupplier).then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_5_step_filter_failure_4() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).filter(alwaysFalse4(), filterAnErrorSupplier).then((v1, v2, v3, v4) -> ok(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_success_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isRight(is(LONGLONG)));
  }

  @Test public void test_6_step_success_supplier() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isRight(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_failure_functor() {
    Either<AnError, Long> stepped = Steps.begin(ok(STRING)).then(v -> ok(STRING)).then((first, second) -> ok(first + second))
      .then((v1, v2, v3) -> ok(STRING)).then((v1, v2, v3, v4) -> ok(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> error(AnError.SIXTH))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isLeft(is(AnError.SIXTH)));
  }

  @Test public void test_6_step_failure_supplier() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> error(AnError.SIXTH)).yield((value1, value2, value3, value4, value5, value6) -> value5);

    assertThat(stepped, isLeft(is(AnError.SIXTH)));
  }

  @Test public void test_6_step_failure_1() {
    Either<AnError, String> stepped = Steps.begin(error(AnError.FIRST)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FIRST)));
  }

  @Test public void test_6_step_failure_2() {
    Either<AnError, String> stepped = Steps.begin(ok(LONG)).then(() -> error(AnError.SECOND)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.SECOND)));
  }

  @Test public void test_6_step_failure_3() {
    Either<AnError, String> stepped = Steps.begin(ok(LONG)).then(() -> ok(STRING)).then(() -> error(AnError.THIRD)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.THIRD)));
  }

  @Test public void test_6_step_failure_4() {
    Either<AnError, String> stepped = Steps.begin(ok(LONG)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> error(AnError.FOURTH))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FOURTH)));
  }

  @Test public void test_6_step_failure_5() {
    Either<AnError, String> stepped = Steps.begin(ok(LONG)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> error(AnError.FIFTH)).then(() -> ok(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FIFTH)));
  }

  @Test public void test_6_step_filter_success() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).filter(alwaysTrue6(), filterAnErrorSupplier)
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isRight(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_filter_failure() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED)).filter(alwaysFalse6(), filterAnErrorSupplier)
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_filter_failure_1() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).filter(alwaysFalse(), filterAnErrorSupplier).then(() -> ok(STRING))
      .then(() -> ok(STRING + STRING)).then(() -> ok(STRING)).then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_filter_failure_2() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).filter(alwaysFalse2(), filterAnErrorSupplier)
      .then(() -> ok(STRING + STRING)).then(() -> ok(STRING)).then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_filter_failure_3() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING))
      .filter(alwaysFalse3(), filterAnErrorSupplier).then(() -> ok(STRING)).then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_filter_failure_4() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .filter(alwaysFalse4(), filterAnErrorSupplier).then(() -> ok(STRING_UPPERED)).then(() -> ok(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  @Test public void test_6_step_filter_failure_5() {
    Either<AnError, String> stepped = Steps.begin(ok(STRING)).then(() -> ok(STRING)).then(() -> ok(STRING + STRING)).then(() -> ok(STRING))
      .then(() -> ok(STRING_UPPERED)).filter(alwaysFalse5(), filterAnErrorSupplier).then(() -> ok(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isLeft(is(AnError.FILTER)));
  }

  private static <T> Either<AnError, T> error(AnError anError) {
    return left(anError);
  }

  private static <T> Either<AnError, T> ok(T value) {
    return right(value);
  }

  public enum AnError {

    FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, FILTER

  }

}
