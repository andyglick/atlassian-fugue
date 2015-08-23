package com.atlassian.fugue;

import org.junit.Test;

import java.util.Iterator;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IteratorAbstractTest {

  public <A> Iterator<A> newIterator(Supplier<A> s) {
    return new Iterators.Abstract<A>() {
      @Override protected A computeNext() {
        return s.get();
      }
    };
  }

  @Test(expected = IllegalStateException.class) public void testComputeNextThrows() {
    Iterator<Object> objectIterator = newIterator(() -> {
      throw new RuntimeException();
    });
    try {
      objectIterator.next();
    } catch (RuntimeException ignored) {}
    objectIterator.next();
  }

  @Test public void testEndOfData() {
    Iterator<String> stopingIterator = new Iterators.Abstract<String>() {
      boolean secondTime = false;

      @Override protected String computeNext() {
        if (!secondTime) {
          secondTime = true;
          return "first";
        }
        return endOfData();
      }
    };
    assertThat(stopingIterator.hasNext(), is(true));
    assertThat(stopingIterator.next(), is("first"));
    assertThat(stopingIterator.hasNext(), is(false));
  }

  @Test public void testHasNext() {
    Iterator<Integer> integerIterator = newIterator(() -> 1);
    assertThat(integerIterator.hasNext(), is(true));
  }

  @Test public void testNext() {
    Iterator<Integer> integerIterator = newIterator(() -> 1);
    assertThat(integerIterator.next(), is(1));
  }
}
