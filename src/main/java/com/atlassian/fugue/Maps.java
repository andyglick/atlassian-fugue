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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;

public final class Maps {
  private Maps() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  /**
   * Union with a combining function per key.
   * 
   * @param f function for selecting the right combining function from the key.
   */
  public static <K, V> Semigroup<Map<K, V>> unionWithKey(final Function<K, Semigroup<V>> f) {
    return new Semigroup<Map<K, V>>() {
      public Map<K, V> append(Map<K, V> left, Map<K, V> right) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        MapDifference<K, V> diff = com.google.common.collect.Maps.difference(left, right);
        builder.putAll(diff.entriesOnlyOnLeft());
        builder.putAll(diff.entriesOnlyOnRight());
        for (Entry<K, V> e : diff.entriesInCommon().entrySet()) {
          K key = e.getKey();
          V value = e.getValue(); // entries in common have the same value
          builder.put(key, f.apply(key).append(value, value));
        }
        for (Entry<K, ValueDifference<V>> e : diff.entriesDiffering().entrySet()) {
          K key = e.getKey();
          ValueDifference<V> v = e.getValue();
          builder.put(key, f.apply(key).append(v.leftValue(), v.rightValue()));
        }
        return builder.build();
      }
    };
  }

  /**
   * Union with a combining function.
   * 
   * @param semi the combining function.
   */
  public static <K, V> Semigroup<Map<K, V>> unionWith(final Semigroup<V> semi) {
    return unionWithKey(Functions.<K, Semigroup<V>> constant(semi));
  }
}
