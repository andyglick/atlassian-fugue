package com.atlassian.fugue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Function;

public class FunctionLiftTest {

  @Test public void testLiftingNull() {
    assertThat(Functions.lift(FunctionLiftTest.<String, String> nullProducer()).apply("ignored"), is(Option.<String> none()));
  }

  @Test public void testLiftingNotNull() {
    assertThat(Functions.lift(com.google.common.base.Functions.<String> identity()).apply("mx1tr1x"), is(Option.some("mx1tr1x")));
  }

  static <A, B> Function<A, B> nullProducer() {
    return new Function<A, B>() {
      @Override public B apply(A a) {
        return null;
      }
    };
  }
}
