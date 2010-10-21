package com.atlassian.fage.functions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestExceptionHandlers
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
        ExceptionHandler loggingExceptionHandler = ExceptionHandlers.loggingExceptionHandler(log);
        loggingExceptionHandler.handle(exception);
        
        verify(log).warn("Exception encountered: ", exception);
    }
    
    @Test
    public void testDelayingExceptionAction()
    {
        ExceptionHandler delayingExceptionHandler = ExceptionHandlers.delayingExceptionHandler(100);
        
        long startTime = System.currentTimeMillis();
        
        delayingExceptionHandler.handle(exception);
        
        long actualTime = System.currentTimeMillis() - startTime;
        
        assertTrue(actualTime >= 100);
    }
    
    @Test
    public void testChainCallOrder()
    {
        final StringBuffer sb = new StringBuffer();
        
        ExceptionHandler first = new ExceptionHandler() {
            public void handle(Exception e) {
                sb.append("1");
            }
        };
        ExceptionHandler second = new ExceptionHandler() {
            public void handle(Exception e) {
                sb.append("2");
            }
        };
        
        ExceptionHandler handler = ExceptionHandlers.chain(first, second);

        handler.handle(exception);
        
        assertEquals("12", sb.toString());
    }
}
