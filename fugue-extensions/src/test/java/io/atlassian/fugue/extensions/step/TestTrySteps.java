package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import java.util.function.Supplier;

import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
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
import static io.atlassian.fugue.hamcrest.TryMatchers.isFailure;
import static io.atlassian.fugue.hamcrest.TryMatchers.isSuccessful;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestTrySteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final Long LONGLONG = 123456123456L;
  private static final Exception exceptionFilter = new Exception();
  private static final Exception exception1 = new Exception();
  private static final Exception exception2 = new Exception();
  private static final Exception exception3 = new Exception();
  private static final Exception exception4 = new Exception();
  private static final Exception exception5 = new Exception();
  private static final Exception exception6 = new Exception();
  private static final Supplier<Exception> exceptionFilterFunction = () -> exceptionFilter;

  @Test public void test_1_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).yield(Long::new);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_1_step_failure() {
    Try<Unit> stepped = Steps.begin(failure(exception1)).yield(value1 -> Unit());

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_1_step_filter_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysTrue(), exceptionFilterFunction).yield(Long::new);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_1_step_filter_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).yield(Long::new);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_2_step_success_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then((firstValue) -> successful(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_2_step_success_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_2_step_failure_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> failure(exception2)).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_2_step_failure_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> failure(exception2)).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_2_step_failure_1() {
    Try<Long> stepped = Steps.begin(failure(exception1)).then(() -> successful(9)).yield((value1, value2) -> new Long(value2));

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_2_step_filter_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).filter(alwaysTrue2(), exceptionFilterFunction)
      .yield((v1, v2) -> v2);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_2_step_filter_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).filter(alwaysFalse2(), exceptionFilterFunction)
      .yield((v1, v2) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_2_step_filter_failure_1() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).then(() -> successful(STRING))
      .yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_3_step_success_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((v1, v2) -> successful(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_3_step_success_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_3_step_failure_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((v1, v2) -> failure(exception3))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception3)));
  }

  @Test public void test_3_step_failure_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> failure(exception3))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception3)));
  }

  @Test public void test_3_step_failure_1() {
    Try<Long> stepped = Steps.begin(failure(exception1)).then(() -> successful(STRING)).then((v1, v2) -> successful(LONG))
      .yield((value1, value2, value3) -> value3);

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_3_step_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> failure(exception2)).then((v1, v2) -> successful(LONG))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_3_step_filter_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).then(() -> successful(STRING_LOWERED))
      .filter(alwaysTrue3(), exceptionFilterFunction).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_3_step_filter_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).then(() -> successful(STRING_LOWERED))
      .filter(alwaysFalse3(), exceptionFilterFunction).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_3_step_filter_failure_1() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).then(() -> successful(LONG))
      .then(() -> successful(STRING_LOWERED)).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_3_step_filter_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).filter(alwaysFalse2(), exceptionFilterFunction)
      .then(() -> successful(STRING_LOWERED)).filter(alwaysFalse3(), exceptionFilterFunction).yield((v1, v2, v3) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_4_step_success_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_4_step_success_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_4_step_failure_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((s, s2) -> successful(STRING))
      .then((s, s2, s3) -> failure(exception4)).yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception4)));
  }

  @Test public void test_4_step_failure_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING)).then(() -> failure(exception4))
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isFailure(is(exception4)));
  }

  @Test public void test_4_step_failure_1() {
    Try<Long> stepped = Steps.begin(failure(exception1)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_4_step_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> failure(exception2)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_4_step_failure_3() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> failure(exception3)).then(() -> successful(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value4));

    assertThat(stepped, isFailure(is(exception3)));
  }

  @Test public void test_4_step_filter_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).then(() -> successful(STRING_LOWERED))
      .then(() -> successful(true)).filter(alwaysTrue4(), exceptionFilterFunction).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_4_step_filter_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).then(() -> successful(STRING_LOWERED))
      .then(() -> successful(true)).filter(alwaysFalse4(), exceptionFilterFunction).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_4_step_filter_failure_1() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).then(() -> successful(LONG))
      .then(() -> successful(STRING_LOWERED)).then(() -> successful(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_4_step_filter_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).filter(alwaysFalse2(), exceptionFilterFunction)
      .then(() -> successful(STRING_LOWERED)).then(() -> successful(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_4_step_filter_failure_3() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(LONG)).then(() -> successful(STRING_LOWERED))
      .filter(alwaysFalse3(), exceptionFilterFunction).then(() -> successful(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_5_step_success_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_5_step_success_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_5_step_failure_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> failure(exception5))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception5)));
  }

  @Test public void test_5_step_failure_supplier() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> failure(exception5)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception5)));
  }

  @Test public void test_5_step_failure_1() {
    Try<Long> stepped = Steps.begin(failure(exception1)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_5_step_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> failure(exception2)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_5_step_failure_3() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> failure(exception3)).then(() -> successful(STRING))
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value2));

    assertThat(stepped, isFailure(is(exception3)));
  }

  @Test public void test_5_step_failure_4() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> failure(exception4)).then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception4)));
  }

  @Test public void test_5_step_filter_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING)).filter(alwaysTrue5(), exceptionFilterFunction)
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_5_step_filter_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING)).filter(alwaysFalse5(), exceptionFilterFunction)
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_5_step_filter_failure_1() {
    Try<Long> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).then(v -> successful(STRING))
      .then((first, second) -> successful(first + second)).then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_5_step_filter_failure_2() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).filter(alwaysFalse2(), exceptionFilterFunction)
      .then((first, second) -> successful(first + second)).then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_5_step_filter_failure_3() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .filter(alwaysFalse3(), exceptionFilterFunction).then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_5_step_filter_failure_4() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).filter(alwaysFalse4(), exceptionFilterFunction).then((v1, v2, v3, v4) -> successful(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_success_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_6_step_success_supplier() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isSuccessful(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_failure_functor() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(v -> successful(STRING)).then((first, second) -> successful(first + second))
      .then((v1, v2, v3) -> successful(STRING)).then((v1, v2, v3, v4) -> successful(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> failure(exception6)).yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isFailure(is(exception6)));
  }

  @Test public void test_6_step_failure_supplier() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> failure(exception6))
      .yield((value1, value2, value3, value4, value5, value6) -> value5);

    assertThat(stepped, isFailure(is(exception6)));
  }

  @Test public void test_6_step_failure_1() {
    Try<String> stepped = Steps.begin(failure(exception1)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exception1)));
  }

  @Test public void test_6_step_failure_2() {
    Try<String> stepped = Steps.begin(successful(LONG)).then(() -> failure(exception2)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exception2)));
  }

  @Test public void test_6_step_failure_3() {
    Try<String> stepped = Steps.begin(successful(LONG)).then(() -> successful(STRING)).then(() -> failure(exception3)).then(() -> successful(STRING))
      .then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exception3)));
  }

  @Test public void test_6_step_failure_4() {
    Try<String> stepped = Steps.begin(successful(LONG)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> failure(exception4)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exception4)));
  }

  @Test public void test_6_step_failure_5() {
    Try<String> stepped = Steps.begin(successful(LONG)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> failure(exception5)).then(() -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exception5)));
  }

  @Test public void test_6_step_filter_success() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .filter(alwaysTrue6(), exceptionFilterFunction).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isSuccessful(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_filter_failure() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).then(() -> successful(STRING_LOWERED))
      .filter(alwaysFalse6(), exceptionFilterFunction).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_filter_failure_1() {
    Try<String> stepped = Steps.begin(successful(STRING)).filter(alwaysFalse(), exceptionFilterFunction).then(() -> successful(STRING))
      .then(() -> successful(STRING + STRING)).then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED))
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_filter_failure_2() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).filter(alwaysFalse2(), exceptionFilterFunction)
      .then(() -> successful(STRING + STRING)).then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED))
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_filter_failure_3() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .filter(alwaysFalse3(), exceptionFilterFunction).then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED))
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_filter_failure_4() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).filter(alwaysFalse4(), exceptionFilterFunction).then(() -> successful(STRING_UPPERED))
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

  @Test public void test_6_step_filter_failure_5() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING + STRING))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED)).filter(alwaysFalse5(), exceptionFilterFunction)
      .then(() -> successful(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isFailure(is(exceptionFilter)));
  }

}
