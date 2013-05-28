package com.atlassian.fugue;

import static com.atlassian.fugue.Option.some;

import org.junit.Test;

import com.google.common.base.Function;

public class FunctionMatcherTest {
  Function<Integer, Option<Integer>> toInt(final int check) {
    return new Function<Integer, Option<Integer>>() {
      @Override public Option<Integer> apply(Integer input) {
        return (check == input) ? some(input) : Option.<Integer> none();
      }
    };
  }

  @Test(expected = NullPointerException.class) public void nullFirst() {
    Functions.matches(null, toInt(1));
  }

  @Test(expected = NullPointerException.class) public void nullSecond() {
    Functions.compose(toInt(1), null);
  }

}
