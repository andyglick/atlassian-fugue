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

import java.util.function.Function;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryFunctionTest {
  private static final int ATTEMPTS = 4;

  @Mock private Function<String, Integer> function;
  @Mock private ExceptionHandler exceptionHandler;
  @Mock private RuntimeException runtimeException;
  public static final String INPUT = "1";
  public static final Integer EXPECTED = 1;

  @Before public void setUp() {
    initMocks(this);
  }

  @Test public void basicFunction() {
    when(function.apply(INPUT)).thenReturn(EXPECTED);
    final Integer result = new RetryFunction<>(function, ATTEMPTS).apply(INPUT);

    verify(function).apply(INPUT);
    assertThat(result, equalTo(EXPECTED));
  }

  @Test(expected = RuntimeException.class) public void basicFunctionRetry() {
    when(function.apply(anyString())).thenThrow(runtimeException);

    try {
      new RetryFunction<>(function, ATTEMPTS).apply(INPUT);
    } finally {
      verify(function, times(ATTEMPTS)).apply(INPUT);
    }
  }

  @Test(expected = RuntimeException.class) public void functionRetryWithExceptionHandler() {
    when(function.apply(INPUT)).thenThrow(runtimeException);

    try {
      new RetryFunction<>(function, ATTEMPTS, exceptionHandler).apply(INPUT);
    } finally {
      verify(function, times(ATTEMPTS)).apply(INPUT);
      verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
  }

  @Test public void functionEarlyExit() {
    when(function.apply(INPUT)).thenThrow(new RuntimeException("First attempt")).thenReturn(EXPECTED)
      .thenThrow(new RuntimeException("Third attempt")).thenThrow(new RuntimeException("Fourth attempt"));

    final Integer result = new RetryFunction<>(function, ATTEMPTS).apply(INPUT);
    assertThat(result, equalTo(EXPECTED));
    verify(function, times(2)).apply(INPUT);
    verifyNoMoreInteractions(function);
  }

  @Test(expected=IllegalArgumentException.class) public void cannotSupplyNegativeRetries(){
    new RetryFunction<>(function, -1);
  }
}
