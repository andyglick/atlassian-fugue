package com.atlassian.fugue;

import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.function.Function;

import static com.atlassian.fugue.Functions.weakMemoize;
import static com.atlassian.fugue.WeakMemoizer.weakMemoizer;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class WeakMemoizerTest {

  static Function<Integer, String> supplier() {
    return Functions.fromSupplier(() -> new String("test"));
  }

  @Test public void callingTwiceReturnsSame() throws Exception {
    final Function<Integer, String> memoizer = weakMemoize(supplier());
    assertSame(memoizer.apply(1), memoizer.apply(1));
  }

  @Test public void callingDifferentMemoizersReturnsDifferent() throws Exception {
    assertNotSame(weakMemoizer(supplier()).apply(1), weakMemoize(supplier()).apply(1));
  }

  @Test public void many() throws Exception {
    final Function<Integer, String> memoizer = weakMemoize(supplier());

    final int size = 10000;
    for (int i = 0; i < 10; i++) {
      System.gc();
      for (int j = 0; j < size; j++) {
        assertSame(memoizer.apply(j), memoizer.apply(j));
      }
    }
  }

  @Test public void losesReference() throws Exception {
    final Function<Integer, String> memoizer = weakMemoize(supplier());

    final WeakReference<String> one = new WeakReference<>(memoizer.apply(1));
    for (int i = 0; i < 10; i++) {
      System.gc();
    }
    assertNotNull(memoizer.apply(1));
    assertNull(one.get());
  }
}
