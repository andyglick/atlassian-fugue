package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestRetryFunction
{
    private static final int ATTEMPTS = 4;
    
    @Mock private Supplier<String> supplier;
    @Mock private Function<String, Integer> function;
    @Mock private ExceptionHandler exceptionHandler;
    @Mock private RuntimeException runtimeException;
    public static final String INPUT = "1";
    public static final Integer EXPECTED = 1;

    @Before
    public void setUp()
    {
        initMocks(this);
    }
    
    @Test
    public void testBasicFunction()
    {
        when(function.apply(INPUT)).thenReturn(EXPECTED);
        Integer result =  new RetryFunction<String, Integer>(function, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).apply(INPUT);

        verify(function).apply(INPUT);
        assertEquals(EXPECTED, result);
    }
    
    @Test
    public void testBasicFunctionRetry()
    {
        when(function.apply(anyString())).thenThrow(runtimeException);

        try
        {
            new RetryFunction<String, Integer>(function, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).apply(INPUT);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertSame(runtimeException, e);
        }
        
        verify(function, times(ATTEMPTS)).apply(INPUT);
    }
    
    @Test
    public void testFunctionRetryWithExceptionHandler()
    {
        when(function.apply(INPUT)).thenThrow(runtimeException);
        
        try
        {
            new RetryFunction<String, Integer>(function, ATTEMPTS, exceptionHandler).apply(INPUT);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertSame(runtimeException, e);
        }

        verify(function, times(ATTEMPTS)).apply(INPUT);
        verify(exceptionHandler, times(ATTEMPTS)).handle(runtimeException);
    }
    
    @Test
    public void testFunctionEarlyExit()
    {
        when(function.apply(INPUT)).thenThrow(new RuntimeException("First attempt")).thenReturn(EXPECTED).thenThrow(new RuntimeException("Third attempt")).thenThrow(new RuntimeException("Fourth attempt"));

        Integer result = new RetryFunction<String, Integer>(function, ATTEMPTS, ExceptionHandlers.ignoreExceptionHandler()).apply(INPUT);
        assertEquals(EXPECTED, result);
        verify(function, times(2)).apply(INPUT);
        verifyNoMoreInteractions(function);
    }
}
