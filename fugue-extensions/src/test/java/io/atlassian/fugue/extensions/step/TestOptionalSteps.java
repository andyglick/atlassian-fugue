package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Unit;
import org.junit.Test;

import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestOptionalSteps {

  private static final String STRING = "123456";
  private static final String STRING_UPPERED = "QWERTY";
  private static final String STRING_LOWERED = "qwerty";
  private static final Long LONG = 123456L;
  private static final Long LONGLONG = 123456123456L;

  @Test public void test_1_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).yield(Long::new);

    assertThat(stepped, isPresentAnd(is(LONG)));
  }

  @Test public void test_2_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then((firstValue) -> of(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_3_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_4_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then((first, second) -> of(first + second)).then(() -> of(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_5_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then((first, second) -> of(first + second)).then(() -> of(STRING))
      .then(() -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_6_step_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then((first, second) -> of(first + second)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then((first, second, third, fourth, fifth) -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_1_step_failure() {
    Optional<Unit> stepped = Steps.begin(empty()).yield(value1 -> Unit.Unit());

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::empty).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(Optional::empty).yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then((s, s2) -> of(STRING)).then((s, s2, s3) -> empty())
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(s -> of(STRING)).then((s, s2) -> of(STRING)).then(Optional::empty)
      .then((value1, value2, value3, value4) -> of(STRING_UPPERED)).yield((value1, value2, value3, value4, value5) -> value5);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(s -> of(STRING)).then((s, s2) -> of(STRING)).then(() -> of(1))
      .then((value1, value2, value3, value4) -> of(STRING_UPPERED)).then((value1, value2, value3, value4, value5) -> empty())
      .yield((value1, value2, value3, value4, value5, value6) -> value3);

    assertThat(stepped, isEmpty());
  }

}
