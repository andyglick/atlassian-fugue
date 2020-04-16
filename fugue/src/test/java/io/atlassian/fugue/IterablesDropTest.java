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

import org.hamcrest.Matchers;
import org.junit.Test;

import static io.atlassian.fugue.Iterables.drop;
import static io.atlassian.fugue.Iterables.dropWhile;
import static io.atlassian.fugue.IterablesTakeTest.asIterable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IterablesDropTest {
  @Test public void dropOneFromList() {
    assertThat(drop(1, asList(1, 2, 3, 4)), contains(2, 3, 4));
  }

  @Test public void dropOneFromNonList() {
    assertThat(drop(1, asIterable(1, 2, 3, 4)), contains(2, 3, 4));
  }

  @Test public void dropNoneFromList() {
    assertThat(drop(0, asList(1, 2, 3, 4)), contains(1, 2, 3, 4));
  }

  @Test public void dropNoneFromNonList() {
    assertThat(drop(0, asIterable(1, 2, 3, 4)), contains(1, 2, 3, 4));
  }

  @Test public void dropAllFromList() {
    assertThat(drop(4, asList(1, 2, 3, 4)), Matchers.<Integer> emptyIterable());
  }

  @Test public void dropAllFromNonList() {
    assertThat(drop(4, asIterable(1, 2, 3, 4)), Matchers.<Integer> emptyIterable());
  }

  @Test public void dropMoreFromList() {
    assertThat(drop(12, asList(1, 2, 3, 4)), Matchers.<Integer> emptyIterable());
  }

  @Test public void dropMoreFromNonList() {
    assertThat(drop(12, asIterable(1, 2, 3, 4)), Matchers.<Integer> emptyIterable());
  }

  @Test public void dropOneToString() {
    assertThat(drop(1, asIterable(1, 2, 3, 4)).toString(), is("[2, 3, 4]"));
  }

  @Test(expected = NullPointerException.class) public void dropNull() {
    drop(0, null);
  }

  @Test(expected = IllegalArgumentException.class) public void dropNegativeFromList() {
    drop(-1, emptyList());
  }

  @Test public void dropWhileTest() {
    assertThat(dropWhile(asList(1, 2, 3, 4), i -> i < 2), contains(2, 3, 4));
  }

  @Test public void dropWhileAll() {
    assertThat(dropWhile(asList(1, 2, 3, 4), i -> true), emptyIterable());
  }

  @Test public void dropWhileNone() {
    assertThat(dropWhile(asList(1, 2, 3, 4), i -> false), contains(1, 2, 3, 4));
  }

  @Test(expected = NullPointerException.class) public void dropWhileNull() {
    dropWhile(null, null);
  }

  @Test(expected = NullPointerException.class) public void dropWhileNullWithPredicate() {
    dropWhile(null, x -> true);
  }

  @Test(expected = NullPointerException.class) public void dropWhileNullPredicate() {
    dropWhile(Iterables.emptyIterable(), null);
  }

  @Test public void dropWhileNotAfter() {
    assertThat(dropWhile(asList(1, 2, 3, 4, 1, 2), i -> i < 2), contains(2, 3, 4, 1, 2));
  }
}
