package com.atlassian.fugue;

import com.google.common.base.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
        when(function.apply(Mockito.<Throwable>any())).thenReturn(runtime);

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

    private static final class MyRuntimeException extends RuntimeException {
        public MyRuntimeException(Throwable throwable) {
            super(throwable);
        }
    }
}
