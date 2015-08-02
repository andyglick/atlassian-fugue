package com.atlassian.fugue;

import org.junit.Test;

import static com.atlassian.fugue.Iterables.cycle;
import static com.atlassian.fugue.Iterables.take;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class IterablesCycleTest {

  @Test public void canCycle(){
    assertThat(take(5, cycle(1,2,3,4)), contains(1,2,3,4,1));
  }

  @Test public void canCycleFewerElements(){
    assertThat(take(2, cycle(1,2,3,4)), contains(1,2));
  }

  @Test(expected = NullPointerException.class) public void canNoNulls(){
    Iterable<Integer> c = cycle(1,2,null,4);
  }
}
