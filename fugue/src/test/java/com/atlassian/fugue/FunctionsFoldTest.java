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

import static com.atlassian.fugue.Functions.fold;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Function;

public class FunctionsFoldTest {
  @Test public void f2FoldSum() {
    final Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
      @Override public Integer apply(final Integer arg1, final Integer arg2) {
        return arg1 + arg2;
      }
    };
    assertThat(fold(add, 0, asList(1, 2, 3, 4, 5)), is(15));
  }

  @Test public void f2FoldMultiply() {
    final Function2<Integer, Integer, Integer> mult = new Function2<Integer, Integer, Integer>() {
      @Override public Integer apply(final Integer arg1, final Integer arg2) {
        return arg1 * arg2;
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4)), is(24));
  }

  @Test public void f2FoldTypes() {
    final Function2<String, Integer, String> append = new Function2<String, Integer, String>() {
      @Override public String apply(final String s, final Integer i) {
        return s + "  " + i;
      }
    };
    assertThat(fold(append, "Iterable:", asList(12, 15, 20)), is("Iterable:  12  15  20"));
  }

  @Test public void f1FoldSum() {
    final Function<Pair<Integer, Integer>, Integer> add = new Function<Pair<Integer, Integer>, Integer>() {
      @Override public Integer apply(final Pair<Integer, Integer> arg) {
        return arg.left() + arg.right();
      }
    };
    assertThat(fold(add, 0, asList(1, 2, 3, 4, 5, 6)), is(21));
  }

  @Test public void f1FoldMultiply() {
    final Function<Pair<Integer, Integer>, Integer> mult = new Function<Pair<Integer, Integer>, Integer>() {
      @Override public Integer apply(final Pair<Integer, Integer> arg) {
        return arg.left() * arg.right();
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4, 5)), is(120));
  }

  @Test public void f1FoldTypes() {
    final Function<Pair<String, Integer>, String> append = new Function<Pair<String, Integer>, String>() {
      @Override public String apply(final Pair<String, Integer> t) {
        return t.left() + "  " + t.right();
      };
    };
    assertThat(fold(append, "Iterable:", asList(12, 15, 20)), is("Iterable:  12  15  20"));
  }

  @Test public void f1FoldVariance() {
    final Function<Pair<Number, Number>, Number> mult = new Function<Pair<Number, Number>, Number>() {
      @Override public Integer apply(final Pair<Number, Number> arg) {
        return arg.left().intValue() * arg.right().intValue();
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4, 5)), is((Number) 120));
  }

  @Test public void f2FoldVariance() {
    final Function2<Number, Number, Number> mult = new Function2<Number, Number, Number>() {
      @Override public Integer apply(final Number arg1, final Number arg2) {
        return arg1.intValue() * arg2.intValue();
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4, 5)), is((Number) 120));
  }

  @Test public void f1FoldVarianceFirstArg() {
    final Function<Pair<Number, Integer>, Integer> mult = new Function<Pair<Number, Integer>, Integer>() {
      @Override public Integer apply(final Pair<Number, Integer> arg) {
        return arg.left().intValue() * arg.right();
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4, 5)), is(120));
  }

  @Test public void f2FoldVarianceFirstArg() {
    final Function2<Number, Integer, Integer> mult = new Function2<Number, Integer, Integer>() {
      @Override public Integer apply(final Number arg1, final Integer arg2) {
        return arg1.intValue() * arg2;
      }
    };
    assertThat(fold(mult, 1, asList(1, 2, 3, 4, 5)), is(120));
  }
}
