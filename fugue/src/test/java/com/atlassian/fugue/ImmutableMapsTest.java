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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.junit.Test;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

public class ImmutableMapsTest {

  @Test public void mapEntry() {
    Function2<String, Integer, Map.Entry<String, Integer>> mapEntryFunction = ImmutableMaps.mapEntry();
    Map.Entry<String, Integer> entry = mapEntryFunction.apply("abc", 1);
    assertThat(entry.getKey(), equalTo("abc"));
    assertThat(entry.getValue(), equalTo(1));
  }

  @Test public void convertIterablesOfMapEntriesToMap() {
    @SuppressWarnings("unchecked")
    Iterable<Map.Entry<String, Integer>> source = Arrays
      .asList(Maps.immutableEntry("a", 1), Maps.immutableEntry("b", 2), Maps.immutableEntry("c", 3));

    ImmutableMap<String, Integer> expected = ImmutableMap.of("a", 1, "b", 2, "c", 3);
    assertEquals(expected, ImmutableMaps.toMap(source));
  }

  @Test public void convertIterablesOfMapEntriesToMapContainingNull() {
    @SuppressWarnings("unchecked")
    Iterable<Map.Entry<String, Integer>> source = Arrays.asList(Maps.immutableEntry("a", 1), null, Maps.immutableEntry("c", 3));

    ImmutableMap<String, Integer> expected = ImmutableMap.of("a", 1, "c", 3);
    assertEquals(expected, ImmutableMaps.toMap(source));
  }

  @Test public void convertIterablesOfMapEntriesToMapWithNullKey() {
    @SuppressWarnings("unchecked")
    Iterable<Map.Entry<String, Integer>> source = Arrays.asList(Maps.immutableEntry("a", 1), Maps.<String, Integer> immutableEntry(null, 2),
      Maps.immutableEntry("c", 3));

    ImmutableMap<String, Integer> expected = ImmutableMap.of("a", 1, "c", 3);
    assertEquals(expected, ImmutableMaps.toMap(source));
  }

  @Test public void convertIterablesOfMapEntriesToMapWithNullValue() {
    @SuppressWarnings("unchecked")
    Iterable<Map.Entry<String, Integer>> source = Arrays.asList(Maps.immutableEntry("a", 1), Maps.<String, Integer> immutableEntry("b", null),
      Maps.immutableEntry("c", 3));

    ImmutableMap<String, Integer> expected = ImmutableMap.of("a", 1, "c", 3);
    assertEquals(expected, ImmutableMaps.toMap(source));
  }

  // Allow override instead of throwing exceptions?
  @Test(expected = IllegalArgumentException.class) public void convertIterablesOfMapEntriesToMapWithDuplicateKey() {
    @SuppressWarnings("unchecked")
    Iterable<Map.Entry<String, Integer>> source = Arrays
      .asList(Maps.immutableEntry("a", 1), Maps.immutableEntry("b", 2), Maps.immutableEntry("b", 3));

    ImmutableMaps.toMap(source);
  }

