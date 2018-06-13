package io.atlassian.fugue.hamcrest;

import org.junit.Test;

import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static io.atlassian.fugue.hamcrest.TryMatchers.isFailure;
import static io.atlassian.fugue.hamcrest.TryMatchers.isSuccessful;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TryMatchersTest {

  @Test public void shouldMatchAnyFailure() {
    assertThat(failure(new RuntimeException("any")), TryMatchers.isFailure());
  }

  @Test public void shouldNotMatchASuccessWhenExpectingAnyFailure() {
    assertThat(successful("a success"), not(isFailure()));
  }

  @Test public void shouldMatchASpecificFailure() {
    assertThat(failure(new RuntimeException("any")), TryMatchers.isFailure(any(Exception.class)));
  }

  @Test public void shouldNotMatchASuccessWhenExpectingASpecificFailure() {
    assertThat(successful("a success"), not(isFailure(any(Exception.class))));
  }

  @Test public void shouldNotMatchADifferentFailure() {
    assertThat(failure(new IllegalArgumentException("wrong exception")),
      not(TryMatchers.isFailure(is(new IllegalStateException("expected exception")))));
  }

  @Test public void shouldMatchASuccessfulTry() {
    assertThat(successful("anyResult"), isSuccessful(isA(String.class)));
  }

  @Test public void shouldMatchASuccessfulTryWithASupertypeMatcher() {
    assertThat(successful("anyResult"), isSuccessful(isA(Object.class)));
  }

  @Test public void shouldNotMatchAFailureWhenExpectingASuccess() {
    assertThat(failure(new RuntimeException("any")), not(isSuccessful(any(Object.class))));
  }

}