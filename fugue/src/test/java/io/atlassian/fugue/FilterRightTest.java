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

import static io.atlassian.fugue.Eithers.filterRight;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FilterRightTest {
  @Test public void leftOnlyFiltersRightToEmpty() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.<Integer, String> left(1), Either.<Integer, String> left(333),
      Either.<Integer, String> left(22));

    assertFalse(filterRight(it).iterator().hasNext());
  }

  @Test public void rightOnlyFiltersRightToSameContents() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.<Integer, String> right("one"), Either.<Integer, String> right("three"),
      Either.<Integer, String> right("2"));

    final Iterator<String> rights = filterRight(it).iterator();
    for (Either<Integer, String> i : it) {
      // calling get on a Maybe is ill advised, but this is a test - if get
      // returns null it's a sign that something
      // bigger has gone wrong
      assertEquals(i.right().get(), rights.next());
    }
  }

  @Test public void mixedEithersFiltersRightToExpectedContents() {
    final List<Either<Integer, String>> it = Arrays.asList(Either.<Integer, String> left(1), Either.<Integer, String> right("three"),
      Either.<Integer, String> right("fore"), Either.<Integer, String> left(22));

    final Iterator<String> iterator = filterRight(it).iterator();
    assertEquals("three", iterator.next());
    assertEquals("fore", iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test public void emptyIterableFiltersRightToEmptyIterable() {
    assertFalse(filterRight(Collections.<Either<Integer, String>> emptyList()).iterator().hasNext());
  }
}
