package com.atlassian.fugue;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Futures;

import org.junit.Test;

import static com.atlassian.fugue.Futures.map;
import static com.atlassian.fugue.UtilityFunctions.int2String;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FuturesTest {

  @Test
  public void testMapGet() throws Exception {
    assertThat(map(Futures.immediateFuture(1), int2String).get(), is("1"));
  }

  @Test
  public void testMapGetTimeout() throws Exception {
    assertThat(map(Futures.immediateFuture(1), int2String).get(1, TimeUnit.HOURS), is("1"));
  }
}
