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

import static com.atlassian.fugue.Eithers.getOrThrow;
import static com.atlassian.fugue.Iterables.emptyIterable;
import static com.atlassian.fugue.Iterables.findFirst;
import static com.atlassian.fugue.Iterables.partition;
import static com.atlassian.fugue.Iterables.rangeTo;
import static com.atlassian.fugue.Iterables.rangeUntil;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Pair.pair;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

public class IterablesTest {

  private final Predicate<Integer> grepOne = new Predicate<Integer>() {
    public boolean apply(final Integer input) {
      return new Integer(1).equals(input);
    }
  };
  private final Option<Integer> none = Option.<Integer> none();

  @Test public void emptyIterableIteratorHasNext() {
    assertThat(emptyIterable().iterator().hasNext(), is(false));
  }

  @Test(expected = NoSuchElementException.class) public void emptyIterableIteratorNext() {
    emptyIterable().iterator().next();
  }

  @Test(expected = IllegalStateException.class) public void emptyIterableIteratorRemove() {
    emptyIterable().iterator().remove();
  }

  @Test public void emptyIterablesAreEqual() {
    assertThat(emptyIterable(), is(emptyIterable()));
  }

  @Test public void emptyIterablesToString() {
    assertThat(emptyIterable().toString(), is("[]"));
  }

  @Test public void findFirstEmpty() {
    assertThat(findFirst(ImmutableList.<Integer> of(), grepOne), is(Option.<Integer> none()));
  }

  @Test public void findFirstAbsent() {
    assertThat(findFirst(of(2), grepOne), is(none));
  }

  @Test public void findFirstSingle() {
    assertThat(findFirst(of(1), grepOne), is(some(1)));
  }

  @Test public void findFirstWhenNotFirstElement() {
    assertThat(findFirst(of(2, 1), grepOne), is(some(1)));
  }

  @Test public void findFirstMultipleMatches() {
    final Pair<Integer, Integer> expected = pair(1, 1);
    final List<Pair<Integer, Integer>> ts = ImmutableList.of(expected, pair(2, 2), pair(1, 3), pair(2, 4));

    final Option<Pair<Integer, Integer>> found = findFirst(ts, new Predicate<Pair<Integer, Integer>>() {
      public boolean apply(final Pair<Integer, Integer> input) {
        return input.left().equals(1);
      }
    });

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
    Pair<Iterable<Integer>, Iterable<Integer>> part = partition(asList(1, 2, 3, 4), Range.greaterThan(2));
    assertThat(part.left(), contains(3, 4));
    assertThat(part.right(), contains(1, 2));
  }

  @Test public void flatMapConcatenates() {
    final Iterable<String> result = Iterables.flatMap(asList("123", "ABC"), new Function<String, Iterable<String>>() {
      public Iterable<String> apply(final String from) {
        return new CharSplitter(from);
      }
    });
    assertThat(result, contains("1", "2", "3", "A", "B", "C"));
  }

  @Test public void findFirstFunctionWorks() {
    assertThat(findFirst(Predicates.equalTo(3)).apply(asList(1, 2, 3)), is(some(3)));
  }

  @Test public void findFirstFunctionFails() {
    assertThat(findFirst(Predicates.equalTo(3)).apply(asList(1, 2, 4)), is(Option.<Integer> none()));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Iterables> defaultCtor().apply(Iterables.class));
  }

  @Test public void revMap() {
    Iterable<Function<Integer, Integer>> fs = ImmutableList.<Function<Integer, Integer>> of(new Function<Integer, Integer>() {
      public Integer apply(final Integer from) {
        return from + 1;
      }
    }, new Function<Integer, Integer>() {
      public Integer apply(final Integer from) {
        return from + 2;
      }
    }, new Function<Integer, Integer>() {
      public Integer apply(final Integer from) {
        return from * from;
      }
    });
    assertThat(Iterables.revMap(fs, 3), contains(4, 5, 9));
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
      return new AbstractIterator<String>() {
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
