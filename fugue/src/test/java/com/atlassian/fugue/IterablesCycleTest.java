package com.atlassian.fugue;

import org.junit.Test;

import static com.atlassian.fugue.Iterables.cycle;
import static com.atlassian.fugue.Iterables.take;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.core.Is.is;

public class IterablesCycleTest {

  @Test public void canCycle(){
    assertThat(take(5, cycle(1,2,3,4)), contains(1,2,3,4,1));
  }

  @Test public void canCycleFewerElements(){
    assertThat(take(2, cycle(1,2,3,4)), contains(1,2));
  }

  @Test public void canCycleNoElements(){
    assertThat(cycle(), emptyIterable());
  }

  @Test public void toStringResult(){
    assertThat(cycle(1,2,3).toString(), is("[1, 2, 3, ...]"));
  }

  @Test public void toStringResultNull(){
    assertThat(cycle(1,2,null).toString(), is("[1, 2, null, ...]"));
  }
}
