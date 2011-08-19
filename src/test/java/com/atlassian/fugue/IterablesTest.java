package com.atlassian.fugue;

import static com.atlassian.fugue.Iterables.findFirst;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Pair.pair;
import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class IterablesTest {

  private final Predicate<Integer> grepOne = new Predicate<Integer>() {
    public boolean apply(final Integer input) {
      return new Integer(1).equals(input);
    }
  };
  private final Option<Integer> none = Option.<Integer> none();

  @Test public void findFirstEmpty() {
    assertThat(findFirst(ImmutableList.<Integer> of(), grepOne), is(Option.<Integer> none()));
  }

  @Test public void findFirstAbsent() {
    assertThat(findFirst(of(2), grepOne), is(none));
  }

  @Test public void findFirstSingle() {
    assertThat(findFirst(of(1), grepOne), is(some(1)));
  }

  @Test public void findFirstWhenNotFirstElement() {
    assertThat(findFirst(of(2, 1), grepOne), is(some(1)));
  }

  @Test public void findFirstMultipleMatches() {
    final Pair<Integer, Integer> expected = pair(1, 1);
    final List<Pair<Integer, Integer>> ts = ImmutableList.of(expected, pair(2, 2), pair(1, 3), pair(2, 4));

    final Option<Pair<Integer, Integer>> found = findFirst(ts, new Predicate<Pair<Integer, Integer>>() {
      public boolean apply(final Pair<Integer, Integer> input) {
        return input.left().equals(1);
      }
    });

    assertThat(found, is(Option.some(expected)));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    final Constructor<Iterables> declaredConstructor = Iterables.class.getDeclaredConstructor();
    declaredConstructor.setAccessible(true);
    declaredConstructor.newInstance();
  }
}