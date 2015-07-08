/*
   Copyright 2011 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.atlassian.fugue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.atlassian.fugue.Iterables.filter;
import static com.atlassian.fugue.Iterables.memoize;
import static com.atlassian.fugue.Iterables.transform;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("unused") public class IterablesMemoizeTest {
  @Test public void assertThatFunctionTransformingSingletonIterableIsOnlyCalledOnce() {
    final CountingFunction<Integer, String> toString = counting(Object::toString);
    final Iterable<String> memoized = memoize(transform(Arrays.asList(1), toString::apply));

    // iterate over it a few times
    for (final String ignore : memoized) {}
    for (final String ignore : memoized) {}

    assertThat(toString.count.get(), is(equalTo(1)));
  }

  @Test public void assertThatFunctionTransformingMultiElementIterableIsOnlyCalledOncePerElement() {
    final CountingFunction<Integer, String> toString = counting(Object::toString);
    final Iterable<String> memoized = memoize(transform(Arrays.asList(1, 2, 3, 4), toString::apply));

    // iterate over it a few times
    for (final String ignore : memoized) {}
    for (final String ignore : memoized) {}

    assertThat(toString.count.get(), is(equalTo(4)));
  }

  @Test public void assertThatMemoizedTransformedIterableHasSameElementsAsOriginalIterable() {
    CountingFunction<Integer, String> counting = counting(Object::toString);
    assertThat(memoize(transform(Arrays.asList(1, 2, 3, 4), counting::apply)), contains("1", "2", "3", "4"));
  }

  @Test public void assertThatMemoizedTransformedIterableHasSameElementsAsOriginalIterableOnSecondIteration() {
    CountingFunction<Integer, String> counting = counting(Object::toString);
    final Iterable<String> memoized = memoize(transform(Arrays.asList(1, 2, 3, 4), counting::apply));
    for (final String ignore : memoized) {}
    assertThat(memoized, contains("1", "2", "3", "4"));
  }

  @Test public void assertThatPredicateUsedWhenFilteringIterableIsOnlyCalledOncePerElement() {
    final CountingPredicate<Integer> even = counting(even());
    Iterable<Integer> filtered = filter(Arrays.asList(1, 2, 3, 4), even::test);
    final Iterable<Integer> memoized = memoize(filtered);

    // iterate over it a few times
    for (final Integer ignore : memoized) {}
    for (final Integer ignore : memoized) {}

    assertThat(even.count.get(), is(equalTo(4)));
  }

  @Test public void assertThatMemoizedFilteredIterableHasSameElementsAsOriginalIterableMinusFilteredElements() {
    final CountingPredicate<Integer> even = counting(even());
    Iterable<Integer> filtered = filter(Arrays.asList(1, 2, 3, 4), even::test);
    assertThat(memoize(filtered), contains(2, 4));
  }

  @Test public void assertThatMemoizedFilteredIterableHasSameElementsAsOriginalIterableMinusFitleredElementsOnSecondIteration() {
    final CountingPredicate<Integer> even = counting(even());
    Iterable<Integer> filtered = filter(Arrays.asList(1, 2, 3, 4), even::test);
    final Iterable<Integer> memoized = memoize(filtered);

    for (final Integer ignore : memoized) {}

    assertThat(memoized, contains(2, 4));
  }

  @Test public void assertThatIteratingHalfWayThroughMemoizedIterableAndThenIteratingCompletelyHasSameElementsOriginalIterable() {
    final Iterable<String> memoized = memoize(transform(Arrays.asList(1, 2, 3, 4), Object::toString));
    Iterator<String> memIt = memoized.iterator();
    memIt.next();
    memIt.next();
    assertThat(memoized, contains("1", "2", "3", "4"));
  }

  @Test public void assertToString() {
    final Iterable<String> memoized = memoize(transform(Arrays.asList(1, 2, 3, 4), Object::toString));
    Iterator<String> memIt = memoized.iterator();
    memIt.next();
    memIt.next();
    assertThat(memoized.toString(), is("[1, 2, 3, 4]"));
  }

  private <A, B> CountingFunction<A, B> counting(final Function<A, B> f) {
    return new CountingFunction<>(f);
  }

  static final class CountingFunction<A, B> implements Function<A, B> {
    private final Function<A, B> f;
    private final AtomicInteger count = new AtomicInteger();

    public CountingFunction(final Function<A, B> f) {
      this.f = f;
    }

    public B apply(final A a) {
      count.incrementAndGet();
      return f.apply(a);
    }
  }

  private <A> CountingPredicate<A> counting(final Predicate<A> p) {
    return new CountingPredicate<A>(p);
  }

  static final class CountingPredicate<A> implements Predicate<A> {
    private final Predicate<A> p;
    private final AtomicInteger count = new AtomicInteger();

    public CountingPredicate(final Predicate<A> p) {
      this.p = p;
    }

    public boolean test(final A a) {
      count.incrementAndGet();
      return p.test(a);
    }
  }

  private Predicate<Integer> even() {
    return EvenPredicate.INSTANCE;
  }

  private enum EvenPredicate implements Predicate<Integer> {
    INSTANCE;

    public boolean test(final Integer i) {
      return i % 2 == 0;
    }
  }

}
