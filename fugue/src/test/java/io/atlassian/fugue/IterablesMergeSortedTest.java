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

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static io.atlassian.fugue.Iterables.mergeSorted;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterableOf;
import static org.hamcrest.Matchers.is;

public class IterablesMergeSortedTest {
  @Test public void mergingEmptyIterablesGivesAnEmptyIterable() {
    final Matcher<Iterable<String>> iterableMatcher = emptyIterableOf(String.class);
    assertThat(mergeSorted(Arrays.asList(new ArrayList<String>(), new LinkedList<String>())), is(iterableMatcher));
  }

  @Test public void mergingNonEmptyAndEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(Arrays.asList(singletonList("a"), emptyList())), contains("a"));
  }

  @Test public void mergingEmptyAndNonEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(Arrays.asList(emptyList(), singletonList("a"))), contains("a"));
  }

  @Test public void mergingNonEmptyIterablesInOrderGivesMergedIterable() {
    assertThat(mergeSorted(Arrays.asList(singletonList("a"), singletonList("b"))), contains("a", "b"));
  }

  @Test public void mergingNonEmptyIterablesOutOfOrderGivesMergedIterable() {
    assertThat(mergeSorted(Arrays.asList(singletonList("b"), singletonList("a"))), contains("a", "b"));
  }

  @Test public void mergingNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(mergeSorted(Arrays.asList(Arrays.asList("b", "d"), Arrays.asList("a", "c", "e"))), contains("a", "b", "c", "d", "e"));
  }

  @Test public void mergingManyNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(
      mergeSorted(Arrays.asList(Arrays.asList("b", "d"), Arrays.asList("f", "x"), Arrays.asList("c", "e"), Arrays.asList("g", "h"),
        Arrays.asList("a", "z"))), contains("a", "b", "c", "d", "e", "f", "g", "h", "x", "z"));
  }

  @Test public void mergedToString() {
    assertThat(mergeSorted(Arrays.asList(Arrays.asList("b", "d"), Arrays.asList("a", "c", "e"))).toString(), is("[a, b, c, d, e]"));
  }
}
