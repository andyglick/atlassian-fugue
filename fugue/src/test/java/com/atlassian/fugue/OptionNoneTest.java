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

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;

import com.atlassian.fugue.mango.Predicates;

public class OptionNoneTest {
  private final Option<Integer> none = none();

  @Test(expected = NoSuchElementException.class) public void get() {
    none.get();
  }

  @Test public void isSet() {
    assertThat(none.isDefined(), is(false));
  }

  @Test public void getOrElse() {
    assertThat(none.getOrElse(1), is(1));
  }

  @Test public void getOrNull() {
    assertThat(none.getOrNull(), is((Integer) null));
  }

  @Test public void map() {
    final Function<Integer, Integer> function = input -> {
      throw new AssertionError("None.map should not call the function.");
    };

    assertThat(none.map(function).isEmpty(), is(true));
  }

  @Test(expected = NullPointerException.class) public void nullFunctionForMap() {
    none.map(null);
  }

  @Test(expected = NullPointerException.class) public void nullPredicateForFilter() {
    none.filter(null);
  }

  @Test public void filterTrueReturnsEmpty() {
    assertThat(none.filter(x -> true).isEmpty(), is(true));
  }

  @Test public void filterFalseReturnsEmpty() {
    assertThat(none.filter(x -> false).isEmpty(), is(true));
  }

  @Test public void existsTrueReturnsFalse() {
    assertThat(none.exists(x -> true), is(false));
  }

  @Test public void existsFalseReturnsFalse() {
    assertThat(none.exists(x -> false), is(false));
  }

  @Test public void toLeftReturnsRight() {
    assertThat(none.toLeft(Suppliers.ofInstance("")).isRight(), is(true));
  }

  @Test public void toRightReturnsLeft() {
    assertThat(none.toRight(Suppliers.ofInstance("")).isLeft(), is(true));
  }

  @Test public void superTypesPermittedOnFilter() {
    final Option<ArrayList<?>> opt = none();
    final Option<ArrayList<?>> nopt = opt.filter(x -> true);
    assertThat(nopt, sameInstance(opt));
  }

  @Test public void superTypesPermittedOnMap() {
    final Option<ArrayList<?>> opt = none();
    final Option<Set<?>> size = opt.map(list -> {
      throw new AssertionError("This internal method should never get called.");
    });
    assertThat(size.isDefined(), is(false));
  }

  @Test public void hashDoesNotThrowException() {
    none.hashCode();
  }

  // These tests are duplicated in TestEmptyIterator, but I've included them
  // here to ensure
  // that None itself complies with the API.
  @Test public void iteratorHasNoNext() {
    assertThat(none.iterator().hasNext(), is(false));
  }

  @Test(expected = NoSuchElementException.class) public void iteratorNext() {
    none.iterator().next();
  }

  @Test(expected = UnsupportedOperationException.class) public void iteratorImmutable() {
    none.iterator().remove();
  }

  @Test public void foreach() {
    assertThat(Count.countEach(none), is(0));
  }

  @Test public void forallTrue() {
    assertThat(none.forall(x -> true), is(true));
  }

  @Test public void forallFalse() {
    assertThat(none.forall(x -> false), is(true));
  }

  @Test public void toStringTest() {
    assertThat(none.toString(), is("none()"));
  }

  @Test public void equalsItself() {
    assertThat(none.equals(none), is(true));
  }

  @Test public void notEqualsSome() {
    assertThat(none.equals(some(5)), is(false));
  }

  @Test public void notEqualsNull() {
    assertThat(none.equals(null), is(false));
  }

  static class MyException extends Exception {
    private static final long serialVersionUID = -1056362494708225175L;
  }

  @Test(expected = MyException.class) public void getOrThrow() throws MyException {
    none.getOrThrow(MyException::new);
  }
}
