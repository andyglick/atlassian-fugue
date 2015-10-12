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

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import static io.atlassian.fugue.Iterables.collect;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

public class IterablesJavaCollectorTest {

  @Test public void collectStringIterableAsIntegerList() {
    final Iterable<String> from = asList("3", "2", "1", "4", "5");

    final List<Integer> collectedAsIntList = collect(from, mapping(Integer::parseInt, toList()));

    assertNotNull(collectedAsIntList);
    assertEquals(5, collectedAsIntList.size());
    assertEquals((Integer) 3, collectedAsIntList.get(0));
    assertEquals((Integer) 2, collectedAsIntList.get(1));
    assertEquals((Integer) 1, collectedAsIntList.get(2));
    assertEquals((Integer) 4, collectedAsIntList.get(3));
    assertEquals((Integer) 5, collectedAsIntList.get(4));
  }

  @Test public void collectStringIterableAsIntegerAverage() {
    final Iterable<String> from = asList("3", "2", "1", "4", "5");

    final Double average = collect(from, averagingInt(Integer::parseInt));

    assertEquals((Double) 3.0, average);
  }

  @Test public void groupStringIterableIntoIntegerLists() {
    final Iterable<String> from = asList("3", "2", "1", "4", "5");

    final Map<Boolean, List<Integer>> grouped = collect(from, mapping(Integer::valueOf, groupingBy(integer -> integer <= 3)));

    assertNotNull(grouped);
    assertEquals(2, grouped.size());
    assertTrue(grouped.containsKey(true));
    assertNotNull(grouped.get(true));
    assertEquals(3, grouped.get(true).size());
    assertTrue(grouped.get(true).contains(1));
    assertTrue(grouped.get(true).contains(2));
    assertTrue(grouped.get(true).contains(3));
    assertTrue(grouped.containsKey(false));
    assertNotNull(grouped.get(false));
    assertEquals(2, grouped.get(false).size());
    assertTrue(grouped.get(false).contains(4));
    assertTrue(grouped.get(false).contains(5));
  }

  @Test public void emptyStringIterableToIntegerListMustResultInEmptyList() {
    final Iterable<String> from = emptyList();

    final List<Integer> collectedAsIntList = collect(from, mapping(Integer::parseInt, toList()));

    assertNotNull(collectedAsIntList);
    assertEquals(0, collectedAsIntList.size());
  }

  @Test public void collectEmptyStringAsIntegerAverageMustResultInZero() {
    final Iterable<String> from = emptyList();

    final Double average = collect(from, averagingInt(Integer::parseInt));

    assertEquals((double) 0, average, 0);
  }

  @Test public void groupEmptyStringIterableIntoIntegerListsMustResultInEmptyList() {
    final Iterable<String> from = emptyList();

    final Map<Boolean, List<Integer>> grouped = collect(from, mapping(Integer::valueOf, groupingBy(integer -> integer <= 3)));

    assertNotNull(grouped);
    assertEquals(0, grouped.size());
  }

  @Test public void stringIterableWithSizeOneToIntegerList() {
    final Iterable<String> from = singletonList("123");

    final List<Integer> collectedAsIntList = collect(from, mapping(Integer::parseInt, toList()));

    assertNotNull(collectedAsIntList);
    assertEquals(1, collectedAsIntList.size());
  }

  @Test public void stringIterableWithSizeOneToIntegerAverage() {
    final Iterable<String> from = singletonList("123");

    final Double average = collect(from, averagingInt(Integer::parseInt));

    assertEquals((double) 123, average, 0);
  }

  @Test public void groupStringIterableWithSizeOneToIntegerLists() {
    final Iterable<String> from = singletonList("123");

    final Map<Boolean, List<Integer>> grouped = collect(from, mapping(Integer::valueOf, groupingBy(integer -> integer <= 3)));

    assertNotNull(grouped);
    assertEquals(1, grouped.size());
    assertTrue(grouped.containsKey(false));
    assertNotNull(grouped.get(false));
    assertEquals(1, grouped.get(false).size());
    assertTrue(grouped.get(false).contains(123));
  }

  @Test(expected = JavaCollectorRuntimeException.class) public void collectorThrowsRuntimeException() {
    final Iterable<String> from = asList("3", "2", "1", "4", "5");

    collect(from, mapping(s -> {
      throw new JavaCollectorRuntimeException();
    }, toList()));
  }

  @Test(expected = NullPointerException.class) @SuppressWarnings("unchecked") public void nullCollectorMustResultInNPE() {
    final Iterable<String> from = asList("3", "2", "1", "4", "5");

    collect(from, (Collector<String, Iterable<String>, Iterable<String>>) null);
  }

  @Test(expected = NullPointerException.class) public void nullIterableMustResultInNPE() {
    collect(null, toList());
  }

  private static class JavaCollectorRuntimeException extends RuntimeException {}
}
