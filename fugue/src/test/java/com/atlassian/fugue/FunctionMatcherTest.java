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

import org.junit.Test;

import java.util.function.Function;

import static com.atlassian.fugue.Functions.compose;
import static com.atlassian.fugue.Functions.matches;
import static com.atlassian.fugue.Option.some;

public class FunctionMatcherTest {
  Function<Integer, Option<Integer>> toInt(final int check) {
    return input -> (check == input) ? some(input) : Option.<Integer> none();
  }

  @Test(expected = NullPointerException.class) public void nullFirst() {
    matches(null, toInt(1));
  }

  @Test(expected = NullPointerException.class) public void nullSecond() {
    compose(toInt(1), null);
  }

}
