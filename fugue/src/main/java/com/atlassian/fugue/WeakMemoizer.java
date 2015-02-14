/**
 * Copyright 2008 Atlassian Pty Ltd 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.atlassian.fugue;

import static com.atlassian.fugue.mango.Preconditions.checkNotNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.atlassian.fugue.mango.Function.Function;

/**
 * {@link WeakMemoizer} caches the result of another function. The result is
 * {@link WeakReference weakly referenced} internally. This is useful if the
 * result is expensive to compute or the identity of the result is particularly
 * important.
 * <p>
 * If the results from this function are further cached then they will tend to
 * stay in this cache for longer.
 * 
 * @param <A> comparable descriptor, the usual rules for any {@link HashMap} key
 * apply.
 * @param <B> the value
 */
final class WeakMemoizer<A, B> implements Function<A, B> {
  static <A, B> WeakMemoizer<A, B> weakMemoizer(final Function<A, B> delegate) {
    return new WeakMemoizer<A, B>(delegate);
  }

  private final ConcurrentMap<A, MappedReference<A, B>> map;
  private final ReferenceQueue<B> queue = new ReferenceQueue<B>();
  private final Function<A, B> delegate;

  /**
   * Construct a new {@link WeakMemoizer} instance.
   * 
   * @param delegate for creating the initial values.
   * @throws IllegalArgumentException if the initial capacity of elements is
   * negative.
   */
  WeakMemoizer(Function<A, B> delegate) {
    this.map = new ConcurrentHashMap<A, MappedReference<A, B>>();
    this.delegate = checkNotNull(delegate, "delegate");
  }

  /**
   * Get a result for the supplied Descriptor.
   * 
   * @param descriptor must not be null
   * @return descriptor lock
   */
  public B apply(final A descriptor) {
    expungeStaleEntries();
    checkNotNull(descriptor, "descriptor");
    while (true) {
      final MappedReference<A, B> reference = map.get(descriptor);
      if (reference != null) {
        final B value = reference.get();
        if (value != null) {
          return value;
        }
        map.remove(descriptor, reference);
      }
      map.putIfAbsent(descriptor, new MappedReference<A, B>(descriptor, delegate.apply(descriptor), queue));
    }
  }

  // expunge entries whose value reference has been collected
  @SuppressWarnings("unchecked") private void expungeStaleEntries() {
    MappedReference<A, B> ref;
    // /CLOVER:OFF
    while ((ref = (MappedReference<A, B>) queue.poll()) != null) {
      final A key = ref.getDescriptor();
      if (key == null) {
        // DO NOT REMOVE! In theory this should not be necessary as it
        // should not be able to be null - but we have seen it happen!
        continue;
      }
      map.remove(key, ref);
    }
    // /CLOVER:ON
  }

  /**
   * A weak reference that maintains a reference to the key so that it can be
   * removed from the map when the value is garbage collected.
   */
  static final class MappedReference<K, V> extends WeakReference<V> {
    private final K key;

    public MappedReference(final K key, final V value, final ReferenceQueue<? super V> q) {
      super(checkNotNull(value, "value"), q);
      this.key = checkNotNull(key, "key");
    }

    final K getDescriptor() {
      return key;
    }
  }
}
