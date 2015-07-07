package com.atlassian.fugue;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.atlassian.fugue.Iterables.filter;
import static com.atlassian.fugue.Iterables.size;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Options.filterNone;
import static com.atlassian.fugue.Options.find;
import static com.atlassian.fugue.Options.flatten;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class OptionsCollectTest {


  static final Integer NULL = null;

  private List<Option<Integer>> twoOptions() {
    return Arrays.asList(option(NULL), option(2), option(NULL), option(4));
  }

  private List<Option<Integer>> fourNones() {
    return Arrays.asList(option(NULL), option(NULL), option(NULL), option(NULL));
  }

  @Test
  public void findFindsFirst() {
    assertThat(find(twoOptions()).get(), is(2));
  }

  @Test public void findFindsOneSingleton() {
    assertThat(find(Arrays.asList(option(3))).get(), is(3));
  }

  @Test public void findFindsNone() {
    assertThat(find(fourNones()).isDefined(), is(false));
  }

  @Test public void findFindsNoneSingleton() {
    assertThat(find(Arrays.asList(option(NULL))).isDefined(), is(false));
  }

  @Test public void filterFindsTwo() {
    final Iterable<Option<Integer>> filtered = filterNone(twoOptions());
    assertThat(size(filtered), is(2));
    final Iterator<Option<Integer>> it = filtered.iterator();
    assertThat(it.next().get(), is(2));
    assertThat(it.next().get(), is(4));
    assertThat(it.hasNext(), is(false));
  }

  @Test public void flattenFindsTwo() {
    final Iterable<Integer> flattened = flatten(twoOptions());
    assertThat(size(flattened), is(2));
    final Iterator<Integer> it = flattened.iterator();
    assertThat(it.next(), is(2));
    assertThat(it.next(), is(4));
    assertThat(it.hasNext(), is(false));
  }

  @Test public void filterFindsNone() {
    assertThat(Iterables.isEmpty().test(filterNone(fourNones())), is(true));
  }

  @Test public void filterNones() {
    final List<Option<Integer>> list = Arrays.asList(some(1), none(Integer.class), some(2));
    MatcherAssert.assertThat(size(filterNone(list)), is(equalTo(2)));
  }

  @Test public void someDefined() {
    MatcherAssert.assertThat(filter(Arrays.asList(some(3)), Maybe::isDefined).iterator().hasNext(), is(true));
  }

  @Test public void noneNotDefined() {
    // throw new RuntimeException();
    MatcherAssert.assertThat(filter(Arrays.asList(none(int.class)), Maybe::isDefined).iterator().hasNext(), is(false));
  }

}
