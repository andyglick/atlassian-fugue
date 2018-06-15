package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Try;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static io.atlassian.fugue.Unit.Unit;
import static io.atlassian.fugue.hamcrest.TryMatchers.isFailure;
import static io.atlassian.fugue.hamcrest.TryMatchers.isSuccessful;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestTrySteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final Long LONGLONG = 123456123456L;

  @Test public void test_1_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).yield(Long::new);

    assertThat(stepped, isSuccessful(is(LONG)));
  }

  @Test public void test_2_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then((firstValue) -> successful(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_3_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> successful(STRING))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_4_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then((first, second) -> successful(first + second))
      .then(() -> successful(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_5_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then((first, second) -> successful(first + second))
      .then(() -> successful(STRING)).then(() -> successful(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_6_step_success() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then((first, second) -> successful(first + second))
      .then(() -> successful(STRING)).then(() -> successful(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> successful(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isSuccessful(is(LONGLONG)));
  }

  @Test public void test_1_step_failure() {
    Try<Unit> stepped = Steps.begin(failure(new RuntimeException())).yield(value1 -> Unit());

    assertThat(stepped, isFailure());
  }

  @Test public void test_2_step_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> failure(new RuntimeException())).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isFailure());
  }

  @Test public void test_3_step_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then(() -> failure(new RuntimeException()))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isFailure());
  }

  @Test public void test_4_step_failure() {
    Try<Long> stepped = Steps.begin(successful(STRING)).then(() -> successful(STRING)).then((s, s2) -> successful(STRING))
      .then((s, s2, s3) -> failure(new RuntimeException())).yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isFailure());
  }

  @Test public void test_5_step_failure() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(s -> successful(STRING)).then((s, s2) -> successful(STRING))
      .then(() -> failure(new RuntimeException())).then((value1, value2, value3, value4) -> successful(STRING_UPPERED))
      .yield((value1, value2, value3, value4, value5) -> value5);

    assertThat(stepped, isFailure());
  }

  @Test public void test_6_step_failure() {
    Try<String> stepped = Steps.begin(successful(STRING)).then(s -> successful(STRING)).then((s, s2) -> successful(STRING)).then(() -> successful(1))
      .then((value1, value2, value3, value4) -> successful(STRING_UPPERED))
      .then((value1, value2, value3, value4, value5) -> failure(new RuntimeException()))
      .yield((value1, value2, value3, value4, value5, value6) -> value3);

    assertThat(stepped, isFailure());
  }

}
