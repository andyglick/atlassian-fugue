package com.atlassian.fage.functions;
import com.google.common.base.Supplier;

import org.mockito.Mock;

import junit.framework.TestCase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestFunctions extends TestCase
{
    @Mock private Supplier<String> supplier;
    @Mock private ExceptionAction exceptionAction; 
    @Mock private RuntimeException runtimeException;
    public static final int ATTEMPTS = 4;

    @Override
    public void setUp()
    {
        initMocks(this);
    }
    
    public void testBasicAttempt()
    {
        String expected = "result";
        when(supplier.get()).thenReturn(expected);
        String result = Functions.attempt(supplier, ATTEMPTS);

        verify(supplier, times(1)).get();
        assertEquals(expected, result);
    }
    
    public void testBasicAttemptRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            Functions.attempt(supplier, ATTEMPTS);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e.getCause());
        }
        
        verify(supplier, times(ATTEMPTS)).get();
    }
    
    public void testAttempt()
    {
        String expected = "result";
        when(supplier.get()).thenReturn(expected);
        String result = Functions.attempt(supplier, ATTEMPTS, exceptionAction);
        
        verify(supplier, times(1)).get();
        assertEquals(expected, result);
        verifyZeroInteractions(exceptionAction);
    }
    
    public void testAttemptRetry()
    {
        when(supplier.get()).thenThrow(runtimeException);
        
        try
        {
            Functions.attempt(supplier, ATTEMPTS, exceptionAction);
            fail("Expected a exception.");
        }
        catch(RuntimeException e)
        {
            assertEquals(runtimeException, e.getCause());
        }

        verify(supplier, times(ATTEMPTS)).get();
        verify(exceptionAction, times(4)).act(runtimeException);
    }
}
