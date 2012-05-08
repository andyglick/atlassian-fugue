/*
   Copyright 2010 Atlassian

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
