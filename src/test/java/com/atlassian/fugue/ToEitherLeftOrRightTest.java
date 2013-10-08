package com.atlassian.fugue;

import org.junit.Test;

import static com.atlassian.fugue.Eithers.toLeft;
import static com.atlassian.fugue.Eithers.toRight;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ToEitherLeftOrRightTest
{
  private static final String ORIGINAL_STRING = "abc";
  private static final int ORIGINAL_INT = 1;

  @Test public void testToLeftFunction() {
    assertThat(Eithers.<String, Integer>toLeft().apply(ORIGINAL_STRING).left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToLeftFunctionWithTypes() {
    assertThat(toLeft(String.class, Integer.class).apply(ORIGINAL_STRING).left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToRightFunction() {
    assertThat(Eithers.<String, Integer>toRight().apply(ORIGINAL_INT).right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToRightFunctionWithTypes() {
    assertThat(toRight(String.class, Integer.class).apply(ORIGINAL_INT).right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToLeftSupplier() {
    assertThat(Eithers.<String, Integer>toLeft(ORIGINAL_STRING).get().left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToLeftSupplierWithType() {
    assertThat(toLeft(ORIGINAL_STRING, Integer.class).get().left().get(), is(ORIGINAL_STRING));
  }

  @Test public void testToRightSupplier() {
    assertThat(Eithers.<String, Integer>toRight(ORIGINAL_INT).get().right().get(), is(ORIGINAL_INT));
  }

  @Test public void testToRightSupplierWithType() {
    assertThat(toRight(String.class, ORIGINAL_INT).get().right().get(), is(ORIGINAL_INT));
  }

  // The following tests are more to demonstrate why these toLeft/toRight Function and Supplier can be useful.

  @Test public void toRightFunctionUsedInFold() {
    Either<String, Integer> either = divideByTwo(ORIGINAL_INT * 2).
            fold(toLeft(ORIGINAL_STRING, Integer.class), toRight(String.class, Integer.class));
    assertThat(either.right().get(), is(ORIGINAL_INT));
  }

  @Test public void toLeftSupplierUsedInFold() {
    Either<String, Integer> either = divideByTwo(ORIGINAL_INT).
            fold(toLeft(ORIGINAL_STRING, Integer.class), toRight(String.class, Integer.class));
    assertThat(either.left().get(), is(ORIGINAL_STRING));
  }

  @Test public void toLeftFunctionUsedInFold() {
    Either<String, Integer> either = divideByTwo(ORIGINAL_INT * 2).
            fold(toRight(Integer.class, ORIGINAL_STRING), toLeft(Integer.class, String.class)).swap();
    assertThat(either.right().get(), is(ORIGINAL_INT));
  }

  @Test public void toRightSupplierUsedInFold() {
    Either<String, Integer> either = divideByTwo(ORIGINAL_INT).
            fold(toRight(Integer.class, ORIGINAL_STRING), toLeft(Integer.class, String.class)).swap();
    assertThat(either.left().get(), is(ORIGINAL_STRING));
  }

  private Option<Integer> divideByTwo(final Integer i) {
    return i % 2 == 0 ? Option.some(i / 2) : Option.<Integer>none();
  }
}
