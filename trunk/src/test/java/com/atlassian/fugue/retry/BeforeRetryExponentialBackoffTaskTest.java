package com.atlassian.fugue.retry;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BeforeRetryExponentialBackoffTaskTest {
  
  @Test (expected = IllegalArgumentException.class)
  public void negativeSleep() {
    new BeforeRetryExponentialBackoffTask(-1);
  }
  
  @Test (expected = IllegalArgumentException.class)
  public void zeroSleep() {
    new BeforeRetryExponentialBackoffTask(0);
  }
  
  @Test
  public void sleep() {
    BeforeRetryExponentialBackoffTask beforeRetryExponentialBackoffTask = new BeforeRetryExponentialBackoffTask(1);

    Thread.currentThread().interrupt();
    try {
      beforeRetryExponentialBackoffTask.run();
    } catch (RuntimeException e) {
      Throwable cause = e.getCause();
      assertThat(cause.getClass(), Matchers.<Class<? extends Throwable>>is(InterruptedException.class));
    }
  }
  
  @Test
  public void backoffIncreases() {
    BeforeRetryExponentialBackoffTask beforeRetryExponentialBackoffTask = new BeforeRetryExponentialBackoffTask(1);

    beforeRetryExponentialBackoffTask.run();
    assertThat(beforeRetryExponentialBackoffTask.currentBackoff(), is(2L));
    beforeRetryExponentialBackoffTask.run();
    assertThat(beforeRetryExponentialBackoffTask.currentBackoff(), is(4L));
  }
}
