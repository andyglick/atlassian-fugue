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

import static io.atlassian.fugue.Eithers.toLeft;
import static io.atlassian.fugue.Eithers.toRight;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ToEitherLeftOrRightTest {
  private static final String ORIGINAL_STRING = "abc";
  private static final int ORIGINAL_INT = 1;

  @Test public void testToLeftFunction() {
    assertThat(Eithers.<String, Integer> toLeft().apply(ORIGINAL_STRING).left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToLeftFunctionWithTypes() {
    assertThat(toLeft(String.class, Integer.class).apply(ORIGINAL_STRING).left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToRightFunction() {
    assertThat(Eithers.<String, Integer> toRight().apply(ORIGINAL_INT).right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToRightFunctionWithTypes() {
    assertThat(toRight(String.class, Integer.class).apply(ORIGINAL_INT).right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToLeftSupplier() {
    assertThat(Eithers.<String, Integer> toLeft(ORIGINAL_STRING).get().left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToLeftSupplierWithType() {
    assertThat(toLeft(ORIGINAL_STRING, Integer.class).get().left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToRightSupplier() {
    assertThat(Eithers.<String, Integer> toRight(ORIGINAL_INT).get().right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToRightSupplierWithType() {
    assertThat(toRight(String.class, ORIGINAL_INT).get().right().get(), is(ORIGINAL_INT));
  }

  // The following tests are more to demonstrate why these toLeft/toRight
  // Function and Supplier can be useful.

  @Test public void toRightFunctionUsedInFold() {
    final Either<String, Integer> either = divideByTwo(ORIGINAL_INT * 2).fold(toLeft(ORIGINAL_STRING, Integer.class),
      toRight(String.class, Integer.class));
    assertThat(either.right().get(), is(ORIGINAL_INT));
  }

  @Test public void toLeftSupplierUsedInFold() {
    final Either<String, Integer> either = divideByTwo(ORIGINAL_INT).fold(toLeft(ORIGINAL_STRING, Integer.class),
      toRight(String.class, Integer.class));
    assertThat(either.left().get(), is(ORIGINAL_STRING));
  }

  @Test public void toLeftFunctionUsedInFold() {
    final Either<String, Integer> either = divideByTwo(ORIGINAL_INT * 2).fold(toRight(Integer.class, ORIGINAL_STRING),
      toLeft(Integer.class, String.class)).swap();
    assertThat(either.right().get(), is(ORIGINAL_INT));
  }

  @Test public void toRightSupplierUsedInFold() {
    final Either<String, Integer> either = divideByTwo(ORIGINAL_INT).fold(toRight(Integer.class, ORIGINAL_STRING),
      toLeft(Integer.class, String.class)).swap();
    assertThat(either.left().get(), is(ORIGINAL_STRING));
  }

  private Option<Integer> divideByTwo(final Integer i) {
    return i % 2 == 0 ? Option.some(i / 2) : Option.<Integer> none();
  }
}
