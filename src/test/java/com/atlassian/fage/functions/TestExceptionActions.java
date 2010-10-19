package com.atlassian.fage.functions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import static junit.framework.Assert.assertEquals;
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
    public void testDelayingExceptionAction()
    {
        ExceptionAction delayingExceptionAction = ExceptionActions.delayingExceptionAction(100);
        
        long startTime = System.currentTimeMillis();
        
        delayingExceptionAction.act(exception);
        
        long actualTime = System.currentTimeMillis() - startTime;
        
        assertTrue(actualTime >= 100);
    }
    
    @Test
    public void testChainCallOrder()
    {
        final StringBuffer sb = new StringBuffer();
        
        ExceptionAction first = new ExceptionAction() {
            public void act(Exception e) {
                sb.append("1");
            }
        };
        ExceptionAction second = new ExceptionAction() {
            public void act(Exception e) {
                sb.append("2");
            }
        };
        
        ExceptionAction action = ExceptionActions.chain(first, second);

        action.act(exception);
        
        assertEquals("12", sb.toString());
    }
}
