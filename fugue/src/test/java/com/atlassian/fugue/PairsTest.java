package com.atlassian.fugue;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static com.atlassian.fugue.Pair.pair;
import static com.atlassian.fugue.Pair.zip;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

// All tests here are for deprecated code moved to fugue-collect

public class PairsTest {
  @Test public void zipped() {
    final Iterable<Integer> ints = Arrays.asList(1, 2, 3);
    final Iterable<String> strings = Arrays.asList("1", "2", "3", "4");
    @SuppressWarnings("unchecked")
    final Matcher<Iterable<? extends Pair<Integer, String>>> contains = contains(pair(1, "1"), pair(2, "2"),
      pair(3, "3"));
    assertThat(zip(ints, strings), contains);
  }

  @Test public void zippedDiscardsLongest() {
    final Iterable<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    final Iterable<String> strings = Arrays.asList("1", "2", "3");
    assertThat(zip(ints, strings), Matchers.<Pair<Integer, String>> iterableWithSize(3));
  }
}
