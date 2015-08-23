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

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.atlassian.fugue.Eithers.getOrThrow;
import static io.atlassian.fugue.Iterables.filter;
import static io.atlassian.fugue.Iterables.isEmpty;
import static io.atlassian.fugue.Iterables.size;
import static io.atlassian.fugue.Option.fromOptional;
import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.option;
import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.Options.ap;
import static io.atlassian.fugue.Options.filterNone;
import static io.atlassian.fugue.Options.find;
import static io.atlassian.fugue.Options.flatten;
import static io.atlassian.fugue.Options.lift;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class OptionStaticsTest {
  static final Integer NULL = null;

  @Test public void getNull() {
    assertThat(option(null), is(sameInstance(none())));
  }

  @Test public void get() {
    assertThat(option("Winter.").get(), is("Winter."));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Options>defaultCtor().apply(Options.class));
  }

  @Test public void identity() {
    assertThat(none(), is(sameInstance(none())));
  }

  private List<Option<Integer>> twoOptions() {
    return Arrays.asList(option(NULL), option(2), option(NULL), option(4));
  }

  private List<Option<Integer>> fourNones() {
    return Arrays.asList(option(NULL), option(NULL), option(NULL), option(NULL));
  }

  @Test public void upcastSome() {
    Option<Integer> some = some(1);
    Option<Number> result = Options.<Number, Integer> upcast(some);
    Number expected = 1;
    assertThat(result.get(), is(expected));
  }

  @Test public void upcastNone() {
    Option<Integer> none = Option.none();
    Option<Number> result = Options.<Number, Integer> upcast(none);
    assertThat(result, is(sameInstance(Option.<Number>none())));
  }

  @Test public void liftToString() {
    assertThat(lift(UtilityFunctions.bool2String).apply(some(true)), is(some(String.valueOf(true))));
  }

  @Test public void liftNone() {
    assertThat(lift(UtilityFunctions.bool2String).apply(Option.<Boolean>none()), is(sameInstance(Option.<String>none())));
  }

  @Test public void liftFunction() {
    assertThat(liftBool2StringFunction().apply(Option.some(true)), is(Option.some(String.valueOf(true))));
  }

  @Test public void liftFunctionNone() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2StringFunction();
    assertThat(liftedBool2String.apply(Option.<Boolean> none()), is(sameInstance(Option.<String> none())));
  }

  private Function<Option<Boolean>, Option<String>> liftBool2StringFunction() {
    return Options.<Boolean, String> lift().apply(UtilityFunctions.bool2String);
  }

  @Test public void apTest() {
    assertThat(ap(Option.some(false), Option.some(UtilityFunctions.bool2String)),
      is(Option.some(String.valueOf(false))));
  }

  @Test public void apNone() {
    assertThat(ap(Option.<Boolean>none(), Option.some(UtilityFunctions.bool2String)),
      is(sameInstance(Option.<String> none())));
  }

  @Test public void apNoneFunction() {
    assertThat(ap(Option.<Boolean>none(), Option.<Function<Boolean, Integer>>none()),
      is(sameInstance(Option.<Integer>none())));
  }

  @Test public void lift2() {
    BiFunction<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options
      .lift2(UtilityFunctions.charAt);
    Option<Option<Character>> b = Option.some(Option.some('b'));
    assertThat(liftedCharAt.apply(some("abc"), Option.some(1)), is(b));
  }

  @Test public void lift2FirstNone() {
    BiFunction<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options
      .lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(Option.<String>none(), Option.some(1)),
      is(sameInstance(Option.<Option<Character>>none())));
  }

  @Test public void lift2SecondNone() {
    BiFunction<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options
      .lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(some("abc"), Option.<Integer> none()),
      is(sameInstance(Option.<Option<Character>> none())));
  }

  @Test public void lift2Function() {
    BiFunction<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = liftCharAtFunction();
    Option<Option<Character>> b = some(some('b'));
    assertThat(liftedCharAt.apply(some("abc"), some(1)), is(b));
  }

  @Test public void liftPredicate() {
    Predicate<Option<Integer>> lifted = lift(Predicate.isEqual(3));
    assertThat(lifted.test(some(3)), is(true));
    assertThat(lifted.test(some(2)), is(false));
    assertThat(lifted.test(Option.<Integer>none()), is(false));
  }


  private BiFunction<Option<String>, Option<Integer>, Option<Option<Character>>> liftCharAtFunction() {
    return Options.<String, Integer, Option<Character>> lift2().apply(UtilityFunctions.charAt);
  }

  @Test public void findFindsFirst() {
    assertThat(find(twoOptions()).get(), is(2));
  }

  @Test public void findFindsOneSingleton() {
    assertThat(find(Collections.singletonList(option(3))).get(), is(3));
  }

  @Test public void findFindsNone() {
    assertThat(find(fourNones()).isDefined(), is(false));
  }

  @Test public void findFindsNoneSingleton() {
    assertThat(find(Collections.singletonList(option(NULL))).isDefined(), is(false));
  }

  @Test public void filterFindsTwo() {
    final Iterable<Option<Integer>> filtered = filterNone(twoOptions());
    assertThat(size(filtered), is(2));
    final Iterator<Option<Integer>> it = filtered.iterator();
    assertThat(it.next().get(), is(2));
    assertThat(it.next().get(), is(4));
    assertThat(it.hasNext(), is(false));
  }

  @Test public void flattenFindsTwo() {
    final Iterable<Integer> flattened = flatten(twoOptions());
    assertThat(size(flattened), is(2));
    final Iterator<Integer> it = flattened.iterator();
    assertThat(it.next(), is(2));
    assertThat(it.next(), is(4));
    assertThat(it.hasNext(), is(false));
  }

  @Test public void filterFindsNone() {
    assertThat(isEmpty().test(filterNone(fourNones())), is(true));
  }


  @Test public void filterNones() {
    final List<Option<Integer>> list = Arrays.asList(some(1), none(Integer.class), some(2));
    MatcherAssert.assertThat(size(filterNone(list)), is(equalTo(2)));
  }

  @Test public void someDefined() {
    MatcherAssert.assertThat(filter(Collections.singletonList(some(3)), Maybe::isDefined).iterator().hasNext(), is(true));
  }

  @Test public void noneNotDefined() {
    // throw new RuntimeException();
    MatcherAssert.assertThat(filter(Collections.singletonList(none(int.class)), Maybe::isDefined).iterator().hasNext(), is(false));
  }

  @Test public void fromPresentOptional() {
    assertThat(fromOptional(Optional.of("value")), is(some("value")));
  }

  @Test public void fromEmptyOptional() {
    assertThat(fromOptional(Optional.empty()), is(none()));
  }

}
