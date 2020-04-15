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

import org.junit.Test;

import java.util.Iterator;
import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IterablesIterateTest {
  private static final Function<Integer, Integer> INC = i -> i + 1;

  @Test public void iterate() {
    final Iterator<Integer> iterator = Iterables.iterate(INC, 1).iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), is(1));
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), is(2));
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), is(3));
    assertThat(iterator.hasNext(), is(true));
  }

  @Test(expected = NullPointerException.class) public void iterateNull() {
    Iterables.iterate(null, 0);
  }
}
