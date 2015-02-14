package com.atlassian.fugue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.atlassian.fugue.WeakMemoizer.MappedReference;
import com.atlassian.fugue.mango.Function.Function;
import com.atlassian.fugue.mango.Function.Supplier;

import org.junit.Test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakMemoizerTest {

  static final Function<Integer, String> lock() {
    return Functions.fromSupplier(new Supplier.AbstractSupplier<String>() {
      public String get() {
        return new String("test");
      }
    });
  }

  @Test public void callingTwiceReturnsSame() throws Exception {
    final WeakMemoizer<Integer, String> memoizer = WeakMemoizer.weakMemoizer(lock());
    assertSame(memoizer.apply(1), memoizer.apply(1));
  }

  @Test public void callingDifferentMemoizersReturnsDifferent() throws Exception {
    assertNotSame(WeakMemoizer.weakMemoizer(lock()).apply(1), WeakMemoizer.weakMemoizer(lock()).apply(1));
  }

  @Test public void lockReferenceNotNull() throws Exception {
    final MappedReference<String, String> ref = new MappedReference<String, String>("test", new String("value"), new ReferenceQueue<String>());
    assertNotNull(ref.getDescriptor());
    assertNotNull(ref.get());
  }

  @Test(expected = NullPointerException.class) public void referenceNullDescriptor() throws Exception {
    new MappedReference<String, String>(null, "value", new ReferenceQueue<String>());
  }

  @Test(expected = NullPointerException.class) public void referenceNullValue() throws Exception {
    new MappedReference<String, String>("ref", null, new ReferenceQueue<String>());
  }

  @Test public void many() throws Exception {
    final WeakMemoizer<Integer, String> memoizer = WeakMemoizer.weakMemoizer(lock());

    final int size = 10000;
    for (int i = 0; i < 10; i++) {
      System.gc();
      for (int j = 0; j < size; j++) {
        assertSame(memoizer.apply(j), memoizer.apply(j));
      }
    }
  }

  @Test public void losesReference() throws Exception {
    final WeakMemoizer<Integer, String> memoizer = WeakMemoizer.weakMemoizer(lock());

    final WeakReference<String> one = new WeakReference<String>(memoizer.apply(1));
    for (int i = 0; i < 10; i++) {
      System.gc();
    }
    assertNotNull(memoizer.apply(1));
    assertNull(one.get());
  }
}
