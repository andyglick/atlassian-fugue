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
