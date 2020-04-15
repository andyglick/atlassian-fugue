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
package io.atlassian.fugue;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.atlassian.fugue.Iterables.all;
import static io.atlassian.fugue.Iterables.any;
import static io.atlassian.fugue.Iterables.emptyIterable;
import static io.atlassian.fugue.Iterables.findFirst;
import static io.atlassian.fugue.Iterables.join;
import static io.atlassian.fugue.Iterables.map;
import static io.atlassian.fugue.Iterables.partition;
import static io.atlassian.fugue.Iterables.rangeTo;
import static io.atlassian.fugue.Iterables.rangeUntil;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class IterablesTest {

  private final Predicate<Integer> grepOne = input -> Objects.equals(input, 1);
  private final Option<Integer> none = Option.<Integer> none();

  @Test public void emptyIterableIteratorHasNext() {
    assertThat(emptyIterable().iterator().hasNext(), is(false));
  }

  @Test(expected = NoSuchElementException.class) public void emptyIterableIteratorNext() {
    emptyIterable().iterator().next();
  }

  @Test(expected = UnsupportedOperationException.class) public void emptyIterableIteratorRemove() {
    emptyIterable().iterator().remove();
  }

  @Test public void emptyIterablesAreEqual() {
    assertThat(emptyIterable(), is(emptyIterable()));
  }

  @Test public void emptyIterablesToString() {
    assertThat(emptyIterable().toString(), is("[]"));
  }

  @Test public void findFirstEmpty() {
    assertThat(findFirst(emptyList(), grepOne), is(Option.<Integer> none()));
  }

  @Test public void findFirstAbsent() {
    assertThat(findFirst(singletonList(2), grepOne), is(none));
  }

  @Test public void findFirstSingle() {
    assertThat(findFirst(singletonList(1), grepOne), is(Option.some(1)));
  }

  @Test public void findFirstWhenNotFirstElement() {
    assertThat(findFirst(asList(2, 1), grepOne), is(Option.some(1)));
  }

  @Test public void findFirstMultipleMatches() {
    final Pair<Integer, Integer> expected = Pair.pair(1, 1);
    final List<Pair<Integer, Integer>> ts = Arrays.asList(expected, Pair.pair(2, 2), Pair.pair(1, 3), Pair.pair(2, 4));

    final Option<Pair<Integer, Integer>> found = findFirst(ts, input -> input.left().equals(1));

    assertThat(found, is(Option.some(expected)));
  }

  @Test public void rangeToSingle() {
    assertThat(rangeTo(1, 5), is(contains(1, 2, 3, 4, 5)));
  }

  @Test public void rangeToSingleNegative() {
    assertThat(rangeTo(5, 1), is(contains(5, 4, 3, 2, 1)));
  }

  @Test public void rangeUntilSingle() {
    assertThat(rangeUntil(1, 5), is(contains(1, 2, 3, 4)));
  }

  @Test public void rangeUntilSingleNegative() {
    assertThat(rangeUntil(5, 1), is(contains(5, 4, 3, 2)));
  }

  @Test public void rangeToStep() {
    assertThat(rangeTo(1, 5, 2), is(contains(1, 3, 5)));
  }

  @Test public void rangeToMaxValue() {
    assertThat(rangeTo(Integer.MAX_VALUE - 1, Integer.MAX_VALUE), is(contains(Integer.MAX_VALUE - 1, Integer.MAX_VALUE)));
  }

  @Test public void rangeToMinValue() {
    assertThat(rangeTo(Integer.MIN_VALUE + 1, Integer.MIN_VALUE), is(contains(Integer.MIN_VALUE + 1, Integer.MIN_VALUE)));
  }

  @Test(expected = NoSuchElementException.class) public void rangeToIterator() {
    final Iterator<Integer> iterator = rangeTo(1, 1, 1).iterator();
    iterator.next();
    iterator.next();
  }

  @Test public void rangeUntilStep() {
    assertThat(rangeUntil(1, 5, 2), is(contains(1, 3)));
  }

  @Test public void rangeToNegativeStep() {
    assertThat(rangeTo(8, -1, -3), is(contains(8, 5, 2, -1)));
  }

  @Test public void rangeUntilNegativeStep() {
    assertThat(rangeUntil(8, -1, -3), is(contains(8, 5, 2)));
  }

  @Test public void rangeToEqual() {
    assertThat(rangeTo(1, 1), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeToNegative() {
    assertThat(rangeTo(1, 2, -1), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeToBackwardsPositive() {
    assertThat(rangeTo(2, 1, 1), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeToZero() {
    assertThat(rangeTo(1, 2, 0), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeUntilNegative() {
    assertThat(rangeUntil(1, 2, -1), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeUntilBackwardsPositive() {
    assertThat(rangeUntil(2, 1, 1), is(contains(1)));
  }

  @Test(expected = IllegalArgumentException.class) public void rangeUntilZero() {
    assertThat(rangeUntil(1, 2, 0), is(contains(1)));
  }

  @Test public void partitionSimple() {
    final Pair<Iterable<Integer>, Iterable<Integer>> part = partition(asList(1, 2, 3, 4), i -> i > 2);
    assertThat(part.left(), contains(3, 4));
    assertThat(part.right(), contains(1, 2));
  }

  @Test public void flatMapConcatenates() {
    final Iterable<String> result = Iterables.flatMap(asList("123", "ABC"), CharSplitter::new);
    assertThat(result, contains("1", "2", "3", "A", "B", "C"));
  }

  @Test public void findFirstFunctionWorks() {
    assertThat(findFirst(Predicate.isEqual(3)).apply(asList(1, 2, 3)), is(Option.some(3)));
  }

  @Test public void findFirstFunctionFails() {
    assertThat(findFirst(Predicate.isEqual(3)).apply(asList(1, 2, 4)), is(Option.<Integer> none()));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    Eithers.getOrThrow(UtilityFunctions.<Iterables> defaultCtor().apply(Iterables.class));
  }

  @Test public void revMap() {
    final Iterable<Function<Integer, Integer>> fs = asList(from -> from + 1, from -> from + 2, from -> from * from);
    assertThat(Iterables.revMap(fs, 3), contains(4, 5, 9));
  }

  @Test public void flattenCollapses() {
    final Iterable<Iterable<Integer>> iterables = asList(singletonList(1), singletonList(2));
    assertThat(join(iterables), contains(1, 2));
  }

  @Test public void findAnyMatching() {
    assertThat(any(Arrays.asList(1, 2, 3), ii -> ii > 2), is(true));
  }

  @Test public void findAnyNoMatching() {
    assertThat(any(Arrays.asList(1, 2, 3), ii -> ii < 0), is(false));
  }

  @Test public void findAnyEmpty() {
    assertThat(any(Collections.<Integer> emptyList(), ii -> ii < 0), is(false));
  }

  @Test public void findAllMatching() {
    assertThat(all(Arrays.asList(1, 2, 3), ii -> ii > 0), is(true));
  }

  @Test public void findAllNoMatching() {
    assertThat(all(Arrays.asList(1, 2, 3), ii -> ii < 2), is(false));
  }

  @Test public void findAllEmpty() {
    assertThat(all(Collections.<Integer> emptyList(), ii -> ii < 0), is(true));
  }

  @Test public void mapChangesIterable() {
    assertThat(map(Arrays.asList(1, 2, 3), i -> i + 1), contains(2, 3, 4));
  }

  @Test public void mappingNull() {
    assertThat(map(Arrays.asList(1, 2, 3), i -> null), everyItem(nullValue()));
  }

  /**
   * Splits a string into characters.
   */
  static class CharSplitter implements Iterable<String> {
    private final CharSequence from;

    CharSplitter(final CharSequence from) {
      this.from = from;
    }

    @Override public Iterator<String> iterator() {
      return new Iterators.Abstract<String>() {
        int index = 0;

        @Override protected String computeNext() {
          if (index >= from.length()) {
            return endOfData();
          }
          return from.subSequence(index, ++index).toString(); // up by 1
        }
      };
    }
  }
}
