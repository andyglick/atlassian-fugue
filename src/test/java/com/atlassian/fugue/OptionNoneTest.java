package com.atlassian.fugue;

import static com.atlassian.fugue.Option.some;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class OptionNoneTest {
  private final Option<Integer> none = Option.none();

  @Test(expected = NoSuchElementException.class) public void get() {
    none.get();
  }

  @Test public void isSet() {
    assertFalse(none.isDefined());
  }

  @Test public void getOrElse() {
    assertEquals(new Integer(1), none.getOrElse(1));
  }

  @Test public void getOrNull() {
    assertNull(none.getOrNull());
  }

  @Test public void map() {
    final Function<Integer, Integer> function = new Function<Integer, Integer>() {
      // /CLOVER:OFF
      @Override public Integer apply(final Integer input) {
        fail("None.map should not call the function.");
        return input;
      }
      // /CLOVER:ON
    };

    assertTrue(none.map(function).isEmpty());
  }

  @Test(expected = NullPointerException.class) public void nullFunctionForMap() {
    none.map(null);
  }

  @Test(expected = NullPointerException.class) public void nullPredicateForFilter() {
    none.filter(null);
  }

  @Test public void filterTrueReturnsEmpty() {
    assertTrue(none.filter(Predicates.<Integer> alwaysTrue()).isEmpty());
  }

  @Test public void filterFalseReturnsEmpty() {
    assertTrue(none.filter(Predicates.<Integer> alwaysFalse()).isEmpty());
  }

  @Test public void existsTrueReturnsFalse() {
    assertFalse(none.exists(Predicates.<Integer> alwaysTrue()));
  }

  @Test public void existsFalseReturnsFalse() {
    assertFalse(none.exists(Predicates.<Integer> alwaysFalse()));
  }

  @Test public void toLeftReturnsRight() {
    assertTrue(none.toLeft(Suppliers.ofInstance("")).isRight());
  }

  @Test public void toRightReturnsLeft() {
    assertTrue(none.toRight(Suppliers.ofInstance("")).isLeft());
  }

  @Test public void superTypesPermittedOnFilter() {
    final Option<ArrayList<?>> opt = Option.none();
    final Option<ArrayList<?>> nopt = opt.filter(Predicates.<List<?>> alwaysTrue());
    assertSame(opt, nopt);
  }

  @Test public void superTypesPermittedOnMap() {
    final Option<ArrayList<?>> opt = Option.none();
    final Option<Set<?>> size = opt.map(new Function<List<?>, Set<?>>() {
      // /CLOVER:OFF
      public Set<?> apply(final List<?> list) {
        fail("This internal method should never get called.");
        return null;
      }
      // /CLOVER:ON
    });
    assertSame(opt, size);
  }

  @Test public void hashDoesNotThrowException() {
    none.hashCode();
  }

  // These tests are duplicated in TestEmptyIterator, but I've included them
  // here to ensure
  // that None itself complies with the API.
  @Test public void iteratorHasNoNext() {
    assertFalse(none.iterator().hasNext());
  }

  @Test(expected = NoSuchElementException.class) public void iteratorNext() {
    none.iterator().next();
  }

  @Test(expected = UnsupportedOperationException.class) public void iteratorImmutable() {
    none.iterator().remove();
  }

  @Test public void foreach() {
    assertThat(Count.countEach(none), is(0));
  }

  @Test public void forallTrue() {
    assertThat(none.forall(Predicates.<Integer> alwaysTrue()), is(true));
  }

  @Test public void forallFalse() {
    assertThat(none.forall(Predicates.<Integer> alwaysFalse()), is(true));
  }

  @Test public void toStringTest() {
    assertEquals("none()", none.toString());
  }

  @Test public void equalsItself() {
    assertTrue(none.equals(none));
  }

  @Test public void notEqualsSome() {
    assertFalse(none.equals(some("")));
  }

  @Test public void notEqualsNull() {
    assertFalse(none.equals(null));
  }
}
