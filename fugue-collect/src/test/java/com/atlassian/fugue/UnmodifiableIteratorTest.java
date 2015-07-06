package com.atlassian.fugue;

import org.junit.Test;

public class UnmodifiableIteratorTest {

  @SuppressWarnings("deprecated")
  @Test(expected=UnsupportedOperationException.class) public void testRemove() {
    UnmodifiableIterator<Integer> unmodifiableIterator = new UnmodifiableIterator<Integer>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public Integer next() {
        return null;
      }
    };

    unmodifiableIterator.remove();
  }
}