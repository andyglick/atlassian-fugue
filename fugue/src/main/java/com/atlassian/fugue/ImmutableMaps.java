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
import com.google.common.collect.Maps;

/**
 * Provides some utility methods to convert Iterables to ImmutableMap, and to
 * transform Maps.
 * 
 * @since 2.0
 */
public class ImmutableMaps {

  // /CLOVER:OFF

  private ImmutableMaps() {}

  // /CLOVER:ON

  /**
   * Returns a function that takes a key of type K and a value of type V and
   * returns a Map entry.
   * 
   * @param <K> the key type
   * @param <V> the value type
   * @return a function that takes a K and a V and return the corresponding Map
   * entry
   */
  public static <K, V> Function2<K, V, Map.Entry<K, V>> mapEntry() {
    @SuppressWarnings("unchecked")
    MapEntryFunction<K, V> result = (MapEntryFunction<K, V>) MapEntryFunction.INSTANCE;
    return result;
  }

  private static class MapEntryFunction<K, V> implements Function2<K, V, Map.Entry<K, V>> {
    static final MapEntryFunction<Object, Object> INSTANCE = new MapEntryFunction<Object, Object>();

    @Override public Map.Entry<K, V> apply(K k, V v) {
      return Maps.immutableEntry(k, v);
    }
  }

  /**
   * Builds an immutable map from the given iterable of
   * {@link java.util.Map.Entry}.
   * <p>
   * Any <code>null</code> entries will be filtered out. Additionally, any
   * entries containing <code>null</code> key or value will also be filtered
   * out. If multiple entries return the same key,
   * {@link IllegalArgumentException} will be thrown.
   * 
   * @param <K> the key type
   * @param <V> the value type
   * @param from the iterable we use as the source
   * @return the transformed map
   */
  public static <K, V> ImmutableMap<K, V> toMap(Iterable<Map.Entry<K, V>> from) {
    ImmutableMap.Builder<K, V> mapBuilder = ImmutableMap.builder();
    for (Map.Entry<K, V> entry : from) {
      if (entry != null) {
        K key = entry.getKey();
        V value = entry.getValue();
        if (key != null && value != null) {
          mapBuilder.put(key, value);
        }
      }
    }
    return mapBuilder.build();
  }

  /**
   * Builds an immutable map from the given iterable, with key derived from the
   * application of the iterable to the keyTransformer, and value derived from
   * the application of the iterable to the valueTransformer.
   * <p>
   * <code>null</code> value is allowed and will be passed to the keyTransformer
   * and valueTransformer. However, if either the keyTransformer or the
   * valueTransformer returns <code>null</code> for an entry, the entry is
   * ignored. If keyTransformer returns the same key for multiple entries,
   * {@link IllegalArgumentException} will be thrown.
   * 
   * @param <T> the input type
   * @param <K> the key type
   * @param <V> the value type
   * @param from the iterable we use as the source
   * @param keyTransformer transform keys
   * @param valueTransformer transform values
   * @return the transformed map
   */
  public static <T, K, V> ImmutableMap<K, V> toMap(Iterable<T> from, final Function<? super T, ? extends K> keyTransformer,
    final Function<? super T, ? extends V> valueTransformer) {
    return toMap(com.google.common.collect.Iterables.transform(from, new Function<T, Map.Entry<K, V>>() {
      @Override public Map.Entry<K, V> apply(T input) {
        @SuppressWarnings("unchecked")
        Entry<K, V> entry = (Entry<K, V>) Maps.immutableEntry(keyTransformer.apply(input), valueTransformer.apply(input));
        return entry;
      }
    }));
  }

  /**
   * Builds an immutable map that is keyed by the result of applying
   * keyTransformer to each element of the given iterable of values.
   * <p>
   * <code>null</code> value is allowed but will be ignored. If keyTransformer
   * returns the same key for multiple entries, {@link IllegalArgumentException}
   * will be thrown.
   * 
   * @param <K> the key type
   * @param <V> the value type
   * @param from the iterable we use as the source
   * @param keyTransformer transform keys
   * @return the transformed map
   */
  public static <K, V> ImmutableMap<K, V> mapBy(Iterable<V> from, final Function<? super V, ? extends K> keyTransformer) {
    return toMap(from, keyTransformer, Functions.<V> identity());
  }

  /**
   * Builds an immutable map from the given iterable and compute the value by
   * applying the valueTransformer.
   * <p>
   * <code>null</code> value is allowed but will be ignored. If there are
   * duplicate entries in the iterable, {@link IllegalArgumentException} will be
   * thrown.
   * 
   * @param <K> the key type
   * @param <V> the value type
   * @param from the iterable we use as the source
   * @param valueTransformer transform values
   * @return the transformed map
   */
  public static <K, V> ImmutableMap<K, V> mapTo(Iterable<K> from, final Function<? super K, ? extends V> valueTransformer) {
    return toMap(from, Functions.<K> identity(), valueTransformer);
  }

