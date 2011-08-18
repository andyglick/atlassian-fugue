package com.atlassian.fugue;

import static com.atlassian.fugue.Option.filterNone;
import static com.atlassian.fugue.Option.find;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

public class OptionStaticsTest {
  static final Integer NULL = null;

  @Test public void getNull() {
    assertThat(option(null), is(sameInstance(none())));
  }

  @Test public void get() {
    assertThat(option("Winter.").get(), is("Winter."));
  }

  @Test public void identity() {
    assertThat(none(), is(sameInstance(none())));
  }

  @Test public void findFindsFirst() {
    final Iterable<Option<Integer>> options = twoOptions();
    assertThat(find(options).get(), is(2));
  }

  @Test public void findFindsOneSingleton() {
    final Iterable<Option<Integer>> options = ImmutableList.of(option(3));
    assertThat(find(options).get(), is(3));
  }

  @Test public void findFindsNone() {
    final Iterable<Option<Integer>> options = fourNones();
    assertThat(find(options).isDefined(), is(false));
  }

  @Test public void findFindsNoneSingleton() {
    final Iterable<Option<Integer>> options = ImmutableList.of(option(NULL));
    assertThat(find(options).isDefined(), is(false));
  }

  @Test public void filterFindsTwo() {
    final Iterable<Option<Integer>> options = twoOptions();
    final Iterable<Option<Integer>> filtered = filterNone(options);
    assertThat(size(filtered), is(2));
    final Iterator<Option<Integer>> it = filtered.iterator();
    assertThat(it.next().get(), is(2));
    assertThat(it.next().get(), is(4));
    assertThat(it.hasNext(), is(false));
  }

  @Test public void filterFindsNone() {
    final Iterable<Option<Integer>> options = fourNones();
    assertThat(isEmpty(filterNone(options)), is(true));
  }

  private ImmutableList<Option<Integer>> twoOptions() {
    return ImmutableList.of(option(NULL), option(2), option(NULL), option(4));
  }

  private ImmutableList<Option<Integer>> fourNones() {
    return ImmutableList.of(option(NULL), option(NULL), option(NULL), option(NULL));
  }
}
