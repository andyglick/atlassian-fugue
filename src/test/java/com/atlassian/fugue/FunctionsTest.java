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

import static com.atlassian.fugue.Functions.matches;
import static com.atlassian.fugue.Functions.partial;
import static com.atlassian.fugue.UtilityFunctions.dividableBy;
import static com.atlassian.fugue.UtilityFunctions.isEven;
import static com.atlassian.fugue.UtilityFunctions.square;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class FunctionsTest {

  @Test public void functionApply() {
    assertThat(Functions.<Integer, Integer> apply(8).apply(square), is(64));
  }

  @Test public void partialNone() {
    assertThat(partial(isEven, square).apply(1), is(Option.<Integer> none()));
  }

  @Test public void partialSome() {
    assertThat(partial(isEven, square).apply(4), is(Option.some(16)));
  }

  @Test public void matches2Some() {
    assertThat(matches(partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(2), is(Option.some(4)));
  }

  @Test public void matches2None() {
    assertThat(matches(partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(1), is(Option.<Integer> none()));
  }

  @Test public void matches3Some() {
    assertThat(matches(partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(2),
      is(Option.some(4)));
  }

  @Test public void matches3None() {
    assertThat(matches(partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square)).apply(1),
      is(Option.<Integer> none()));
  }

  @Test public void matches4Some() {
    assertThat(
      matches(partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square))
        .apply(2), is(Option.some(4)));
  }

  @Test public void matches4None() {
    assertThat(
      matches(partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square), partial(dividableBy(2), square))
        .apply(1), is(Option.<Integer> none()));
  }

  @SuppressWarnings("unchecked") @Test public void matches5Some() {
    assertThat(
      matches(partial(dividableBy(6), square), partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square),
        partial(dividableBy(2), square)).apply(2), is(Option.some(4)));
  }

  @SuppressWarnings("unchecked") @Test public void matches5None() {
    assertThat(
      matches(partial(dividableBy(6), square), partial(dividableBy(5), square), partial(dividableBy(4), square), partial(dividableBy(3), square),
        partial(dividableBy(2), square)).apply(1), is(Option.<Integer> none()));
  }
}
