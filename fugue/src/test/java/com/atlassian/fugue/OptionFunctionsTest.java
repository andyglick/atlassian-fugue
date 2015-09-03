package com.atlassian.fugue;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OptionFunctionsTest {

  private Integer NULL = null;

  @Test public void nullSafeIdentityOnNull() {
    assertThat(Options.nullSafe(i -> i).apply(NULL).isEmpty(), is(true));
  }

  @Test public void nullSafeIdentityOnValue() {
    assertThat(Options.nullSafe(i -> i).apply(1).isEmpty(), is(false));
  }

  @Test public void toOptionWithNull() {
    assertThat(Options.toOption().apply(NULL).isEmpty(), is(true));
  }

  @Test public void toOptionWithValue() {
    assertThat(Options.toOption().apply(1).isEmpty(), is(false));
  }
}
