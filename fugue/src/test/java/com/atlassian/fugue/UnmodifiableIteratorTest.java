package com.atlassian.fugue;

import org.junit.Test;

public class UnmodifiableIteratorTest {

  @SuppressWarnings("deprecation") @Test(expected=UnsupportedOperationException.class) public void testRemove() {
    Iterators.Unmodifiable<Integer> unmodifiableIterator = new Iterators.Unmodifiable<Integer>() {
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