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

import java.util.function.Function;

import static io.atlassian.fugue.Option.some;
import static io.atlassian.fugue.UtilityFunctions.toStringFunction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OptionCompositionTest {
  @Test public void composeLaw() {
    Function<Integer, Integer> plusOne = input -> input + 1;
    assertThat(some(1).map(plusOne).map(toStringFunction()), is(some(1).map(Functions.compose(toStringFunction(), plusOne))));
  }

  @Test public void composeNull() {
    Function<Integer, Integer> nasty = input -> null;
    Function<Object, String> constant = Functions.constant("foo");
    assertThat(some(1).map(nasty).map(constant), is(some(1).map(Functions.compose(constant, nasty))));
  }
}
