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

import java.util.Arrays;
import java.util.List;

import static com.atlassian.fugue.Pair.pair;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PairTest {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    pair(null, "");
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    pair("", null);
  }

  @Test public void left() {
    assertThat(pair("left", "right").left(), equalTo("left"));
  }

  @Test public void right() {
    assertThat(pair("left", "right").right(), equalTo("right"));
  }

  @Test public void toStringTest() {
    assertThat(pair("hello", 4).toString(), equalTo("Pair(hello, 4)"));
  }

  @Test public void hashCodeTest() {
    assertThat(pair(1, 3).hashCode(), equalTo(65539));
  }

  @Test public void notEqualToNull() {
    assertThat(pair(1, 3).equals(null), equalTo(false));
  }

  @Test public void equalToSelf() {
    final Pair<Integer, Integer> pair = pair(1, 3);
    assertThat(pair.equals(pair), equalTo(true));
  }

  @Test public void notEqualToArbitraryObject() {
    assertThat(pair(1, 3).equals(new Object()), equalTo(false));
  }

  @Test public void notEqualLeft() {
    assertThat(pair(1, 3).equals(pair(0, 3)), equalTo(false));
  }

  @Test public void notEqualRight() {
    assertThat(pair(1, 3).equals(pair(1, 0)), equalTo(false));
  }

  @Test public void equalsSameValue() {
    assertThat(pair(1, 3).equals(pair(1, 3)), equalTo(true));
  }

  @Test public void leftFunction() {
    assertEquals(pairs().get(0).left().longValue(), 1);
    assertEquals(pairs().get(1).left().longValue(), 2);
    assertEquals(pairs().get(2).left().longValue(), 3);
  }

  @Test public void rightFunction() {
    assertEquals(pairs().get(0).right(), "1");
    assertEquals(pairs().get(1).right(), "2");
    assertEquals(pairs().get(2).right(), "3");
  }

  private List<Pair<Integer, String>> pairs() {
    return Arrays.asList(pair(1, "1"), pair(2, "2"), pair(3, "3"));
  }
}
