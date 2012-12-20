package com.atlassian.fugue;

import static com.atlassian.fugue.Option.some;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.annotation.Nullable;

import org.junit.Test;

import com.google.common.base.Function;

public class FunctionComposeTest {
  Function<String, Option<Integer>> toInt = new Function<String, Option<Integer>>() {
    @Override public Option<Integer> apply(@Nullable String input) {
      try {
        return Option.some(Integer.parseInt(input));
      } catch (NumberFormatException e) {
        return Option.none();
      }
    }
  };

  Function<Integer, Option<String>> toString = new Function<Integer, Option<String>>() {
    @Override public Option<String> apply(@Nullable Integer input) {
      return Option.some(input.toString());
    }
  };

  @Test public void composeNotNull() {
    assertThat(Functions.compose(toInt, toString), notNullValue());
  }

  @Test(expected = NullPointerException.class) public void nullFirst() {
    Functions.compose(null, toInt);
  }

  @Test(expected = NullPointerException.class) public void nullSecond() {
    Functions.compose(toInt, null);
  }

  @Test public void someForInt() {
    assertThat(Functions.compose(toInt, toString).apply("12"), is(some("12")));
  }

  @Test public void noneForNonParsable() {
    assertThat(Functions.compose(toInt, toString).apply("twelve"), is(Option.<String> none()));
  }
}
