package io.atlassian.fugue;

import org.junit.Test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.function.Function;

import static io.atlassian.fugue.Functions.WeakMemoizer.weakMemoizer;
import static io.atlassian.fugue.Functions.weakMemoize;
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
    assertNotSame(weakMemoizer(supplier()).apply(1), weakMemoizer(supplier()).apply(1));
  }



  @Test public void lockReferenceNotNull() throws Exception {
    final Functions.WeakMemoizer.MappedReference<String, String> ref = new Functions.WeakMemoizer.MappedReference<>("test", "value", new ReferenceQueue<>());
    assertNotNull(ref.getDescriptor());
    assertNotNull(ref.get());
  }



  @Test(expected = NullPointerException.class) public void referenceNullDescriptor() throws Exception {
    new Functions.WeakMemoizer.MappedReference<String, String>(null, "value", new ReferenceQueue<>());
  }



  @Test(expected = NullPointerException.class) public void referenceNullValue() throws Exception {
    new Functions.WeakMemoizer.MappedReference<String, String>("ref", null, new ReferenceQueue<>());
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
