package com.atlassian.fugue;

import org.junit.Test;

import java.util.Iterator;

public class UnmodifiableIteratorTest {

  @Test(expected=UnsupportedOperationException.class) public void testRemove() {
    final Iterators.Unmodifiable<Integer> unmodifiableIterator = new Iterators.Unmodifiable<Integer>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public Integer next() {
        return null;
      }
    };

    remove(unmodifiableIterator);
  }
  @SuppressWarnings("deprecation")
  private void remove(Iterator<Integer> i){
    i.remove();
  }
}