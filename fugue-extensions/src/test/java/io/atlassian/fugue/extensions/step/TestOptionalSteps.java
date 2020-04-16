package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Unit;
import org.junit.Test;

import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
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
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

  @Test public void test_1_step_failure() {
    Optional<Unit> stepped = Steps.begin(empty()).yield(value1 -> Unit());

    assertThat(stepped, isEmpty());
  }

  @Test public void test_1_step_filter_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysTrue()).yield(Long::new);

    assertThat(stepped, isPresentAnd(is(LONG)));
  }

  @Test public void test_1_step_filter_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).yield(Long::new);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_success_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then((firstValue) -> of(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_2_step_success_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_2_step_failure_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> empty()).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_failure_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::empty).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_failure_1() {
    Optional<Long> stepped = Steps.begin(empty()).then(() -> of(9)).yield((value1, value2) -> new Long(value2));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_filter_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).filter(alwaysTrue2()).yield((v1, v2) -> v2);

    assertThat(stepped, isPresentAnd(is(LONG)));
  }

  @Test public void test_2_step_filter_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).filter(alwaysFalse2()).yield((v1, v2) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_2_step_filter_failure_1() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).then(() -> of(LONG)).yield((v1, v2) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_success_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((v1, v2) -> of(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_3_step_success_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::of).then(() -> of(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_3_step_failure_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((v1, v2) -> empty())
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_failure_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(Optional::empty).yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_failure_1() {
    Optional<Long> stepped = Steps.begin(empty()).then(() -> of(STRING)).then((v1, v2) -> of(LONG)).yield((value1, value2, value3) -> value3);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::empty).then((v1, v2) -> of(LONG))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_filter_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).filter(alwaysTrue3())
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isPresentAnd(is(LONG)));
  }

  @Test public void test_3_step_filter_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).filter(alwaysFalse3())
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_filter_failure_1() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).then(() -> of(LONG)).then(() -> of(STRING_LOWERED))
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_3_step_filter_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).filter(alwaysFalse2()).then(() -> of(STRING_LOWERED))
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_success_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_4_step_success_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_4_step_failure_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((s, s2) -> of(STRING)).then((s, s2, s3) -> empty())
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_failure_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING)).then(Optional::empty)
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_failure_1() {
    Optional<Long> stepped = Steps.begin(empty()).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::empty).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_failure_3() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(Optional::empty).then(() -> of(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value4));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_filter_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).then(() -> of(true)).filter(alwaysTrue4())
      .yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isPresentAnd(is(LONG)));
  }

  @Test public void test_4_step_filter_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).then(() -> of(true)).filter(alwaysFalse4())
      .yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_filter_failure_1() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).then(() -> of(true))
      .yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_filter_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).filter(alwaysFalse2()).then(() -> of(STRING_LOWERED)).then(() -> of(true))
      .yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_4_step_filter_failure_3() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(LONG)).then(() -> of(STRING_LOWERED)).filter(alwaysFalse3()).then(() -> of(true))
      .yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_success_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_5_step_success_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_5_step_failure_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> empty()).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure_supplier() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(Optional::empty).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure_1() {
    Optional<Long> stepped = Steps.begin(empty()).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(Optional::empty).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure_3() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(Optional::empty).then(() -> of(STRING))
      .then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value2));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_failure_4() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(Optional::empty)
      .then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_filter_success() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).filter(alwaysTrue5())
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_5_step_filter_failure() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).filter(alwaysFalse5())
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_filter_failure_1() {
    Optional<Long> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_filter_failure_2() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).filter(alwaysFalse2()).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_filter_failure_3() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second)).filter(alwaysFalse3())
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_5_step_filter_failure_4() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).filter(alwaysFalse4()).then((v1, v2, v3, v4) -> of(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_success_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isPresentAnd(is(LONGLONG)));
  }

  @Test public void test_6_step_success_supplier() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isPresentAnd(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_failure_functor() {
    Optional<Long> stepped = Steps.begin(of(STRING)).then(v -> of(STRING)).then((first, second) -> of(first + second))
      .then((v1, v2, v3) -> of(STRING)).then((v1, v2, v3, v4) -> of(STRING_UPPERED)).then((first, second, third, fourth, fifth) -> empty())
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_supplier() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(Optional::empty).yield((value1, value2, value3, value4, value5, value6) -> value5);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_1() {
    Optional<String> stepped = Steps.begin(empty()).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_2() {
    Optional<String> stepped = Steps.begin(of(LONG)).then(Optional::empty).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_3() {
    Optional<String> stepped = Steps.begin(of(LONG)).then(() -> of(STRING)).then(Optional::empty).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_4() {
    Optional<String> stepped = Steps.begin(of(LONG)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(Optional::empty)
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_failure_5() {
    Optional<String> stepped = Steps.begin(of(LONG)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(Optional::empty).then(() -> of(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_success() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).filter(alwaysTrue6())
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isPresentAnd(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_filter_failure() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED)).filter(alwaysFalse6())
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_failure_1() {
    Optional<String> stepped = Steps.begin(of(STRING)).filter(alwaysFalse()).then(() -> of(STRING)).then(() -> of(STRING + STRING))
      .then(() -> of(STRING)).then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_failure_2() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).filter(alwaysFalse2()).then(() -> of(STRING + STRING))
      .then(() -> of(STRING)).then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_failure_3() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).filter(alwaysFalse3())
      .then(() -> of(STRING)).then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_failure_4() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .filter(alwaysFalse4()).then(() -> of(STRING_UPPERED)).then(() -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

  @Test public void test_6_step_filter_failure_5() {
    Optional<String> stepped = Steps.begin(of(STRING)).then(() -> of(STRING)).then(() -> of(STRING + STRING)).then(() -> of(STRING))
      .then(() -> of(STRING_UPPERED)).filter(alwaysFalse5()).then(() -> of(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isEmpty());
  }

}
