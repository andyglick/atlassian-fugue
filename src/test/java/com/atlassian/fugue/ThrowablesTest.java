package com.atlassian.fugue;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public final class ThrowablesTest {
  @Mock
  private Function<Throwable, RuntimeException> function;

  @Test
  public void testPropagateWithFunctionForRuntimeException() throws Exception {
    final Exception original = new RuntimeException();
    try {
      Throwables.propagate(original, function);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertSame(original, e);
    }

    verifyZeroInteractions(function);
  }

  @Test
  public void testPropagateWithFunctionForNonRuntimeException() throws Exception {
    final RuntimeException runtime = new RuntimeException();
    when(function.apply(Mockito.<Throwable> any())).thenReturn(runtime);

    final Throwable original = new Exception();
    try {
      Throwables.propagate(original, function);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertSame(runtime, e);
    }

    verify(function).apply(original);
  }

  @Test
  public void testPropagateWithTypeForRuntimeException() throws Exception {
    final Exception original = new RuntimeException();
    try {
      Throwables.propagate(original, MyRuntimeException.class);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertSame(original, e);
    }
  }

  @Test
  public void testPropagateWithTypeForNonRuntimeException() throws Exception {
    final Exception original = new Exception();
    try {
      Throwables.propagate(original, MyRuntimeException.class);
      fail("Should have thrown an exception");
    } catch (Exception e) {
      assertTrue(e instanceof MyRuntimeException);
      assertSame(original, e.getCause());
    }
  }

  static final class MyRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 5698445063323657007L;

    public MyRuntimeException(Throwable throwable) {
      super(throwable);
    }
  }
}
