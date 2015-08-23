package com.atlassian.fugue;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static com.atlassian.fugue.Iterables.unzip;
import static com.atlassian.fugue.Pair.pair;
import static com.atlassian.fugue.Pair.zip;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class PairsTest {
  @Test public void zipped() {
    final Iterable<Integer> ints = Arrays.asList(1, 2, 3);
    final Iterable<String> strings = Arrays.asList("1", "2", "3", "4");
    @SuppressWarnings("unchecked")
    final Matcher<Iterable<? extends Pair<Integer, String>>> contains = contains(pair(1, "1"), pair(2, "2"), pair(3, "3"));
    assertThat(zip(ints, strings), contains);
  }

  @Test public void zippedDiscardsLongest() {
    final Iterable<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    final Iterable<String> strings = Arrays.asList("1", "2", "3");
    assertThat(zip(ints, strings), Matchers.<Pair<Integer, String>> iterableWithSize(3));
  }

  @Test(expected = UnsupportedOperationException.class) public void zippedUnmodifiable() {
    final Iterable<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    final Iterable<String> strings = Arrays.asList("1", "2", "3");
    final Iterator<Pair<Integer, String>> zip = zip(ints, strings).iterator();
    zip.next();
    zip.remove();
  }

  @Test public void unzipLeft() {
    final Iterable<Pair<Integer, String>> pairs = Arrays.asList(pair(1, "1"), pair(2, "2"), pair(3, "3"), pair(4, "4"));
    final Pair<Iterable<Integer>, Iterable<String>> ls = unzip(pairs);
    assertThat(ls.left(), contains(1, 2, 3, 4));
  }

  @Test public void unzipRight() {
    final Iterable<Pair<Integer, String>> pairs = Arrays.asList(pair(1, "1"), pair(2, "2"), pair(3, "3"), pair(4, "4"));
    final Pair<Iterable<Integer>, Iterable<String>> ls = unzip(pairs);
    assertThat(ls.right(), contains("1", "2", "3", "4"));
  }
}