  /**
   * Returns an immutable map that applies function to each entry of
   * {@code fromMap}. If <code>null</code> is returned by the function for any
   * entry, or if an entry returned by the function contains a <code>null</code>
   * key or value, that entry is discarded in the result. If the function
   * returns entries with the same key for multiple entries,
   * {@link IllegalArgumentException} will be thrown.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the map we use as the source
   * @param function transform keys and values
   * @return the transformed map
   */
  public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> function) {
    return toMap(com.google.common.collect.Iterables.transform(from.entrySet(), function));
  }

  /**
   * Returns an immutable map that applies the keyTransformer and
   * valueTransformer functions to each entry of {@code fromMap}. If for any
   * entry, a <code>null</code> key or value is returned, that entry is
   * discarded in the result. If the keyTransformer function returns the same
   * key for multiple entries, {@link IllegalArgumentException} will be thrown.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the map we use as the source
   * @param keyTransformer transform keys
   * @param valueTransformer transform values
   * @return the transformed map
   */
  public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, final Function<? super K1, ? extends K2> keyTransformer,
    final Function<? super V1, ? extends V2> valueTransformer) {
    return toMap(com.google.common.collect.Iterables.transform(from.entrySet(), new Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>>() {
      @Override public Map.Entry<K2, V2> apply(Map.Entry<K1, V1> input) {
        @SuppressWarnings("unchecked")
        Entry<K2, V2> entry = (Entry<K2, V2>) Maps.immutableEntry(keyTransformer.apply(input.getKey()), valueTransformer.apply(input.getValue()));
        return entry;
      }
    }));
  }

  /**
   * Returns an immutable map that applies keyTransformer to the key of each
   * entry of the source map. If <code>null</code> is returned by the
   * keyTransformer for any entry, that entry is discarded in the result. If an
   * entry contains a <code>null</code> value, it will also be discarded in the
   * result. If the {@code function} returns the same result key for multiple
   * keys, {@link IllegalArgumentException} will be thrown.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V> the value type
   * @param from the map we use as the source
   * @param keyTransformer transform keys
   * @return the transformed map
   */
  public static <K1, K2, V> ImmutableMap<K2, V> transformKey(Map<K1, V> from, final Function<? super K1, ? extends K2> keyTransformer) {
    return transform(from, keyTransformer, Functions.<V> identity());
  }

  /**
   * Returns an immutable map that applies valueTransformer to the value of each
   * entry of the source map. If <code>null</code> is returned by the
   * valueTransformer for any entry, that entry is discarded in the result. If
   * an entry contains a <code>null</code> key, it will also be discarded in the
   * result.
   * 
   * @param <K> the key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the iterable we use as the source
   * @param valueTransformer transform values
   * @return the transformed map
   */
  public static <K, V1, V2> ImmutableMap<K, V2> transformValue(Map<K, V1> from, final Function<? super V1, ? extends V2> valueTransformer) {
    return transform(from, Functions.<K> identity(), valueTransformer);
  }

  /**
   * Filters and maps (aka transforms) the source map.
   * 
   * Applies the given partial function to each entry of the unfiltered map. If
   * the application returns none, the entry will be left out; otherwise, the
   * transformed entry contained in the Option will be added to the result map.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the iterable we use as the source
   * @param partial transform and select entries
   * @return the transformed map
   */
  public static <K1, K2, V1, V2> ImmutableMap<K2, V2> collect(Map<K1, V1> from, Function<Map.Entry<K1, V1>, Option<Map.Entry<K2, V2>>> partial) {
    return toMap(Iterables.collect(from.entrySet(), partial));
  }

  /**
   * Filters and maps (aka transforms) the source map.
   * 
   * Applies the given partial key function and partial value function to the
   * key and value of each entry of the unfiltered map. If either of the
   * application returns none, the entry will be left out; otherwise, an entry
   * of transformed key and transformed value contained in the options will be
   * added to the result map.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the iterable we use as the source
   * @param keyPartial transform and collect keys
   * @param valuePartial transform and collect values
   * @return the transformed map
   */
  public static <K1, K2, V1, V2> ImmutableMap<K2, V2> collect(Map<K1, V1> from, final Function<? super K1, Option<K2>> keyPartial,
    final Function<? super V1, Option<V2>> valuePartial) {
    return collect(from, new Function<Map.Entry<K1, V1>, Option<Map.Entry<K2, V2>>>() {
      @Override public Option<Map.Entry<K2, V2>> apply(Map.Entry<K1, V1> input) {
        Option<K2> ok = keyPartial.apply(input.getKey());
        Option<V2> ov = valuePartial.apply(input.getValue());
        return Options.lift2(ImmutableMaps.<K2, V2> mapEntry()).apply(ok, ov);
      }
    });
  }

  /**
   * Filters and maps (aka transforms) the source map.
   * 
   * Applies the given partial key function to the key of each entry of the
   * unfiltered map. If the application returns none, the entry will be left
   * out; otherwise, an entry of transformed key contained in the option and the
   * original value will be added to the result map.
   * 
   * @param <K1> the input key type
   * @param <K2> the output key type
   * @param <V> the value type
   * @param from the iterable we use as the source
   * @param keyPartial transform and collect keys
   * @return the transformed map
   */
  public static <K1, K2, V> ImmutableMap<K2, V> collectByKey(Map<K1, V> from, final Function<? super K1, Option<K2>> keyPartial) {
    return collect(from, keyPartial, Option.<V> toOption());
  }

  /**
   * Filters and maps (aka transforms) the source map.
   * 
   * Applies the given partial value function to the value of each entry of the
   * unfiltered map. If the application returns none, the entry will be left
   * out; otherwise, an entry of the original key and the transformed key
   * contained in the option will be added to the result map.
   * 
   * @param <K> the key type
   * @param <V1> the input value type
   * @param <V2> the output value type
   * @param from the iterable we use as the source
   * @param valuePartial transform and collect values
   * @return the transformed map
   */
  public static <K, V1, V2> ImmutableMap<K, V2> collectByValue(Map<K, V1> from, final Function<? super V1, Option<V2>> valuePartial) {
    return collect(from, Option.<K> toOption(), valuePartial);
  }
}
