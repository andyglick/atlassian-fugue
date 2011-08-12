package com.atlassian.fugue;

import com.google.common.base.Predicate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.atlassian.fugue.Pair.pair;
import static junit.framework.Assert.assertEquals;

public class IterablesTest {

  private final Predicate<Integer> grepOne = new Predicate<Integer>() {
    public boolean apply(Integer input) {
      return new Integer(1).equals(input);
    }
  };

  @Test
  public void findEmpty() {
    Iterable<Integer> ts = new LinkedList<Integer>();

    Option<Integer> integerOption = Iterables.find(ts, grepOne);
    assertEquals(Option.<Integer>none(), integerOption);
  }

  @Test
  public void findAbsent() {

    List<Integer> ts = new LinkedList<Integer>();
    ts.add(2);

    Option<Integer> integerOption = Iterables.find(ts, grepOne);
    assertEquals(Option.<Integer>none(), integerOption);
  }

  @Test
  public void findSingle() {

    List<Integer> ts = new ArrayList<Integer>();
    ts.add(1);

    Option<Integer> integerOption = Iterables.find(ts, grepOne);
    assertEquals(Option.<Integer>some(1), integerOption);
  }

  @Test
  public void findFirstWhen() {

    List<Pair<Integer, Integer>> ts = new LinkedList<Pair<Integer, Integer>>();
    Pair<Integer, Integer> expected = pair(1, 1);
    ts.add(expected);
    ts.add(pair(2,2));
    ts.add(pair(1,3));
    ts.add(pair(2, 4));

    Option<Pair<Integer, Integer>> found = Iterables.find(ts, new Predicate<Pair<Integer, Integer>>(){
      public boolean apply(Pair<Integer, Integer> input) {
        return new Integer(1).equals(input.left());
      }
    });
    
    assertEquals(Option.some(expected), found);
  }

}
