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

import static com.atlassian.fugue.Either.getOrThrow;
import static com.atlassian.fugue.Iterables.findFirst;
import static com.atlassian.fugue.Iterables.rangeTo;
import static com.atlassian.fugue.Iterables.rangeUntil;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Pair.pair;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

public class IterablesTest {

  private final Predicate<Integer> grepOne = new Predicate<Integer>() {
    public boolean apply(final Integer input) {
      return new Integer(1).equals(input);
    }
  };
  private final Option<Integer> none = Option.<Integer> none();

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

  @Test public void testRangeTo() {
    assertThat(rangeTo(1, 5), is(contains(1, 2, 3, 4, 5)));
  }

  @Test public void testRangeUntil() {
    assertThat(rangeUntil(1, 5), is(contains(1, 2, 3, 4)));
  }

  @Test public void testRangeToStep() {
    assertThat(rangeTo(1, 5, 2), is(contains(1, 3, 5)));
  }

  @Test public void testRangeUntilStep() {
    assertThat(rangeUntil(1, 5, 2), is(contains(1, 3)));
  }

  @Test public void testRangeUntilNegativeStep() {
    assertThat(rangeUntil(8, -1, -3), is(contains(8, 5, 2)));
  }

  @Test public void flatMapConcatenates() {
    final Iterable<String> result = Iterables.flatMap(asList("123", "ABC"), new Function<String, Iterable<String>>() {
      public Iterable<String> apply(final String from) {
        return new CharSplitter(from);
      }
    });
    assertThat(result, contains("1", "2", "3", "A", "B", "C"));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws Exception {
    getOrThrow(UtilityFunctions.<Iterables> defaultCtor().apply(Iterables.class));
  }

  /**
   * Splits a string into characters.
   */
  class CharSplitter implements Iterable<String> {
    private final String from;

    CharSplitter(final String from) {
      this.from = from;
    }

    @Override public Iterator<String> iterator() {
      return new AbstractIterator<String>() {
        int index = 0;

        @Override protected String computeNext() {
          if (index >= from.length()) {
            return endOfData();
          }
          return from.substring(index, ++index); // up by 1
        }
      };
    }
  }
}
