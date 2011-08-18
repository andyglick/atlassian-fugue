package com.atlassian.fugue;

import static com.atlassian.fugue.Pair.pair;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PairTest {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    pair(null, "");
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    pair("", null);
  }

  @Test public void left() {
    assertThat(pair("left", "right").left(), is("left"));
  }

  @Test public void right() {
    assertThat(pair("left", "right").right(), is("right"));
  }

  @Test public void toStringTest() {
    assertThat(pair("hello", 4).toString(), is("Pair(hello, 4)"));
  }

  @Test public void hashCodeTest() {
    assertThat(pair(1, 3).hashCode(), is(65539));
  }

  @Test public void notEqualToNull() {
    assertThat(pair(1, 3).equals(null), is(false));
  }

  @Test public void equalToSelf() {
    final Pair<Integer, Integer> pair = pair(1, 3);
    assertThat(pair.equals(pair), is(true));
  }

  @Test public void notEqualToArbitraryObject() {
    assertThat(pair(1, 3).equals(new Object()), is(false));
  }

  @Test public void notEqualLeft() {
    assertThat(pair(1, 3).equals(pair(0, 3)), is(false));
  }

  @Test public void notEqualRight() {
    assertThat(pair(1, 3).equals(pair(1, 0)), is(false));
  }

  @Test public void equalsSameValue() {
    assertThat(pair(1, 3).equals(pair(1, 3)), is(true));
  }
}
