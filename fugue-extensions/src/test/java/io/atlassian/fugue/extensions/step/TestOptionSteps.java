package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import java.util.function.Supplier;

import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.Unit.Unit;
import static io.atlassian.fugue.hamcrest.OptionMatchers.isNone;
import static io.atlassian.fugue.hamcrest.OptionMatchers.isSome;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestOptionSteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final Long LONGLONG = 123456123456L;

  @Test public void test_1_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).yield(Long::new);

    assertThat(stepped, isSome(is(LONG)));
  }

  @Test public void test_2_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then((firstValue) -> some(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_3_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_4_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then((first, second) -> some(first + second)).then(() -> some(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_5_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then((first, second) -> some(first + second)).then(() -> some(STRING))
      .then(() -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_6_step_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then((first, second) -> some(first + second)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then((first, second, third, fourth, fifth) -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_1_step_failure() {
    Option<Unit> stepped = Steps.begin(none()).yield(value1 -> Unit());

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then((Supplier<Option<Object>>) Option::none).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(Option::none).yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then((s, s2) -> some(STRING)).then((s, s2, s3) -> none())
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure() {
    Option<String> stepped = Steps.begin(some(STRING)).then(s -> some(STRING)).then((s, s2) -> some(STRING)).then(() -> none())
      .then((value1, value2, value3, value4) -> some(STRING_UPPERED)).yield((value1, value2, value3, value4, value5) -> value5);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure() {
    Option<String> stepped = Steps.begin(some(STRING)).then(s -> some(STRING)).then((s, s2) -> some(STRING)).then(() -> some(1))
      .then((value1, value2, value3, value4) -> some(STRING_UPPERED)).then((value1, value2, value3, value4, value5) -> none())
      .yield((value1, value2, value3, value4, value5, value6) -> value3);

    assertThat(stepped, isNone());
  }

}
