package io.atlassian.fugue;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import static io.atlassian.fugue.Iterables.unzip;
import static io.atlassian.fugue.Pair.pair;
import static io.atlassian.fugue.Pair.zip;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
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
    assertThat(zip(ints, strings), iterableWithSize(3));
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

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") private static <A, B> void assertZip(final Optional<A> o1, final Optional<B> o2,
    final Optional<Pair<A, B>> expected) {
    final Optional<Pair<A, B>> zip = zip(o1, o2);
    assertThat(zip, is(expected));
  }

  @Test public void zippingTwoEmptiesGivesAnEmpty() {
    assertZip(empty(), empty(), empty());
  }

  @Test public void zippingAnEmptyWithAFullGivesAnEmpty() {
    assertZip(empty(), Optional.of(42), empty());
  }

  @Test public void zippingAFullWithAnEmptyGivesAnEmpty() {
    assertZip(Optional.of(42), empty(), empty());
  }

  @Test public void zippingAFullWithAFullGivesAPair() {
    assertZip(Optional.of("a"), Optional.of("b"), Optional.of(pair("a", "b")));
  }
}
