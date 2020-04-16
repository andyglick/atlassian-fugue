package io.atlassian.fugue.extensions.step;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Unit;
import org.junit.Test;

import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.noneSupplier;
import static io.atlassian.fugue.Option.some;
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
import static io.atlassian.fugue.hamcrest.OptionMatchers.isNone;
import static io.atlassian.fugue.hamcrest.OptionMatchers.isSome;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

  @Test public void test_1_step_failure() {
    Option<Unit> stepped = Steps.begin(none()).yield(value1 -> Unit());

    assertThat(stepped, isNone());
  }

  @Test public void test_1_step_filter_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysTrue()).yield(Long::new);

    assertThat(stepped, isSome(is(LONG)));
  }

  @Test public void test_1_step_filter_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).yield(Long::new);

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_success_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then((firstValue) -> some(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_2_step_success_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_2_step_failure_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> none()).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_failure_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(noneSupplier()).yield((value1, value2) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_failure_1() {
    Option<Long> stepped = Steps.begin(none(String.class)).then(() -> some(9)).yield((value1, value2) -> new Long(value2));

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_filter_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).filter(alwaysTrue2()).yield((v1, v2) -> v2);

    assertThat(stepped, isSome(is(LONG)));
  }

  @Test public void test_2_step_filter_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).filter(alwaysFalse2()).yield((v1, v2) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_2_step_filter_failure_1() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).then(() -> some(STRING))
      .yield((value1, value2) -> new Long(value1 + value2));

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_success_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((v1, v2) -> some(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_3_step_success_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(Option::some).then(() -> some(88))
      .yield((value1, value2, value3) -> new Long(value1 + value2));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_3_step_failure_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((v1, v2) -> none())
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_failure_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(Option::none).yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_failure_1() {
    Option<Long> stepped = Steps.begin(none()).then(() -> some(STRING)).then((v1, v2) -> some(LONG)).yield((value1, value2, value3) -> value3);

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(noneSupplier()).then((v1, v2) -> some(LONG))
      .yield((value1, value2, value3) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_filter_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).then(() -> some(STRING_LOWERED)).filter(alwaysTrue3())
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isSome(is(LONG)));
  }

  @Test public void test_3_step_filter_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).then(() -> some(STRING_LOWERED)).filter(alwaysFalse3())
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_filter_failure_1() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).then(() -> some(LONG)).then(() -> some(STRING_LOWERED))
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_3_step_filter_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).filter(alwaysFalse2()).then(() -> some(STRING_LOWERED))
      .yield((v1, v2, v3) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_success_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_4_step_success_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_4_step_failure_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((s, s2) -> some(STRING)).then((s, s2, s3) -> none())
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_failure_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING)).then(Option::none)
      .yield((value1, value2, value3, value4) -> new Long(value1));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_failure_1() {
    Option<Long> stepped = Steps.begin(none(String.class)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(noneSupplier()).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_failure_3() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(noneSupplier()).then(() -> some(STRING))
      .yield((value1, value2, value3, value4) -> new Long(value4));

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_filter_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).then(() -> some(STRING_LOWERED)).then(() -> some(true))
      .filter(alwaysTrue4()).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isSome(is(LONG)));
  }

  @Test public void test_4_step_filter_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).then(() -> some(STRING_LOWERED)).then(() -> some(true))
      .filter(alwaysFalse4()).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_filter_failure_1() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).then(() -> some(LONG)).then(() -> some(STRING_LOWERED))
      .then(() -> some(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_filter_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).filter(alwaysFalse2()).then(() -> some(STRING_LOWERED))
      .then(() -> some(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_4_step_filter_failure_3() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(LONG)).then(() -> some(STRING_LOWERED)).filter(alwaysFalse3())
      .then(() -> some(true)).yield((v1, v2, v3, v4) -> v2);

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_success_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_5_step_success_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_5_step_failure_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> none()).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure_supplier() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(noneSupplier()).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure_1() {
    Option<Long> stepped = Steps.begin(none(String.class)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(noneSupplier()).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure_3() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(noneSupplier()).then(() -> some(STRING))
      .then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value2));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_failure_4() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(noneSupplier())
      .then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_filter_success() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).filter(alwaysTrue5())
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_5_step_filter_failure() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).filter(alwaysFalse5())
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_filter_failure_1() {
    Option<Long> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_filter_failure_2() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).filter(alwaysFalse2()).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_filter_failure_3() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second)).filter(alwaysFalse3())
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING)).yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_5_step_filter_failure_4() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).filter(alwaysFalse4()).then((v1, v2, v3, v4) -> some(STRING))
      .yield((value1, value2, value3, value4, value5) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_success_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING_UPPERED))
      .then((first, second, third, fourth, fifth) -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isSome(is(LONGLONG)));
  }

  @Test public void test_6_step_success_supplier() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isSome(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_failure_functor() {
    Option<Long> stepped = Steps.begin(some(STRING)).then(v -> some(STRING)).then((first, second) -> some(first + second))
      .then((v1, v2, v3) -> some(STRING)).then((v1, v2, v3, v4) -> some(STRING_UPPERED)).then((first, second, third, fourth, fifth) -> none())
      .yield((value1, value2, value3, value4, value5, value6) -> new Long(value3));

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_supplier() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(noneSupplier()).yield((value1, value2, value3, value4, value5, value6) -> value5);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_1() {
    Option<String> stepped = Steps.begin(none(String.class)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_2() {
    Option<String> stepped = Steps.begin(some(LONG)).then(noneSupplier()).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_3() {
    Option<String> stepped = Steps.begin(some(LONG)).then(() -> some(STRING)).then(noneSupplier()).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_4() {
    Option<String> stepped = Steps.begin(some(LONG)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(noneSupplier())
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_failure_5() {
    Option<String> stepped = Steps.begin(some(LONG)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(noneSupplier()).then(() -> some(STRING_LOWERED)).yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_success() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).filter(alwaysTrue6())
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isSome(is(STRING_LOWERED)));
  }

  @Test public void test_6_step_filter_failure() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED)).filter(alwaysFalse6())
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_failure_1() {
    Option<String> stepped = Steps.begin(some(STRING)).filter(alwaysFalse()).then(() -> some(STRING)).then(() -> some(STRING + STRING))
      .then(() -> some(STRING)).then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_failure_2() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).filter(alwaysFalse2()).then(() -> some(STRING + STRING))
      .then(() -> some(STRING)).then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_failure_3() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).filter(alwaysFalse3())
      .then(() -> some(STRING)).then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_failure_4() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .filter(alwaysFalse4()).then(() -> some(STRING_UPPERED)).then(() -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

  @Test public void test_6_step_filter_failure_5() {
    Option<String> stepped = Steps.begin(some(STRING)).then(() -> some(STRING)).then(() -> some(STRING + STRING)).then(() -> some(STRING))
      .then(() -> some(STRING_UPPERED)).filter(alwaysFalse5()).then(() -> some(STRING_LOWERED))
      .yield((value1, value2, value3, value4, value5, value6) -> value6);

    assertThat(stepped, isNone());
  }

}
