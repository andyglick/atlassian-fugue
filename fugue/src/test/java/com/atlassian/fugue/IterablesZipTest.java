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

import org.hamcrest.Matcher;
import org.junit.Test;

import static com.atlassian.fugue.Iterables.zip;
import static com.atlassian.fugue.Iterables.zipWith;
import static com.atlassian.fugue.Iterables.zipWithIndex;
import static com.atlassian.fugue.Pair.pair;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class IterablesZipTest {
  @SuppressWarnings("unchecked") @Test public void zipFromLists() {
    assertThat(zip(asList(4, 3, 2, 1), asList(1, 2, 3, 4)), contains(pair(4, 1), pair(3, 2), pair(2, 3), pair(1, 4)));
  }

  @SuppressWarnings("unchecked") @Test public void zipFromLongerFirstList() {
    assertThat(zip(asList(4, 2, 3, 1, 12), asList(1, 2, 3, 4)), contains(pair(4, 1), pair(2, 2), pair(3, 3), pair(1, 4)));
  }

  @SuppressWarnings("unchecked") @Test public void zipFromLongerLastList() {
    assertThat(zip(asList(4, 3, 2, 1), asList(6, 2, 5, 4, 5, 6)), contains(pair(4, 6), pair(3, 2), pair(2, 5), pair(1, 4)));
  }

  @Test public void zipWithFrom() {
    assertThat(zipWith(UtilityFunctions.product).apply(asList(4, 3, 2, 8), asList(2, 4, 6, 8)), contains(8, 12, 12, 64));
  }

  @Test public void testZipWithIndex() {
    @SuppressWarnings("unchecked")
    Matcher<Iterable<? extends Pair<String, Integer>>> containsPairs = contains(pair("a", 0), pair("b", 1), pair("c", 2));
    assertThat(zipWithIndex(asList("a", "b", "c")), containsPairs);
  }
}
