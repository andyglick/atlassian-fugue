package io.atlassian.fugue.hamcrest;

import org.junit.Test;

import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static io.atlassian.fugue.hamcrest.TryMatchers.isFailure;
import static io.atlassian.fugue.hamcrest.TryMatchers.isSuccessful;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TryMatchersTest {

  @Test public void shouldMatchAFailure() {
    assertThat(failure(new RuntimeException("any")), TryMatchers.isFailure());
    assertThat(failure(new RuntimeException("any")), TryMatchers.isFailure(any(Exception.class)));
    RuntimeException runtimeException = new RuntimeException("any");
    assertThat(failure(runtimeException), TryMatchers.isFailure(is(runtimeException)));
    assertThat(successful("a success"), not(isFailure()));
  }

  @Test public void shouldMatchASuccessfulTry() {
    assertThat(successful("anyResult"), isSuccessful(isA(String.class)));
    assertThat(successful("anyResult"), isSuccessful(isA(Object.class)));
    assertThat(successful("anyResult"), isSuccessful(is("anyResult")));
    assertThat(failure(new RuntimeException("any")), not(isSuccessful(any(Object.class))));
  }

}