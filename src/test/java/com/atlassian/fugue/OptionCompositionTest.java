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
import static com.google.common.base.Functions.compose;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.google.common.base.Function;

public class OptionCompositionTest {
  @Test public void composeLaw() {
    Function<Integer, Integer> plusOne = new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input + 1;
      }
    };
    assertThat(some(1).map(plusOne).map(Functions.toStringFunction()), is(some(1).map(compose(Functions.toStringFunction(), plusOne))));
  }

  @Test public void composeNull() {
    Function<Integer, Integer> nasty = new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return null;
      }
    };
    Function<Object, String> constant = Functions.constant("foo");
    assertThat(some(1).map(nasty).map(constant), is(some(1).map(compose(constant, nasty))));
  }
}
