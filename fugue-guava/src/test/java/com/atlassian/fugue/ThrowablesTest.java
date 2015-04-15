/*
   Copyright 2011 Atlassian

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
package com.atlassian.fugue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;

import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class) public final class ThrowablesTest {
  @Mock private Function<Throwable, RuntimeException> function;

  @Test public void testPropagateWithFunctionForRuntimeException() throws Exception {
    final Exception original = new RuntimeException();
    try {
      Throwables.propagate(original, function);
      Assert.fail("Should have thrown an exception");
    } catch (Exception e) {
      Assert.assertSame(original, e);
    }

    verifyZeroInteractions(function);
  }

  @Test public void testPropagateWithFunctionForNonRuntimeException() throws Exception {
    final RuntimeException runtime = new RuntimeException();
    Mockito.when(function.apply(Mockito.<Throwable> any())).thenReturn(runtime);

    final Throwable original = new Exception();
    try {
      Throwables.propagate(original, function);
      Assert.fail("Should have thrown an exception");
    } catch (Exception e) {
      Assert.assertSame(runtime, e);
    }

    Mockito.verify(function).apply(original);
  }

  @Test public void testPropagateWithTypeForRuntimeException() throws Exception {
    final Exception original = new RuntimeException();
    try {
      Throwables.propagate(original, MyRuntimeException.class);
      Assert.fail("Should have thrown an exception");
    } catch (Exception e) {
      Assert.assertSame(original, e);
    }
  }

  @Test public void testPropagateWithTypeForNonRuntimeException() throws Exception {
    final Exception original = new Exception();
    try {
      Throwables.propagate(original, MyRuntimeException.class);
      Assert.fail("Should have thrown an exception");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof MyRuntimeException);
      Assert.assertSame(original, e.getCause());
    }
  }

  static final class MyRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 5698445063323657007L;

    public MyRuntimeException(Throwable throwable) {
      super(throwable);
    }
  }
}
