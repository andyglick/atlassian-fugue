package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestRetrySupplier
{
    private static final int ATTEMPTS = 4;
    public static final String RESULT = "result";

    @Mock private Supplier<String> supplier;
    @Mock private Function<String, Integer> function;
    @Mock private ExceptionHandler exceptionHandler;
    @Mock private RuntimeException runtimeException;

    @Before
    public void setUp()
    {
        initMocks(this);
    }
    
    @Test
    public void testBasicSupplier()
    {
        when(supplier.get()).thenReturn(RESULT);
        String result = new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.noOpExceptionHandler()).get();

        verify(supplier).get();
        assertEquals(RESULT, result);
    }
    
    @Test
    public void testBasicSupplierRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.noOpExceptionHandler()).get();
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e);
        }
        
        verify(supplier, times(ATTEMPTS)).get();
    }
    
    @Test
    public void testSupplier()
    {
        when(supplier.get()).thenReturn(RESULT);
        String result = new RetrySupplier<String>(supplier, ATTEMPTS, exceptionHandler).get();
        
        verify(supplier).get();
        assertEquals(RESULT, result);
        verifyZeroInteractions(exceptionHandler);
    }
    
    @Test
    public void testSupplierRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            new RetrySupplier<String>(supplier, ATTEMPTS, exceptionHandler).get();
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e);
        }

        verify(supplier, times(ATTEMPTS)).get();
        verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
    
    @Test 
    public void testSupplierEarlyExit()
    {
        when(supplier.get()).thenThrow(new RuntimeException("First attempt")).thenReturn(RESULT).thenThrow(new RuntimeException("Third attempt")).thenThrow(new RuntimeException("Fourth attempt"));

        String result = new RetrySupplier<String>(supplier, ATTEMPTS, ExceptionHandlers.noOpExceptionHandler()).get();
        assertEquals(RESULT, result);
        verify(supplier, times(2)).get();
        verifyNoMoreInteractions(supplier);
    }
}
