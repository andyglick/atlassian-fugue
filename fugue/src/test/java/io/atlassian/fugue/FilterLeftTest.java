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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static io.atlassian.fugue.Eithers.filterLeft;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FilterLeftTest {
  @Test public void rightOnlyFiltersLeftToEmpty() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.right("one"), Either.right("three"), Either.right("2"));

    assertFalse(filterLeft(it).iterator().hasNext());
  }

  @Test public void leftOnlyFiltersLeftToSameContents() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.left(1), Either.left(333), Either.left(22));

    final Iterator<Integer> lefts = filterLeft(it).iterator();
    for (final Either<Integer, String> i : it) {
      // calling get on a Maybe is ill advised, but this is a test - if get
      // returns null it's a sign that something
      // bigger has gone wrong
      assertEquals(i.left().get(), lefts.next());
    }
  }

  @Test public void mixedEithersFiltersLeftToExpectedContents() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.left(1), Either.right("three"), Either.right("fore"), Either.left(22));

    final Iterator<Integer> iterator = filterLeft(it).iterator();
    assertEquals(Integer.valueOf(1), iterator.next());
    assertEquals(Integer.valueOf(22), iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test public void emptyIterableFiltersLeftToEmptyIterable() {
    assertFalse(filterLeft(Collections.<Either<Integer, String>> emptyList()).iterator().hasNext());
  }
}
