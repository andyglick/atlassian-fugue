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
package io.atlassian.fugue.retry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetrySupplierTest {
  private static final int ATTEMPTS = 4;
  public static final String RESULT = "result";

  @Mock private Supplier<String> supplier;
  @Mock private ExceptionHandler exceptionHandler;
  @Mock private RuntimeException runtimeException;

  @Before public void setUp() {
    initMocks(this);
  }

  @Test public void basicSupplier() {
    when(supplier.get()).thenReturn(RESULT);
    final String result = new RetrySupplier<>(supplier, ATTEMPTS).get();

    verify(supplier).get();
    assertThat(result, equalTo(RESULT));
  }

  @Test(expected = IllegalArgumentException.class) public void basicSupplierRequiresPositiveTries() {
    new RetrySupplier<>(supplier, 0).get();
  }

  @Test(expected = RuntimeException.class) public void basicSupplierRetry() {
    when(supplier.get()).thenThrow(runtimeException);

    try {
      new RetrySupplier<>(supplier, ATTEMPTS).get();
    } finally {
      verify(supplier, times(ATTEMPTS)).get();
    }
  }

  @Test public void supplierWithExceptionHandler() {
    when(supplier.get()).thenReturn(RESULT);
    final String result = new RetrySupplier<>(supplier, ATTEMPTS, exceptionHandler).get();

    verify(supplier).get();
    assertThat(result, equalTo(RESULT));
    verifyZeroInteractions(exceptionHandler);
  }

  @Test(expected = RuntimeException.class) public void supplierRetryWithExceptions() {
    when(supplier.get()).thenThrow(runtimeException);

    try {
      new RetrySupplier<>(supplier, ATTEMPTS, exceptionHandler).get();
    } finally {
      verify(supplier, times(ATTEMPTS)).get();
      verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
  }

  @Test public void supplierEarlyExit() {
    when(supplier.get()).thenThrow(new RuntimeException("First attempt")).thenReturn(RESULT).thenThrow(new RuntimeException("Third attempt"))
      .thenThrow(new RuntimeException("Fourth attempt"));

    final String result = new RetrySupplier<>(supplier, ATTEMPTS).get();
    assertThat(result, equalTo(RESULT));
    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }
}
