package com.atlassian.fage.functions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestExceptionActions
{
    @Mock private Logger log;
    @Mock private Exception exception;
    
    @Before
    public void setUp()
    {
        initMocks(this);
    }
    
    @Test
    public void testLoggingExceptionAction()
    {
        ExceptionAction loggingExceptionAction = ExceptionActions.loggingExceptionAction(log);
        loggingExceptionAction.act(exception);
        
        verify(log).warn("Exception encountered: ", exception);
    }
    
    @Test
    public void testLoggingDelayingExceptionAction()
    {
        ExceptionAction loggingDelayingExceptionAction = ExceptionActions.chain(ExceptionActions.loggingExceptionAction(log), ExceptionActions.delayingExceptionAction(100));
        long startTime = System.currentTimeMillis();
        
        loggingDelayingExceptionAction.act(exception);
        
        long actualTime = System.currentTimeMillis() - startTime;
        
        assertTrue(actualTime >= 100);
        verify(log).warn("Exception encountered: ", exception);
    }
}
