package com.atlassian.fage.functions;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestAttempt
{
    @Mock private Supplier<String> supplier;
    @Mock private Function<String, Integer> function;
    @Mock private ExceptionHandler exceptionHandler; 
    @Mock private RuntimeException runtimeException;
    public static final int ATTEMPTS = 4;

    @Before
    public void setUp()
    {
        initMocks(this);
    }

    @Test
    public void testBasicSupplier()
    {
        String expected = "result";
        when(supplier.get()).thenReturn(expected);
        String result = Attempt.attempt(supplier, ATTEMPTS);

        verify(supplier).get();
        assertEquals(expected, result);
    }
    
    @Test
    public void testBasicSupplierRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            Attempt.attempt(supplier, ATTEMPTS);
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
        String expected = "result";
        when(supplier.get()).thenReturn(expected);
        String result = Attempt.attempt(supplier, ATTEMPTS, exceptionHandler);
        
        verify(supplier).get();
        assertEquals(expected, result);
        verifyZeroInteractions(exceptionHandler);
    }
    
    @Test
    public void testSupplierRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            Attempt.attempt(supplier, ATTEMPTS, exceptionHandler);
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
        final String expected = "success";
        when(supplier.get()).thenThrow(new RuntimeException("First attempt")).thenReturn(expected).thenThrow(new RuntimeException("Third attempt")).thenThrow(new RuntimeException("Fourth attempt"));
        
        String result = Attempt.attempt(supplier, ATTEMPTS);
        assertEquals(expected, result);
        verify(supplier, times(2)).get();
        verifyNoMoreInteractions(supplier);
    }
    
    @Test
    public void testBasicFunction()
    {
        Integer expected = 1;
        String input = "1";
        
        when(function.apply(input)).thenReturn(expected);
        Integer result = Attempt.attempt(function, input, ATTEMPTS);

        verify(function).apply(input);
        assertEquals(expected, result);
    }
    
    @Test
    public void testBasicFunctionRetry()
    {
        when(function.apply(anyString())).thenThrow(runtimeException);
        
        try
        {
            Attempt.attempt(function, "application", ATTEMPTS);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e);
        }
        
        verify(function, times(ATTEMPTS)).apply("application");
    }
    
    @Test
    public void testFunctionRetry()
    {
        when(function.apply("application")).thenThrow(runtimeException);
        
        try
        {
            Attempt.attempt(function, "application", ATTEMPTS, exceptionHandler);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e);
        }

        verify(function, times(ATTEMPTS)).apply("application");
        verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
    
    @Test
    public void testFunctionEarlyExit()
    {
        Integer expected = 1;
        String input = "1";
        
        when(function.apply(input)).thenThrow(new RuntimeException("First attempt")).thenReturn(expected).thenThrow(new RuntimeException("Third attempt")).thenThrow(new RuntimeException("Fourth attempt"));

        Integer result = Attempt.attempt(function, input, ATTEMPTS);
        assertEquals(expected, result);
        verify(function, times(2)).apply(input);
        verifyNoMoreInteractions(function);
    }
}
