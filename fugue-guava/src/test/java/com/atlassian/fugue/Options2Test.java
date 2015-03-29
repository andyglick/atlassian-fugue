package com.atlassian.fugue;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.atlassian.fugue.Iterables.size;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Options2.filterNone;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class Options2Test {

  @Test
  public void filterNones() {
    final List<Option<Integer>> list = Arrays.asList(some(1), none(Integer.class), some(2));
    assertThat(size(filterNone(list)), is(equalTo(2)));
  }

  @Test public void someDefined() {
    assertThat(com.google.common.collect.Iterables.filter(ImmutableList.of(some(3)), Maybe::isDefined).iterator().hasNext(), is(true));
  }

  @Test public void noneNotDefined() {
    //throw new RuntimeException();
    assertThat(com.google.common.collect.Iterables.filter(ImmutableList.of(none(int.class)), Maybe::isDefined).iterator().hasNext(), is(false));
  }
}