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

import static com.atlassian.fugue.Pair.pair;
import static com.atlassian.fugue.Pair.zip;
import static com.google.common.collect.Iterables.transform;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

public class PairTest {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    pair(null, "");
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    pair("", null);
  }

  @Test public void left() {
    assertThat(pair("left", "right").left(), is("left"));
  }

  @Test public void right() {
    assertThat(pair("left", "right").right(), is("right"));
  }

  @Test public void toStringTest() {
    assertThat(pair("hello", 4).toString(), is("Pair(hello, 4)"));
  }

  @Test public void hashCodeTest() {
    assertThat(pair(1, 3).hashCode(), is(65539));
  }

  @Test public void notEqualToNull() {
    assertThat(pair(1, 3).equals(null), is(false));
  }

  @Test public void equalToSelf() {
    final Pair<Integer, Integer> pair = pair(1, 3);
    assertThat(pair.equals(pair), is(true));
  }

  @Test public void notEqualToArbitraryObject() {
    assertThat(pair(1, 3).equals(new Object()), is(false));
  }

  @Test public void notEqualLeft() {
    assertThat(pair(1, 3).equals(pair(0, 3)), is(false));
  }

  @Test public void notEqualRight() {
    assertThat(pair(1, 3).equals(pair(1, 0)), is(false));
  }

  @Test public void equalsSameValue() {
    assertThat(pair(1, 3).equals(pair(1, 3)), is(true));
  }

  @Test public void leftFunction() {
    final Iterable<Integer> ints = transform(pairs(), Pair.<Integer> leftValue());
    assertThat(ints, contains(1, 2, 3));
  }

  @Test public void rightFunction() {
    final Iterable<String> ints = transform(pairs(), Pair.<String> rightValue());
    assertThat(ints, contains("1", "2", "3"));
  }

  @Test public void zipped() {
    final Iterable<Integer> ints = ImmutableList.of(1, 2, 3);
    final Iterable<String> strings = ImmutableList.of("1", "2", "3", "4");
    @SuppressWarnings("unchecked")
    final Matcher<Iterable<? extends Pair<Integer, String>>> contains = contains(pair(1, "1"), pair(2, "2"), pair(3, "3"));
    assertThat(zip(ints, strings), contains);
  }

  @Test public void zippedDiscardsLongest() {
    final Iterable<Integer> ints = ImmutableList.of(1, 2, 3, 4, 5);
    final Iterable<String> strings = ImmutableList.of("1", "2", "3");
    assertThat(zip(ints, strings), Matchers.<Pair<Integer, String>> iterableWithSize(3));
  }

  @Test(expected = UnsupportedOperationException.class) public void zippedUnmodifiable() {
    final Iterable<Integer> ints = ImmutableList.of(1, 2, 3, 4, 5);
    final Iterable<String> strings = ImmutableList.of("1", "2", "3");
    final Iterator<Pair<Integer, String>> zip = zip(ints, strings).iterator();
    zip.next();
    zip.remove();
  }

  private Iterable<Pair<Integer, String>> pairs() {
    return ImmutableList.of(pair(1, "1"), pair(2, "2"), pair(3, "3"));
  }
}
