package com.atlassian.fugue;

import static com.atlassian.fugue.Functions.fold;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Function;

public class FoldTest {
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
}
