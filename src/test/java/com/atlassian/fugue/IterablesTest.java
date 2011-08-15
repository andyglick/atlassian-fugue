package com.atlassian.fugue;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.atlassian.fugue.Pair.pair;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IterablesTest {

  private final Predicate<Integer> grepOne = new Predicate<Integer>() {
    public boolean apply(Integer input) {
      return new Integer(1).equals(input);
    }
  };

  @Test
  public void findFirstEmpty() {
    Iterable<Integer> ts = new LinkedList<Integer>();

    Option<Integer> integerOption = Iterables.findFirst(ts, grepOne);
    assertThat(integerOption, is(Option.<Integer>none()));
  }

  @Test
  public void findFirstAbsent() {

    Option<Integer> integerOption = Iterables.findFirst(ImmutableList.of(2), grepOne);
    assertThat(integerOption, is(Option.<Integer>none()));
  }

  @Test
  public void findFirstSingle() {

    Option<Integer> integerOption = Iterables.findFirst(ImmutableList.of(1), grepOne);
    assertThat(integerOption, is(Option.<Integer>some(1)));
  }

  @Test
  public void findFirstWhenNotFirstElement() {

    Option<Integer> integerOption = Iterables.findFirst(ImmutableList.of(2,1), grepOne);
    assertThat(integerOption, is(Option.<Integer>some(1)));
  }
  
  @Test
  public void findFirstMultipleMatches() {

    Pair<Integer, Integer> expected = pair(1, 1);
    List<Pair<Integer, Integer>> ts = ImmutableList.of(expected, pair(2, 2), pair(1, 3), pair(2, 4));
    
    Option<Pair<Integer, Integer>> found = Iterables.findFirst(ts, new Predicate<Pair<Integer, Integer>>() {
      public boolean apply(Pair<Integer, Integer> input) {
        return new Integer(1).equals(input.left());
      }
    });
    
    assertThat(found, is(Option.some(expected)));
  }

}
