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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestRetryTask {
  private static final int ATTEMPTS = 4;

  @Mock private Runnable task;
  @Mock private ExceptionHandler exceptionHandler;
  @Mock private RuntimeException runtimeException;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void testBasicTask() {
    new RetryTask(task, ATTEMPTS).run();
    verify(task).run();
  }

  @Test public void testBasicTaskRetry() {

    doThrow(runtimeException).when(task).run();

    try {
      new RetryTask(task, ATTEMPTS).run();
      fail("Expected a exception.");
    } catch (final RuntimeException e) {
      assertSame(runtimeException, e);
    }

    verify(task, times(ATTEMPTS)).run();
  }

  @Test public void testTaskWithExceptionHandler() {
    new RetryTask(task, ATTEMPTS, exceptionHandler).run();
    verify(task).run();
    verifyZeroInteractions(exceptionHandler);
  }

  @Test public void testTaskRetryWithExceptions() {
    doThrow(runtimeException).when(task).run();

    try {
      new RetryTask(task, ATTEMPTS, exceptionHandler).run();
      fail("Expected a exception.");
    } catch (final RuntimeException e) {
      assertSame(runtimeException, e);
    }

    verify(task, times(ATTEMPTS)).run();
    verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
  }

  @Test public void testTaskEarlyExit() {
    
    final AtomicReference<Integer> failcount = new AtomicReference<Integer>(0);
    Runnable localTask = new Runnable() {
      public void run() {
        failcount.set(failcount.get() + 1);
        switch (failcount.get()) {
          case 1 : throw new RuntimeException("First attempt");
          case 2 : return;
          default : throw new RuntimeException("Third runthrough (fail)");
        }
      }
    };
    
    new RetryTask(localTask, ATTEMPTS).run();
    assertThat(failcount.get(), is(2));
  }

  
}
