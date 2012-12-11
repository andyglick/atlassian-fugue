package com.atlassian.fugue;

import static com.atlassian.fugue.Eithers.filterRight;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class FilterRightTest {
  @Test public void testThatLeftOnlyFiltersRightToEmpty() {
    final List<Either<Integer, String>> it = ImmutableList.of(Either.<Integer, String> left(1), Either.<Integer, String> left(333),
      Either.<Integer, String> left(22));

    assertFalse(filterRight(it).iterator().hasNext());
  }

  @Test public void testThatRightOnlyFiltersRightToSameContents() {
    final List<Either<Integer, String>> it = ImmutableList.of(Either.<Integer, String> right("one"), Either.<Integer, String> right("three"),
      Either.<Integer, String> right("2"));

    final Iterator<String> rights = filterRight(it).iterator();
    for (Either<Integer, String> i : it) {
      // calling get on a Maybe is ill advised, but this is a test - if get
      // returns null it's a sign that something
      // bigger has gone wrong
      assertEquals(i.right().get(), rights.next());
    }
  }

  @Test public void testThatMixedEithersFiltersRightToExpectedContents() {
    final List<Either<Integer, String>> it = ImmutableList.of(Either.<Integer, String> left(1), Either.<Integer, String> right("three"),
      Either.<Integer, String> right("fore"), Either.<Integer, String> left(22));

    final Iterator<String> iterator = filterRight(it).iterator();
    assertEquals("three", iterator.next());
    assertEquals("fore", iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test public void testThatEmptyIterableFiltersRightToEmptyIterable() {
    final List<Either<Integer, String>> it = Collections.emptyList();
    assertFalse(filterRight(it).iterator().hasNext());
  }
}
