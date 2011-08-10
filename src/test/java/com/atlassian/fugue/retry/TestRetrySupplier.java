package com.atlassian.fugue.retry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.common.base.Supplier;

public class TestRetrySupplier {
  private static final int ATTEMPTS = 4;
  public static final String RESULT = "result";

  @Mock private Supplier<String> supplier;
  @Mock private ExceptionHandler exceptionHandler;
  @Mock private RuntimeException runtimeException;

  @Before public void setUp() {
    initMocks(this);
  }

  @Test public void testBasicSupplier() {
    when(supplier.get()).thenReturn(RESULT);
    final String result = new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).get();

    verify(supplier).get();
    assertEquals(RESULT, result);
  }

  @Test public void testBasicSupplierRetry() {
    when(supplier.get()).thenThrow(runtimeException);

    try {
      new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).get();
      fail("Expected a exception.");
    } catch (final RuntimeException e) {
      assertSame(runtimeException, e);
    }

    verify(supplier, times(ATTEMPTS)).get();
  }

  @Test public void testSupplierWithExceptionHandler() {
    when(supplier.get()).thenReturn(RESULT);
    final String result = new RetrySupplier<String>(supplier, ATTEMPTS, exceptionHandler).get();

    verify(supplier).get();
    assertEquals(RESULT, result);
    verifyZeroInteractions(exceptionHandler);
  }

  @Test public void testSupplierRetryWithExceptions() {
    when(supplier.get()).thenThrow(runtimeException);

    try {
      new RetrySupplier<String>(supplier, ATTEMPTS, exceptionHandler).get();
      fail("Expected a exception.");
    } catch (final RuntimeException e) {
      assertSame(runtimeException, e);
    }

    verify(supplier, times(ATTEMPTS)).get();
    verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
  }

  @Test public void testSupplierEarlyExit() {
    when(supplier.get()).thenThrow(new RuntimeException("First attempt")).thenReturn(RESULT).thenThrow(new RuntimeException("Third attempt"))
      .thenThrow(new RuntimeException("Fourth attempt"));

    final String result = new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).get();
    assertEquals(RESULT, result);
    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }
}