  @Test public void transformIterablesToMap() {
    Iterable<Integer> source = Arrays.asList(1, 2, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("-1", -1, "-2", -2, "-3", -3);
    assertEquals(expected, ImmutableMaps.toMap(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input * -1;
      }
    }));
  }

  @Test public void transformIterablesToMapContainingNull() {
    Iterable<Integer> source = Arrays.asList(1, null, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("-1", -1, "-null", 0, "-3", -3);
    assertEquals(expected, ImmutableMaps.toMap(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? 0 : input * -1;
      }
    }));
  }

  @Test public void transformIterablesToMapGeneratingNullKey() {
    Iterable<Integer> source = Arrays.asList(1, null, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("-1", -1, "-3", -3);
    assertEquals(expected, ImmutableMaps.toMap(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return input == null ? null : "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? 0 : input * -1;
      }
    }));
  }

  @Test public void transformIterablesToMapGeneratingNullValue() {
    Iterable<Integer> source = Arrays.asList(1, null, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("-1", -1, "-3", -3);
    assertEquals(expected, ImmutableMaps.toMap(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? null : input * -1;
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void transformIterablesToMapGeneratingDuplicateKey() {
    Iterable<Integer> source = Arrays.asList(1, 2, 3);

    ImmutableMaps.toMap(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return String.valueOf(input % 2);
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input * -1;
      }
    });
  }

  @Test public void mapBy() {
    Iterable<Integer> source = Arrays.asList(1, 2, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("+1", 1, "+2", 2, "+3", 3);
    assertEquals(expected, ImmutableMaps.mapBy(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }));
  }

  @Test public void mapByContravariantKeyFunction() {
    Iterable<Child> source = Arrays.asList(new Child(1), new Child(2), new Child(3));

    ImmutableMap<String, Child> expected = ImmutableMap.of("+1", new Child(1), "+2", new Child(2), "+3", new Child(3));
    assertEquals(expected, ImmutableMaps.mapBy(source, new Function<Parent, String>() {
      @Override public String apply(Parent input) {
        return "+" + input.num();
      }
    }));
  }

  @Test public void mapByContainingNull() {
    Iterable<Integer> source = Arrays.asList(1, null, 3);

    ImmutableMap<String, Integer> expected = ImmutableMap.of("+1", 1, "+3", 3);
    assertEquals(expected, ImmutableMaps.mapBy(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void mapByDuplicateKey() {
    Iterable<Integer> source = Arrays.asList(1, 2, 3);

    ImmutableMaps.mapBy(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + (input % 2);
      }
    });
  }

  @Test public void mapTo() {
    Iterable<Integer> source = Arrays.asList(1, 2, 3);

    ImmutableMap<Integer, String> expected = ImmutableMap.of(1, "+1", 2, "+2", 3, "+3");
    assertEquals(expected, ImmutableMaps.mapTo(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }));
  }

  @Test public void mapToContainingNull() {
    Iterable<Integer> source = Arrays.asList(1, null, 3);

    ImmutableMap<Integer, String> expected = ImmutableMap.of(1, "+1", 3, "+3");
    assertEquals(expected, ImmutableMaps.mapTo(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void mapToContainingDuplicates() {
    Iterable<Integer> source = Arrays.asList(1, 2, 1);

    ImmutableMaps.mapTo(source, new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    });
  }

  @Test public void transformEntries() {
    Map<String, Integer> source = ImmutableMap.of("a", 1, "b", 2, "c", 3);

    ImmutableMap<Integer, String> expected = ImmutableMap.of(2, "aa", 4, "bb", 6, "cc");
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
      @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
        return Maps.immutableEntry(input.getValue() * 2, input.getKey() + input.getKey());
      }
    }));
  }

  @Test public void transformEntriesContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("c", 3);

    ImmutableMap<Integer, String> expected = ImmutableMap.of(2, "aa", 4, "nullnull", 6, "cc");
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
      @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
        return Maps.immutableEntry(input.getValue() * 2, input.getKey() + input.getKey());
      }
    }));
  }

  @Test public void transformEntriesContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "c");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("aa", 2, "nullnull", 4, "cc", 6);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
      @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
        return Maps.immutableEntry(input.getValue() + input.getValue(), input.getKey() * 2);
      }
    }));
  }

  @Test public void transformEntriesReturningNull() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "b", 3, "c");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("aa", 2, "cc", 6);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
      @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
        return input.getKey() % 2 == 0 ? null : Maps.immutableEntry(input.getValue() + input.getValue(), input.getKey() * 2);
      }
    }));
  }

  @Test public void transformEntriesReturningNullKey() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "b", 3, "c");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("aa", 2, "cc", 6);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
      @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
        return Maps.immutableEntry(input.getKey() % 2 == 0 ? null : input.getValue() + input.getValue(), input.getKey() * 2);
      }
    }));
  }

  @Test public void transformEntriesReturningNullValue() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "b", 3, "c");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("aa", 2, "cc", 6);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
      @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
        return Maps.immutableEntry(input.getValue() + input.getValue(), input.getKey() % 2 == 0 ? null : input.getKey() * 2);
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void transformEntriesReturningDuplicateKey() {
    Map<String, Integer> source = ImmutableMap.of("a", 1, "b", 2, "c", 3);

    ImmutableMaps.transform(source, new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
      @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
        return Maps.immutableEntry(input.getValue() % 2, input.getKey() + input.getKey());
      }
    });
  }

  @Test public void transformKeysAndValues() {
    Map<String, Integer> source = ImmutableMap.of("a", 1, "bb", 2, "ccc", 3);

    ImmutableMap<Integer, Boolean> expected = ImmutableMap.of(1, true, 2, false, 3, true);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }));
  }

  @Test public void transformKeysAndValuesContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    ImmutableMap<Integer, Boolean> expected = ImmutableMap.of(1, true, 0, false, 3, true);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }));
  }

  @Test public void transformKeysAndValuesContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("(1)", 1, "(2)", 0, "(3)", 3);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }));
  }

  @Test public void transformKeysAndValuesReturningNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    ImmutableMap<Integer, Boolean> expected = ImmutableMap.of(1, true, 3, true);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }));
  }

  @Test public void transformKeysAndValuesReturningNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    ImmutableMap<String, Integer> expected = ImmutableMap.of("(1)", 1, "(3)", 3);
    assertEquals(expected, ImmutableMaps.transform(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void transformKeysAndValuesReturningDuplicateKey() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "b", 3, "c");

    ImmutableMaps.transform(source, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    });
  }

  @Test public void transformKey() {
    Map<String, Integer> source = ImmutableMap.of("a", 10, "bb", 20, "ccc", 30);

    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(1, 10, 2, 20, 3, 30);
    assertEquals(expected, ImmutableMaps.transformKey(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }));
  }

  @Test public void transformKeyContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 10);
    source.put(null, 20);
    source.put("ccc", 30);

    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(1, 10, 0, 20, 3, 30);
    assertEquals(expected, ImmutableMaps.transformKey(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }));
  }

  @Test public void transformKeyContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    ImmutableMap<String, String> expected = ImmutableMap.of("(1)", "a", "(3)", "ccc");
    assertEquals(expected, ImmutableMaps.transformKey(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }));
  }

  @Test public void transformKeyReturningNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 11);
    source.put(null, 12);
    source.put("ccc", 13);

    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(1, 11, 3, 13);
    assertEquals(expected, ImmutableMaps.transformKey(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }));
  }

  @Test(expected = IllegalArgumentException.class) public void transformKeyReturningDuplicateKey() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "b", 3, "c");

    ImmutableMaps.transformKey(source, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    });
  }

  @Test public void transformValue() {
    Map<String, Integer> source = ImmutableMap.of("a", 1, "bb", 2, "ccc", 3);

    ImmutableMap<String, Boolean> expected = ImmutableMap.of("a", true, "bb", false, "ccc", true);
    assertEquals(expected, ImmutableMaps.transformValue(source, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }));
  }

  @Test public void transformValueContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    ImmutableMap<String, Boolean> expected = ImmutableMap.of("a", true, "ccc", true);
    assertEquals(expected, ImmutableMaps.transformValue(source, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }));
  }

  @Test public void transformValueContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(11, "a");
    source.put(12, null);
    source.put(13, "ccc");

    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(11, 1, 12, 0, 13, 3);
    assertEquals(expected, ImmutableMaps.transformValue(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }));
  }

  @Test public void transformValueReturningNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(11, "a");
    source.put(12, null);
    source.put(13, "ccc");

    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(11, 1, 13, 3);
    assertEquals(expected, ImmutableMaps.transformValue(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }));
  }

  @Test public void collectEntries() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "bb", 3, "ccc");
    ImmutableMap<String, Integer> expected = ImmutableMap.of("2", 1, "6", 3);
    assertEquals(expected, ImmutableMaps.collect(source, new Function<Map.Entry<Integer, String>, Option<Map.Entry<String, Integer>>>() {
      @Override public Option<Map.Entry<String, Integer>> apply(@Nullable Map.Entry<Integer, String> input) {
        if (input == null || input.getKey() == null || input.getKey() % 2 == 0 || input.getValue() == null) {
          return Option.none();
        }
        return Option.some(Maps.immutableEntry(String.valueOf(input.getKey() * 2), input.getValue().length()));
      }
    }));
  }

  @Test public void collectKeysAndValuesWithEitherReturningNone() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "bbb", 3, "cc");
    ImmutableMap<String, Integer> expected = ImmutableMap.of("2", 1);
    assertEquals(expected, ImmutableMaps.collect(source, new Function<Integer, Option<String>>() {
      @Override public Option<String> apply(@Nullable Integer input) {
        return input != null && input % 2 > 0 ? Option.some(String.valueOf(input * 2)) : Option.none(String.class);
      }
    }, new Function<String, Option<Integer>>() {
      @Override public Option<Integer> apply(@Nullable String input) {
        return input != null && input.length() % 2 > 0 ? Option.some(input.length()) : Option.none(Integer.class);
      }
    }));
  }

  @Test public void collectByKey() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "bb", 3, "cccc");
    ImmutableMap<String, String> expected = ImmutableMap.of("2", "a", "6", "cccc");
    assertEquals(expected, ImmutableMaps.collectByKey(source, new Function<Integer, Option<String>>() {
      @Override public Option<String> apply(@Nullable Integer input) {
        return input != null && input % 2 > 0 ? Option.some(String.valueOf(input * 2)) : Option.none(String.class);
      }
    }));
  }
  
  @Test public void collectByKeyContravariantKeyFunction() {
    Map<Child, String> source = ImmutableMap.of(new Child(2), "a", new Child(3), "bb", new Child(6), "cccc");
    ImmutableMap<String, String> expected = ImmutableMap.of("2", "a", "6", "cccc");
    assertEquals(expected, ImmutableMaps.collectByKey(source, new Function<Parent, Option<String>>() {
      @Override public Option<String> apply(@Nullable Parent input) {
        return input.num() % 2 == 0 ? Option.some(String.valueOf(input.num())) : Option.<String> none();
      }
    }));
  }

  @Test public void collectByValue() {
    Map<Integer, String> source = ImmutableMap.of(1, "a", 2, "bb", 3, "ccccc");
    ImmutableMap<Integer, Integer> expected = ImmutableMap.of(1, 1, 3, 5);
    assertEquals(expected, ImmutableMaps.collectByValue(source, new Function<String, Option<Integer>>() {
      @Override public Option<Integer> apply(@Nullable String input) {
        return input != null && input.length() % 2 > 0 ? Option.some(input.length()) : Option.none(Integer.class);
      }
    }));
  }

  @Test public void collectByValueContravariantKeyFunction() {
    Map<Integer, Child> source = ImmutableMap.of(2, new Child(2), 3, new Child(3), 6, new Child(6));
    ImmutableMap<Integer, String> expected = ImmutableMap.of(2, "2", 6, "6");
    assertEquals(expected, ImmutableMaps.collectByValue(source, new Function<Parent, Option<String>>() {
      @Override public Option<String> apply(@Nullable Parent input) {
        return input.num() % 2 == 0 ? Option.some(String.valueOf(input.num())) : Option.<String> none();
      }
    }));
  }

  static class Parent {
    int num() {
      return -1;
    }
  }

  static class Child extends Parent {
    final int num;

    Child(int num) {
      this.num = num;
    }

    int num() {
      return num;
    }

    @Override public int hashCode() {
      return num;
    }

    @Override public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      return num == ((Child) obj).num;
    }
  }
}
