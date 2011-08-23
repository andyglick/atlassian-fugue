package com.atlassian.fugue;

import static com.atlassian.fugue.Either.getOrThrow;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Options.filterNone;
import static com.atlassian.fugue.Options.find;
import static com.atlassian.fugue.Options.flatten;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.InvocationTargetException;
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
    assertThat(find(twoOptions()).get(), is(2));
  }

  @Test public void findFindsOneSingleton() {
    assertThat(find(ImmutableList.of(option(3))).get(), is(3));
  }

  @Test public void findFindsNone() {
    assertThat(find(fourNones()).isDefined(), is(false));
  }

  @Test public void findFindsNoneSingleton() {
    assertThat(find(ImmutableList.of(option(NULL))).isDefined(), is(false));
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

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Options> defaultCtor().apply(Options.class));
  }
}
