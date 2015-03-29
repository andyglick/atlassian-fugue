package com.atlassian.fugue;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Option.some;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class OptionSomeTest {

  private static final Integer ORIGINAL_VALUE = 1;
  Option<Integer> some = some(ORIGINAL_VALUE);

  @Test
  public void map() {
    assertThat(some.map(i -> i + 1).get(), is(2));
  }

  @Test public void superTypesPermittedOnFilter() {
    final ArrayList<Integer> list = Lists.newArrayList(1, 2);
    final Option<ArrayList<Integer>> option = option(list);
    final Option<ArrayList<Integer>> nopt = option.filter(Predicates.<List<Integer>> alwaysTrue()::apply);
    assertThat(nopt, sameInstance(option));
  }

  @Test public void superTypesPermittedOnMap() {
    final Option<ArrayList<Integer>> option = option(Lists.newArrayList(1, 2));
    final Option<Set<Number>> set = option.map(new Function<List<Integer>, Set<Number>>() {
      public Set<Number> apply(final List<Integer> list) {
        return Sets.<Number> newHashSet(list);
      }
    });
    assertThat(set.get().size(), is(option.get().size()));
  }

}
