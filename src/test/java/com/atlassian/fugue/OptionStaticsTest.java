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

import static com.atlassian.fugue.Eithers.getOrThrow;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Options.filterNone;
import static com.atlassian.fugue.Options.find;
import static com.atlassian.fugue.Options.flatten;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import com.google.common.base.Function;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class OptionStaticsTest {
  static final Integer NULL = null;

  @Test public void getNull() {
    assertThat(option(null), is(sameInstance(none())));
  }

  @Test public void get() {
    assertThat(option("Winter.").get(), is("Winter."));
  }

  @Test public void identity() {
    assertThat(none(), is(sameInstance(none())));
  }

  @Test public void findFindsFirst() {
    assertThat(find(twoOptions()).get(), is(2));
  }

  @Test public void findFindsOneSingleton() {
    assertThat(find(ImmutableList.of(option(3))).get(), is(3));
  }

  @Test public void findFindsNone() {
    assertThat(find(fourNones()).isDefined(), is(false));
  }

  @Test public void findFindsNoneSingleton() {
    assertThat(find(ImmutableList.of(option(NULL))).isDefined(), is(false));
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
    assertThat(isEmpty(filterNone(fourNones())), is(true));
  }

  private ImmutableList<Option<Integer>> twoOptions() {
    return ImmutableList.of(option(NULL), option(2), option(NULL), option(4));
  }

  private ImmutableList<Option<Integer>> fourNones() {
    return ImmutableList.of(option(NULL), option(NULL), option(NULL), option(NULL));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Options> defaultCtor().apply(Options.class));
  }

  @Test public void upcastSome() {
    Option<Integer> some = Option.some(1);
    Option<Number> result = Options.<Number, Integer>upcast(some);
    Number expected = 1;
    assertThat(result.get(), is(expected));
  }

  @Test public void upcastNone() {
    Option<Integer> none = Option.none();
    Option<Number> result = Options.<Number, Integer>upcast(none);
    assertThat(result, is(sameInstance(Option.<Number>none())));
  }

  @Test public void lift() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2String();
    assertThat(liftedBool2String.apply(Option.some(true)), is(Option.some(String.valueOf(true))));
  }

  @Test public void liftNone() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2String();
    assertThat(liftedBool2String.apply(Option.<Boolean>none()), is(sameInstance(Option.<String>none())));
  }

  private Function<Option<Boolean>, Option<String>> liftBool2String() {
    return Options.lift(UtilityFunctions.bool2String);
  }

  @Test public void liftFunction() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2StringFunction();
    assertThat(liftedBool2String.apply(Option.some(true)), is(Option.some(String.valueOf(true))));
  }

  @Test public void liftFunctionNone() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2StringFunction();
    assertThat(liftedBool2String.apply(Option.<Boolean>none()), is(sameInstance(Option.<String>none())));
  }

  private Function<Option<Boolean>, Option<String>> liftBool2StringFunction() {
    Function<Function<Boolean, String>, Function<Option<Boolean>, Option<String>>> liftFunction = Options.lift();
    return liftFunction.apply(UtilityFunctions.bool2String);
  }

  @Test public void ap() {
    assertThat(Options.ap(Option.some(false), Option.some(UtilityFunctions.bool2String)),
        is(Option.some(String.valueOf(false))));
  }

  @Test public void apNone() {
    assertThat(Options.ap(Option.<Boolean>none(), Option.some(UtilityFunctions.bool2String)),
        is(sameInstance(Option.<String>none())));
  }

  @Test public void apNoneFunction() {
    assertThat(Options.ap(Option.<Boolean>none(), Option.<Function<Boolean, Integer>>none()),
        is(sameInstance(Option.<Integer>none())));
  }

  @Test public void lift2() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(Option.some("abc"), Option.some(1)), is(Option.some(Option.some('b'))));
  }

  @Test public void lift2FirstNone() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(Option.<String>none(), Option.some(1)), is(sameInstance(Option.<Option<Character>>none())));
  }

  @Test public void lift2SecondNone() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(Option.some("abc"), Option.<Integer>none()), is(sameInstance(Option.<Option<Character>>none())));
  }

  @Test public void lift2Function() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = liftCharAtFunction();
    assertThat(liftedCharAt.apply(Option.some("abc"), Option.some(1)), is(Option.some(Option.some('b'))));
  }

  private Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftCharAtFunction() {
    Function<Function2<String, Integer, Option<Character>>, Function2<Option<String>, Option<Integer>, Option<Option<Character>>>>
        liftFunction = Options.lift2();
    return liftFunction.apply(UtilityFunctions.charAt);
  }
}
