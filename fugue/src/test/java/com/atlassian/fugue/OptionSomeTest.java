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
import static com.atlassian.fugue.Suppliers.ofInstance;
import static com.atlassian.fugue.UtilityFunctions.addOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Test;

import com.atlassian.fugue.mango.Predicates;

public class OptionSomeTest {
  private static final Integer ORIGINAL_VALUE = 1;
  private static final Integer NOT_IN_SOME = 3;
  Option<Integer> some = some(ORIGINAL_VALUE);

  @Test public void get() {
    assertThat(some.get(), is(ORIGINAL_VALUE));
  }

  @Test public void isSet() {
    assertThat(some.isDefined(), is(true));
  }

  @Test public void getOrElse() {
    assertThat(some.getOrElse(NOT_IN_SOME), is(ORIGINAL_VALUE));
  }

  @Test public void getOrNull() {
    assertThat(some.getOrNull(), is(ORIGINAL_VALUE));
  }

  @Test(expected = NullPointerException.class) public void someNull() {
    some(null);
  }

  @Test(expected = NullPointerException.class) public void mapForNull() {
    some.map(null);
  }

  @Test public void map() {
    assertThat(some.map(addOne).get(), is(2));
  }

  @Test(expected = NullPointerException.class) public void filterForNull() {
    some.filter(null);
  }

  @Test public void positiveFilter() {
    assertThat(some.filter(x -> true).get(), is(ORIGINAL_VALUE));
  }

  @Test public void negativeFilter() {
    assertThat(some.filter(x -> false).isDefined(), is(false));
  }

  @Test public void existsTrueReturnsTrue() {
    assertThat(some.exists(x -> true), is(true));
  }

  @Test public void existsFalseReturnsFalse() {
    assertThat(some.exists(x -> false), is(false));
  }

  @Test public void toLeftReturnsLeft() {
    assertThat(some.toLeft(ofInstance("")).isLeft(), is(true));
  }

  @Test public void toRightReturnsRight() {
    assertThat(some.toRight(ofInstance("")).isRight(), is(true));
  }

  @Test public void iteratorHasNext() {
    assertThat(some.iterator().hasNext(), is(true));
  }

  @Test public void iteratorNext() {
    final Iterator<Integer> iterator = some.iterator();
    assertThat(iterator.next(), is(ORIGINAL_VALUE));
    assertThat(iterator.hasNext(), is(false));
  }

  @Test(expected = UnsupportedOperationException.class) public void iteratorImmutable() {
    final Iterator<Integer> iterator = some.iterator();
    iterator.next();
    iterator.remove();
  }

  @Test public void foreach() {
    assertThat(Count.countEach(some), is(1));
  }

  @Test public void forallTrue() {
    assertThat(some.forall(x -> true), is(true));
  }

  @Test public void forallFalse() {
    assertThat(some.forall(x -> false), is(false));
  }

  @Test public void toStringTest() {
    assertThat(some.toString(), is("some(1)"));
  }

  @Test public void equalsItself() {
    assertThat(some.equals(some), is(true));
  }

  @Test public void notEqualsNone() {
    assertThat(some.equals(none(Integer.class)), is(false));
  }

  @Test public void notEqualsNull() {
    assertThat(some.equals(null), is(false));
  }

  @Test public void hashDoesNotThrowException() {
    some.hashCode();
  }

  static class MyException extends Exception {
    private static final long serialVersionUID = -1056362494708225175L;
  }

  @Test public void getOrThrow() throws MyException {
    assertThat(some.getOrThrow(MyException::new), is(ORIGINAL_VALUE));
  }
}
