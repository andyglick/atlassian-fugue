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
package com.atlassian.fugue;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.atlassian.fugue.Functions.apply;
import static com.atlassian.fugue.Functions.constant;
import static com.atlassian.fugue.Functions.countingPredicate;
import static com.atlassian.fugue.Functions.curried;
import static com.atlassian.fugue.Functions.flip;
import static com.atlassian.fugue.Functions.matches;
import static com.atlassian.fugue.Functions.partial;
import static com.atlassian.fugue.Functions.toBiFunction;
import static com.atlassian.fugue.Iterables.map;
import static com.atlassian.fugue.UtilityFunctions.dividableBy;
import static com.atlassian.fugue.UtilityFunctions.hasMinLength;
import static com.atlassian.fugue.UtilityFunctions.isEven;
import static com.atlassian.fugue.UtilityFunctions.leftOfString;
import static com.atlassian.fugue.UtilityFunctions.square;
import static com.atlassian.fugue.UtilityFunctions.subtract;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FunctionsTest {

  @Test public void functionApply() {
    assertThat(Functions.<Integer, Integer> apply(8).apply(square), is(64));
  }

  @Test public void functionLazyApply() {
    assertThat(Functions.<Integer, Integer> apply(Suppliers.ofInstance(8)).apply(square), is(64));
  }

  @Test public void functionLazyApplyIsLazy() {
    // NoSuchElementException should not be thrown
    apply(Suppliers.fromOption(Option.none()));
  }

  @Test public void partialNone() {
    assertThat(partial(isEven, square).apply(1), is(Option.<Integer>none()));
  }

  @Test public void partialSome() {
    assertThat(partial(isEven, square).apply(4), is(Option.some(16)));
  }

  @Test public void matches2Some() {
    assertThat(matches(partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(2), is(Option.some(4)));
  }

  @Test public void matches2None() {
    assertThat(matches(partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(1),
      is(Option.<Integer> none()));
  }

  @Test public void matches3Some() {
    assertThat(
      matches(partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(
        2), is(Option.some(4)));
  }

  @Test public void matches3None() {
    assertThat(
      matches(partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(
        1), is(Option.<Integer> none()));
  }

  @Test public void matches4Some() {
    assertThat(
      matches(partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(2), is(Option.some(
        4)));
  }

  @Test public void matches4None() {
    assertThat(
      matches(partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square),
        partial(dividableBy(2), square)).apply(1), is(Option.<Integer>none()));
  }

  @Test public void matches5Some() {
    assertThat(
      matches(partial(dividableBy(6), square), partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square),
        partial(dividableBy(2), square)).apply(2), is(Option.some(4)));
  }

  @Test public void matches5None() {
    assertThat(
      matches(partial(dividableBy(6), square), partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square),
        partial(dividableBy(2), square)).apply(1), is(Option.<Integer> none()));
  }

  @Test public void functionsToBiFunction() {
    assertThat(toBiFunction(leftOfString).apply("abcde", 3), is(Option.some("abc")));
  }

  @Test public void functionsCurried() {
    assertThat(curried(subtract).apply(6).apply(2), is(4));
  }

  @Test public void flipped() {
    assertThat(flip(hasMinLength).apply(2).apply("abcde"), is(true));
  }

  @Test public void functionsConstant() {
    assertThat(map(Arrays.asList(1, 2, 3), constant(1)), contains(1, 1, 1));
  }

  @Test public void countingPredicateWithOne() {
    final Predicate<Integer> p = countingPredicate(1);
    assertThat(p.test(1), is(true));
    assertThat(p.test(1), is(false));
  }

  @Test public void countingPredicateWithZero() {
    final Predicate<Integer> p = countingPredicate(0);
    assertThat(p.test(1), is(false));
  }

  @Test(expected = IllegalArgumentException.class) public void countingPredicateWithNegative() {
    final Predicate<Integer> p = countingPredicate(-1);
  }
}
