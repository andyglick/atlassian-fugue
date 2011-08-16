package com.atlassian.fugue.retry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestExceptionHandlers {
  @Mock private Logger log;
  @Mock private RuntimeException exception;

  @Before public void setUp() {
    initMocks(this);
  }

  @Test public void loggingExceptionAction() {
    ExceptionHandler loggingExceptionHandler = ExceptionHandlers.loggingExceptionHandler(log);
    loggingExceptionHandler.handle(exception);

    verify(log).warn("Exception encountered: ", exception);
  }

  @Test public void chainCallOrder() {
    final StringBuffer sb = new StringBuffer();

    ExceptionHandler first = new ExceptionHandler() {
      public void handle(RuntimeException e) {
        sb.append("1");
      }
    };
    ExceptionHandler second = new ExceptionHandler() {
      public void handle(RuntimeException e) {
        sb.append("2");
      }
    };

    ExceptionHandler handler = ExceptionHandlers.chain(first, second);

    handler.handle(exception);

    assertEquals("12", sb.toString());
  }
  
  @Test public void loggingExceptionHandler() {
    Logger logger = mock(Logger.class);
    ExceptionHandler exceptionHandler = ExceptionHandlers.loggingExceptionHandler(logger);
    
    assertThat(((ExceptionHandlers.LoggingExceptionHandler) exceptionHandler).logger(), is(logger));
  }
  
  @Test public void loggingExceptionHandlerNull() {
    Logger logger = mock(Logger.class);
    ExceptionHandler exceptionHandler = ExceptionHandlers.loggingExceptionHandler(null);
    
    assertThat(exceptionHandler.getClass(), Matchers.<Class<? extends ExceptionHandler>>is(ExceptionHandlers.LoggingExceptionHandler.class));
    assertThat(((ExceptionHandlers.LoggingExceptionHandler)exceptionHandler).logger(), is(ExceptionHandlers.logger()));
  }

  @Test (expected = InvocationTargetException.class) public void nonInstantiable() throws NoSuchMethodException, 
    InvocationTargetException, IllegalAccessException, InstantiationException {
    Constructor<ExceptionHandlers> declaredConstructor = ExceptionHandlers.class.getDeclaredConstructor();
    declaredConstructor.setAccessible(true);
    declaredConstructor.newInstance();
  }
}
