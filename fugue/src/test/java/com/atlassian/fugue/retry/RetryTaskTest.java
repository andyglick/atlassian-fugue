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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RetryTaskTest {
  private static final int ATTEMPTS = 4;

  @Mock private Runnable task;
  @Mock private ExceptionHandler exceptionHandler;
  @Mock private RuntimeException runtimeException;

  @Before public void setUp() {
    initMocks(this);
  }

  @Test public void basicTask() {
    new RetryTask(task, ATTEMPTS).run();
    verify(task).run();
  }

  @Test(expected = RuntimeException.class) public void basicTaskRetry() {
    doThrow(runtimeException).when(task).run();

    try {
      new RetryTask(task, ATTEMPTS).run();
    } finally {
      verify(task, times(ATTEMPTS)).run();
    }
  }

  @Test public void taskWithExceptionHandler() {
    new RetryTask(task, ATTEMPTS, exceptionHandler).run();
    verify(task).run();
    verifyZeroInteractions(exceptionHandler);
  }

  @Test(expected = RuntimeException.class) public void taskRetryWithExceptions() {
    doThrow(runtimeException).when(task).run();

    try {
      new RetryTask(task, ATTEMPTS, exceptionHandler).run();
    } finally {
      verify(task, times(ATTEMPTS)).run();
      verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
  }

  @Test public void taskEarlyExit() {
    final AtomicReference<Integer> failcount = new AtomicReference<>(0);
    Runnable localTask = () -> {
      failcount.set(failcount.get() + 1);
      switch (failcount.get()) {
        case 1:
          throw new RuntimeException("First attempt");
        case 2:
          return;
        default:
          throw new RuntimeException("Third runthrough (fail)");
      }
    };

    new RetryTask(localTask, ATTEMPTS).run();
    assertThat(failcount.get(), equalTo(2));
  }
}
