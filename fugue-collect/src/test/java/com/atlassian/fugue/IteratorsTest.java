package com.atlassian.fugue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.atlassian.fugue.Iterators.emptyIterator;
import static com.atlassian.fugue.Iterators.peekingIterator;
import static com.atlassian.fugue.Iterators.singletonIterator;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IteratorsTest {

  @Test(expected=IllegalStateException.class) public void removeAfterPeeking() throws Exception {
    PeekingIterator<Integer> integerPeekingIterator = peekingIterator(asList(1, 2).iterator());
    assertThat(integerPeekingIterator.peek(), is(1));
    integerPeekingIterator.remove();
  }

  @Test public void peekAfterRemoving() throws Exception {
    List<Integer> testList = new ArrayList<>();
    testList.add(1);
    testList.add(2);
    PeekingIterator<Integer> integerPeekingIterator = peekingIterator(testList.listIterator());
    integerPeekingIterator.next();
    integerPeekingIterator.remove();
    assertThat(integerPeekingIterator.peek(), is(2));
  }

  @Test public void testSingletonIterator() throws Exception {
    Iterator<Integer> integerIterator = singletonIterator(1);
    assertThat(integerIterator.hasNext(), is(true));
    assertThat(integerIterator.next(), is(1));
    assertThat(integerIterator.hasNext(), is(false));
  }


  @Test(expected=UnsupportedOperationException.class) public void iteratingTwiceFails() throws Exception {
    Iterator<Integer> integerIterator = singletonIterator(1);
    assertThat(integerIterator.next(), is(1));
    assertThat(integerIterator.hasNext(), is(false));
    integerIterator.next();
  }

  @Test public void testEmptyIterator() throws Exception {
    Iterator<Integer> intIter = emptyIterator();
    assertThat(intIter.hasNext(), is(false));
  }
}