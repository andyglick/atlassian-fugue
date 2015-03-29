package com.atlassian.fugue;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static com.atlassian.fugue.Iterables.size;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Options.find;
import static com.atlassian.fugue.Options2.filterNone;
import static com.atlassian.fugue.Options2.flatten;
import static com.google.common.collect.Iterables.isEmpty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class OptionsStaticsTest {
  static final Integer NULL = null;

  @Test
  public void identity() {
    assertThat(none(), is(sameInstance(none())));
  }

  @Test public void findFindsFirst() {
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
    assertThat(isEmpty(filterNone(fourNones())), is(true));
  }

  private ImmutableList<Option<Integer>> twoOptions() {
    return ImmutableList.of(option(NULL), option(2), option(NULL), option(4));
  }

  private ImmutableList<Option<Integer>> fourNones() {
    return ImmutableList.of(option(NULL), option(NULL), option(NULL), option(NULL));
  }
}
