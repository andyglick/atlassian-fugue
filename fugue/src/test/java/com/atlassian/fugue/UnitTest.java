package com.atlassian.fugue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

public class UnitTest {
  @Test public void unitValueIsNotNull() {
    assertThat(Unit.VALUE, is(not(nullValue())));
  }
}
