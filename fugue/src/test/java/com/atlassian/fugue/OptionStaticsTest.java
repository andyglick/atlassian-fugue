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
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Options.lift;
import static com.atlassian.fugue.UtilityFunctions.bool2String;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;

import com.atlassian.fugue.mango.Function.Function;
import com.atlassian.fugue.mango.Function.Function2;
import com.atlassian.fugue.mango.Function.Predicate;
import com.atlassian.fugue.mango.Predicates;

import org.junit.Test;


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

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Options> defaultCtor().apply(Options.class));
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
    assertThat(result, is(sameInstance(Option.<Number> none())));
  }

  @Test public void liftToString() {
    assertThat(lift(bool2String).apply(some(true)), is(some(String.valueOf(true))));
  }

  @Test public void liftNone() {
    assertThat(lift(bool2String).apply(Option.<Boolean> none()), is(sameInstance(Option.<String> none())));
  }

  @Test public void liftFunction() {
    assertThat(liftBool2StringFunction().apply(Option.some(true)), is(Option.some(String.valueOf(true))));
  }

  @Test public void liftFunctionNone() {
    Function<Option<Boolean>, Option<String>> liftedBool2String = liftBool2StringFunction();
    assertThat(liftedBool2String.apply(Option.<Boolean> none()), is(sameInstance(Option.<String> none())));
  }

  private Function<Option<Boolean>, Option<String>> liftBool2StringFunction() {
    return Options.<Boolean, String>lift().apply(UtilityFunctions.bool2String);
  }

  @Test public void ap() {
    assertThat(Options.ap(Option.some(false), Option.some(UtilityFunctions.bool2String)), is(Option.some(String.valueOf(false))));
  }

  @Test public void apNone() {
    assertThat(Options.ap(Option.<Boolean> none(), Option.some(UtilityFunctions.bool2String)), is(sameInstance(Option.<String> none())));
  }

  @Test public void apNoneFunction() {
    assertThat(Options.ap(Option.<Boolean> none(), Option.<Function<Boolean, Integer>> none()), is(sameInstance(Option.<Integer> none())));
  }

  @Test public void lift2() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(some("abc"), Option.some(1)), is(Option.some(Option.some('b'))));
  }

  @Test public void lift2FirstNone() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(Option.<String> none(), Option.some(1)), is(sameInstance(Option.<Option<Character>> none())));
  }

  @Test public void lift2SecondNone() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = Options.lift2(UtilityFunctions.charAt);
    assertThat(liftedCharAt.apply(some("abc"), Option.<Integer> none()), is(sameInstance(Option.<Option<Character>> none())));
  }

  @Test public void lift2Function() {
    Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftedCharAt = liftCharAtFunction();
    assertThat(liftedCharAt.apply(some("abc"), some(1)), is(some(some('b'))));
  }

  @Test public void liftPredicate() {
    Predicate<Option<Integer>> lifted = Options.lift(Predicates.equalTo(3));
    assertThat(lifted.apply(some(3)), is(true));
    assertThat(lifted.apply(some(2)), is(false));
    assertThat(lifted.apply(Option.<Integer> none()), is(false));
  }

  private Function2<Option<String>, Option<Integer>, Option<Option<Character>>> liftCharAtFunction() {
    return Options.<String, Integer, Option<Character>>lift2().apply(UtilityFunctions.charAt);
  }
}
