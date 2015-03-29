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

import static com.atlassian.fugue.Iterables.mergeSorted;
import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterableOf;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.LinkedList;

import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

// TODO broke iterables somewhere
public class IterablesMergeSortedTest {
  @Test public void mergingEmptyIterablesGivesAnEmptyIterable() {
    Matcher<Iterable<String>> iterableMatcher = emptyIterableOf(String.class);
    assertThat(mergeSorted(of(new ArrayList<String>(), new LinkedList<String>())), is(iterableMatcher));
  }

  @Ignore @Test public void mergingNonEmptyAndEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(of(of("a"), ImmutableList.<String> of())), contains("a"));
  }

  @Ignore @Test public void mergingEmptyAndNonEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(of(ImmutableList.<String> of(), of("a"))), contains("a"));
  }

  @Ignore @Test public void mergingNonEmptyIterablesInOrderGivesMergedIterable() {
    assertThat(mergeSorted(of(of("a"), of("b"))), contains("a", "b"));
  }

  @Ignore @Test public void mergingNonEmptyIterablesOutOfOrderGivesMergedIterable() {
    assertThat(mergeSorted(of(of("b"), of("a"))), contains("a", "b"));
  }

  @Ignore @Test public void mergingNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(mergeSorted(of(of("b", "d"), of("a", "c", "e"))), contains("a", "b", "c", "d", "e"));
  }

  @Ignore @Test public void mergingManyNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(mergeSorted(of(of("b", "d"), of("f", "x"), of("c", "e"), of("g", "h"), of("a", "z"))),
      contains("a", "b", "c", "d", "e", "f", "g", "h", "x", "z"));
  }

  @Ignore @Test public void mergedToString() {
    assertThat(mergeSorted(of(of("b", "d"), of("a", "c", "e"))).toString(), is("[a, b, c, d, e]"));
  }
}
