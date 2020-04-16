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

import static io.atlassian.fugue.Functions.compose;
import static io.atlassian.fugue.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class FunctionComposeTest {
  Function<String, Option<Integer>> toInt = input -> {
    try {
      return Option.some(Integer.parseInt(input));
    } catch (NumberFormatException e) {
      return Option.none();
    }
  };

  Function<Integer, Option<String>> toString = input -> Option.some(input.toString());

  @Test public void composeNotNull() {
    assertThat(Functions.composeOption(toString, toInt), notNullValue());
  }

  @Test(expected = NullPointerException.class) public void nullFirst() {
    compose(null, toInt);
  }

  @Test(expected = NullPointerException.class) public void nullSecond() {
    compose(toInt, null);
  }

  @Test public void someForInt() {
    assertThat(Functions.composeOption(toString, toInt).apply("12"), is(some("12")));
  }

  @Test public void noneForNonParsable() {
    assertThat(Functions.composeOption(toString, toInt).apply("twelve"), is(Option.<String> none()));
  }

  @Test public void referenceEqualityOfComposition() {
    final Function<Integer, Integer> intFunc = (a) -> a + 1;
    final Function<Integer, Double> intDouble = (a) -> a + 1.0;
    assertEquals(compose(intDouble, intFunc), compose(intDouble, intFunc));
  }
}
