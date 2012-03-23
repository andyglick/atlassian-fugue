package com.atlassian.fugue;

import static com.atlassian.fugue.Pair.pair;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPair {
  @Test(expected = NullPointerException.class) public void testNullLeft() {
    pair(null, "");
  }

  @Test(expected = NullPointerException.class) public void testNullRight() {
    pair("", null);
  }

  @Test public void testLeft() {
    assertEquals("left", pair("left", "right").left());
  }

  @Test public void testRight() {
    assertEquals("right", pair("left", "right").right());
  }

  @Test public void testToString() {
    assertEquals("Pair(hello, 4)", pair("hello", 4).toString());
  }

  @Test public void testHashCode() {
    assertEquals(65539, pair(1, 3).hashCode());
  }

  @Test public void testNotEqualToNull() {
    assertFalse(pair(1, 3).equals(null));
  }

  @Test public void testEqualToSelf() {
    final Pair<Integer, Integer> pair = pair(1, 3);
    assertTrue(pair.equals(pair));
  }

  @Test public void testNotEqualToArbitraryObject() {
    assertFalse(pair(1, 3).equals(new Object()));
  }

  @Test public void testNotEqualLeft() {
    assertFalse(pair(1, 3).equals(pair(0, 3)));
  }

  @Test public void testNotEqualRight() {
    assertFalse(pair(1, 3).equals(pair(1, 0)));
  }

  @Test public void testEqualsSameValue() {
    assertTrue(pair(1, 3).equals(pair(1, 3)));
  }
}
