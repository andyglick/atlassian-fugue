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

import static com.atlassian.fugue.Option.some;

import org.junit.Test;

import com.google.common.base.Function;

public class FunctionMatcherTest {
  Function<Integer, Option<Integer>> toInt(final int check) {
    return new Function<Integer, Option<Integer>>() {
      @Override public Option<Integer> apply(Integer input) {
        return (check == input) ? some(input) : Option.<Integer> none();
      }
    };
  }

  @Test(expected = NullPointerException.class) public void nullFirst() {
    Functions.matches(null, toInt(1));
  }

  @Test(expected = NullPointerException.class) public void nullSecond() {
    Functions.compose(toInt(1), null);
  }

}
