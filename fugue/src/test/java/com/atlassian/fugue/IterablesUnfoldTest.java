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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.function.Function;

public class IterablesUnfoldTest {
  private static final Function<Integer, Option<Pair<String, Integer>>> F = i -> (i > 4) ? Option.<Pair<String, Integer>> none() : Option.some(Pair.pair(i.toString(), i + 1));

  @Test public void unfold() {
    assertThat(Iterables.unfold(F, 1), contains("1", "2", "3", "4"));
  }

  @Test(expected = NullPointerException.class) public void unfoldNull() {
    Iterables.unfold(null, 0);
  }
}
