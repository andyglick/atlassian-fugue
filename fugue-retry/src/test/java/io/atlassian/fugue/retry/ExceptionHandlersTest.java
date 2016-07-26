/*
   Copyright 2010 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package io.atlassian.fugue.retry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExceptionHandlersTest {
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
    final StringBuilder sb = new StringBuilder();

    ExceptionHandler first = e -> sb.append("1");
    ExceptionHandler second = e -> sb.append("2");

    ExceptionHandler handler = ExceptionHandlers.chain(first, second);

    handler.handle(exception);

    assertThat(sb.toString(), equalTo("12"));
  }

  @Test public void loggingExceptionHandler() {
    Logger logger = mock(Logger.class);
    ExceptionHandler exceptionHandler = ExceptionHandlers.loggingExceptionHandler(logger);

    assertThat(((ExceptionHandlers.LoggingExceptionHandler) exceptionHandler).logger(), equalTo(logger));
  }

  @Test public void loggingExceptionHandlerNull() {
    ExceptionHandler exceptionHandler = ExceptionHandlers.loggingExceptionHandler(null);

    assertThat(exceptionHandler.getClass(), Matchers.<Class<? extends ExceptionHandler>> is(ExceptionHandlers.LoggingExceptionHandler.class));
    assertThat(((ExceptionHandlers.LoggingExceptionHandler) exceptionHandler).logger(), equalTo(ExceptionHandlers.logger()));
  }

  @Test(expected = InvocationTargetException.class) public void nonInstantiable() throws NoSuchMethodException, InvocationTargetException,
    IllegalAccessException, InstantiationException {
    Constructor<ExceptionHandlers> declaredConstructor = ExceptionHandlers.class.getDeclaredConstructor();
    declaredConstructor.setAccessible(true);
    declaredConstructor.newInstance();
  }
}
