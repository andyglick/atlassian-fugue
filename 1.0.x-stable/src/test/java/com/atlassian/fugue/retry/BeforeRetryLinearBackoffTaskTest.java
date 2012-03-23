package com.atlassian.fugue.retry;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BeforeRetryLinearBackoffTaskTest {
  
  @Test(expected = IllegalArgumentException.class)
  public void negativeSleep() {
    new BeforeRetryLinearBackoffTask(-1);
  }
  
  @Test (expected = IllegalArgumentException.class)
  public void zeroSleep() {
    new BeforeRetryLinearBackoffTask(0);
  }
  
  @Test
  public void sleep() {
    BeforeRetryLinearBackoffTask beforeRetryLinearBackoffTask = new BeforeRetryLinearBackoffTask(1);

    Thread.currentThread().interrupt();
    try {
      beforeRetryLinearBackoffTask.run();
    } catch (RuntimeException e) {
      Throwable cause = e.getCause();
      assertThat(cause.getClass(), Matchers.<Class<? extends Throwable>>is(InterruptedException.class));
    }
  }
  
  @Test
  public void backoffIncreases() {
    BeforeRetryLinearBackoffTask beforeRetryLinearBackoffTask = new BeforeRetryLinearBackoffTask(1);

    beforeRetryLinearBackoffTask.run();
    assertThat(beforeRetryLinearBackoffTask.currentBackoff(), is(1L));
    beforeRetryLinearBackoffTask.run();
    assertThat(beforeRetryLinearBackoffTask.currentBackoff(), is(1L));
  }
}
