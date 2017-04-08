/*
   Copyright 2011 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package io.atlassian.fugue;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.atlassian.fugue.Option.defined;
import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.Suppliers.ofInstance;
import static io.atlassian.fugue.UtilityFunctions.toStringFunction;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class OptionTest {
  @Test public void foldOnNoneReturnsValueFromSupplier() {
    assertThat(none().fold(ofInstance("a"), toStringFunction()), is(equalTo("a")));
  }

  @Test public void foldOnSomeReturnsValueAfterFunctionIsApplied() {
    assertThat(some(1).fold(ofInstance(0), increment()), is(equalTo(2)));
  }

  @Test public void isDefinedIsTrueForSome() {
    assertThat(some("a").isDefined(), is(true));
  }

  @Test public void isDefinedIsFalseForNone() {
    assertThat(none().isDefined(), is(false));
  }

  @Test public void getOnSomeReturnsValue() {
    assertThat(some(1).get(), is(equalTo(1)));
  }

  @Test(expected = NoSuchElementException.class) public void getOnNoneThrowsException() {
    none().get();
  }

  @Test public void getOrElseOnSomeReturnsValue() {
    assertThat(some(1).getOrElse(0), is(equalTo(1)));
  }

  @Test public void getOrElseOnNoneReturnsElseValue() {
    assertThat(none(Integer.class).getOrElse(0), is(equalTo(0)));
  }

  @Test public void getOrElseOnNoneReturnsValueFromSupplier() {
    assertThat(none(Integer.class).getOr(ofInstance(0)), is(equalTo(0)));
  }

  @Test public void getElseOnNoneReturnsValueFromSupplier() {
    assertThat(none(Integer.class).getOr(ofInstance(0)), is(equalTo(0)));
  }

  @Test public void getElseOnNoneReturnsValueFromLambda() {
    assertThat(none(Integer.class).getOr(() -> 0), is(equalTo(0)));
  }

  @Test public void iteratorOverSomeContainsOnlyValue() {
    assertThat(some(1), contains(1));
  }

  @Test public void noneIsEmptyIterable() {
    assertThat(none(), is(emptyIterable()));
  }

  @Test public void mapAppliesFunctionToSomeValue() {
    assertThat(some(1).map(increment()), is(equalTo(some(2))));
  }

  @Test public void mapOverNoneDoesNothing() {
    assertThat(none(Integer.class).map(increment()), is(equalTo(none(Integer.class))));
  }

  @Test public void flatMapAppliesFunctionToSomeValue() {
    assertThat(some(1).flatMap(liftedIncrement()), is(equalTo(some(2))));
  }

  @Test public void flatMapOverNoneDoesNothing() {
    assertThat(none(Integer.class).flatMap(liftedIncrement()), is(equalTo(none(Integer.class))));
  }

  @Test public void equalSomesAreEqual() {
    assertThat(some(2), is(some(2)));
  }

  @Test public void nonEqualSomesAreNotEqual() {
    assertThat(some(1), is(not(some(2))));
  }

  @Test public void hashCodesFromEqualSomesAreEqual() {
    assertThat(some(1).hashCode(), is(some(1).hashCode()));
  }

  @Test public void noneSomeEquality() {
    assertThat(none().equals(some("")), is(false));
  }

  @Test public void someNoneEquality() {
    assertThat(some("").equals(none(String.class)), is(false));
  }

  @Test public void someSomeEquality() {
    assertThat(some("something"), is(some("something")));
  }

  @Test public void noneNoneEquality() {
    assertThat(none(), is(equalTo(none())));
  }

  @Test public void someOrElseReturnsOriginal() {
    assertThat(some(1).orElse(some(2)), is(equalTo(some(1))));
  }

  @Test public void noneOrElseReturnsOrElse() {
    assertThat(none(int.class).orElse(some(2)), is(equalTo(some(2))));
  }

  @Test public void someOrElseSupplierReturnsOriginal() {
    assertThat(some(1).orElse(ofInstance(some(2))), is(equalTo(some(1))));
  }

  @Test public void noneOrElseSupplierReturnsOrElse() {
    assertThat(none(int.class).orElse(ofInstance(some(2))), is(equalTo(some(2))));
  }

  @Test public void definedOnSome() {
    assertThat(defined().test(some("a")), is(true));
  }

  @Test public void definedOnNone() {
    assertThat(defined().test(none()), is(false));
  }

  @Test public void someToOptional() {
    assertThat(some("value").toOptional(), is(Optional.of("value")));
  }

  @Test(expected = NullPointerException.class) public void someWithNullToOptional() {
    some("null").map(Functions.constant(null)).toOptional();
  }

  @Test public void noneToOptional() {
    assertThat(none().toOptional(), is(Optional.empty()));
  }

  @Test public void someToStream() {
    final Stream<String> stream = some("value").toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), contains("value"));
  }

  @Test public void someWithNullToStream() {
    final String nullString = null;
    final Stream<String> stream = some("null").map(Functions.constant(nullString)).toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), contains(nullString));
  }

  @Test public void noneToStream() {
    final Stream<String> stream = Option.<String> none().toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), empty());
  }

  //
  // scaffolding
  //

  private Function<Integer, Option<Integer>> liftedIncrement() {
    return Functions.compose(Functions.<Integer> nullToOption(), increment());
  }

  private Function<Integer, Integer> increment() {
    return i -> i + 1;
  }
}
