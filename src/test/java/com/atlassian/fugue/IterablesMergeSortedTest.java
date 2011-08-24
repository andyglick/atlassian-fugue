package com.atlassian.fugue;

import static com.atlassian.fugue.Iterables.mergeSorted;
import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.LinkedList;

public class IterablesMergeSortedTest {
  @Test public void assertThatMergingEmptyIterablesGivesAnEmptyIterable() {
    assertThat(mergeSorted(of(new ArrayList<String>(), new LinkedList<String>())), is(emptyIterable(String.class)));
  }

  @Test public void assertThatMergingNonEmptyAndEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(of(of("a"), ImmutableList.<String> of())), contains("a"));
  }

  @Test public void assertThatMergingEmptyAndNonEmptyIterablesGivesTheMergedIterable() {
    assertThat(mergeSorted(of(ImmutableList.<String> of(), of("a"))), contains("a"));
  }

  @Test public void assertThatMergingNonEmptyIterablesInOrderGivesMergedIterable() {
    assertThat(mergeSorted(of(of("a"), of("b"))), contains("a", "b"));
  }

  @Test public void assertThatMergingNonEmptyIterablesOutOfOrderGivesMergedIterable() {
    assertThat(mergeSorted(of(of("b"), of("a"))), contains("a", "b"));
  }

  @Test public void assertThatMergingNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(mergeSorted(of(of("b", "d"), of("a", "c", "e"))), contains("a", "b", "c", "d", "e"));
  }

  @Test public void assertThatMergingManyNonEmptyIterablesOutOfOrderGivesMergedIterableInOrder() {
    assertThat(mergeSorted(of(of("b", "d"), of("f", "x"), of("c", "e"), of("g", "h"), of("a", "z"))), contains("a", "b", "c", "d", "e", "f", "g",
      "h", "x", "z"));
  }

  @Test public void assertMergedToString() {
    assertThat(mergeSorted(of(of("b", "d"), of("a", "c", "e"))).toString(), is("[a, b, c, d, e]"));
  }

  private static <A> Matcher<java.lang.Iterable<A>> emptyIterable(final Class<A> a) {
    return Matchers.<A> emptyIterable();
  }
}
