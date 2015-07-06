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
package com.atlassian.fugue.extras;

import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.collect.Maps.immutableEntry;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImmutableMapsTest {

  @Test public void mapEntry() {
    BiFunction<String, Integer, Map.Entry<String, Integer>> mapEntryFunction = ImmutableMaps.mapEntry();
    Map.Entry<String, Integer> entry = mapEntryFunction.apply("abc", 1);
    assertThat(entry.getKey(), equalTo("abc"));
    assertThat(entry.getValue(), equalTo(1));
  }

  @Test public void toMap() {
    Iterable<Map.Entry<String, Integer>> source = list(immutableEntry("a", 1), immutableEntry("b", 2), immutableEntry("c", 3));

    assertThat(ImmutableMaps.toMap(source), equalTo(ImmutableMap.of("a", 1, "b", 2, "c", 3)));
  }

  @Test public void toMapContainingNull() {
    assertThat(ImmutableMaps.toMap(list(immutableEntry("a", 1), null, immutableEntry("c", 3))), equalTo(ImmutableMap.of("a", 1, "c", 3)));
  }

  @Test public void toMapWithNullKey() {
    assertThat(ImmutableMaps.toMap(list(immutableEntry("a", 1), Maps.<String, Integer> immutableEntry(null, 2), immutableEntry("c", 3))),
      equalTo(ImmutableMap.of("a", 1, "c", 3)));
  }

  @Test public void toMapWithNullValue() {
    assertThat(ImmutableMaps.toMap(list(immutableEntry("a", 1), Maps.<String, Integer> immutableEntry("b", null), immutableEntry("c", 3))),
      equalTo(ImmutableMap.of("a", 1, "c", 3)));
  }

  @Test public void toMapFunction() {
    assertThat(ImmutableMaps.toMap(list(1, 2, 3), Functions.<Integer> identity(), Object::toString), equalTo(ImmutableMap.of(1, "1", 2, "2", 3, "3")));
  }

  @Test public void toMapFunctionVariance() {
    Function<GrandParent, Child> f = input -> new Child(input.num());
    assertThat(ImmutableMaps.<Parent, Parent, Parent> toMap(list(new Parent(1), new Parent(2), new Parent(3)), f, f),
      equalTo(ImmutableMap.<Parent, Parent> of(new Child(1), new Child(1), new Child(2), new Child(2), new Child(3), new Child(3))));
  }

  // Allow override instead of throwing exceptions?
  @Test(expected = IllegalArgumentException.class) public void toMapWithDuplicateKey() {
    ImmutableMaps.toMap(list(immutableEntry("a", 1), immutableEntry("b", 2), immutableEntry("b", 3)));
  }

  @Test public void transformIterablesToMap() {
    assertThat(ImmutableMaps.toMap(list(1, 2, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input * -1;
      }
    }), equalTo(ImmutableMap.of("-1", -1, "-2", -2, "-3", -3)));
  }

  @Test public void transformIterablesToMapContainingNull() {
    assertThat(ImmutableMaps.toMap(list(1, null, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? 0 : input * -1;
      }
    }), equalTo(ImmutableMap.of("-1", -1, "-null", 0, "-3", -3)));
  }

  @Test public void transformIterablesToMapGeneratingNullKey() {
    assertThat(ImmutableMaps.toMap(list(1, null, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return input == null ? null : "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? 0 : input * -1;
      }
    }), equalTo(ImmutableMap.of("-1", -1, "-3", -3)));
  }

  @Test public void transformIterablesToMapGeneratingNullValue() {
    assertThat(ImmutableMaps.toMap(list(1, null, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "-" + input;
      }
    }, new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input == null ? null : input * -1;
      }
    }), equalTo(ImmutableMap.of("-1", -1, "-3", -3)));
  }

  @Test(expected = IllegalArgumentException.class) public void transformIterablesToMapGeneratingDuplicateKey() {
    ImmutableMaps.toMap(list(1, 2, 3), new Function<Integer, String>() {
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
    assertThat(ImmutableMaps.mapBy(list(1, 2, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }), equalTo(ImmutableMap.of("+1", 1, "+2", 2, "+3", 3)));
  }

  @Test public void mapByContravariantKeyFunction() {
    assertThat(ImmutableMaps.mapBy(list(new Child(1), new Child(2), new Child(3)), new Function<Parent, String>() {
      @Override public String apply(Parent input) {
        return "+" + input.num();
      }
    }), equalTo(ImmutableMap.of("+1", new Child(1), "+2", new Child(2), "+3", new Child(3))));
  }

  @Test public void mapByContainingNull() {
    assertThat(ImmutableMaps.mapBy(list(1, null, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }), equalTo(ImmutableMap.of("+1", 1, "+3", 3)));
  }

  @Test(expected = IllegalArgumentException.class) public void mapByDuplicateKey() {
    ImmutableMaps.mapBy(list(1, 2, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + (input % 2);
      }
    });
  }

  @Test public void mapTo() {
    assertThat(ImmutableMaps.mapTo(list(1, 2, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }), equalTo(ImmutableMap.of(1, "+1", 2, "+2", 3, "+3")));
  }

  @Test public void mapToContainingNull() {
    assertThat(ImmutableMaps.mapTo(list(1, null, 3), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    }), equalTo(ImmutableMap.of(1, "+1", 3, "+3")));
  }

  @Test(expected = IllegalArgumentException.class) public void mapToContainingDuplicates() {
    ImmutableMaps.mapTo(list(1, 2, 1), new Function<Integer, String>() {
      @Override public String apply(Integer input) {
        return "+" + input;
      }
    });
  }

  @Test public void mapToVariance() {
    assertThat(ImmutableMaps.<Integer, Parent> mapTo(list(1, 2, 3), new Function<Number, Child>() {
      @Override public Child apply(Number input) {
        return new Child((Integer) input);
      }
    }), equalTo(ImmutableMap.of(1, new Parent(1), 2, new Parent(2), 3, new Parent(3))));
  }

  @Test public void transformEntries() {
    assertThat(
      ImmutableMaps.transform(ImmutableMap.of("a", 1, "b", 2, "c", 3), new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
        @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
          return immutableEntry(input.getValue() * 2, input.getKey() + input.getKey());
        }
      }), equalTo(ImmutableMap.of(2, "aa", 4, "bb", 6, "cc")));
  }

  @Test public void transformEntriesContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("c", 3);

    assertThat(ImmutableMaps.transform(source, new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
      @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
        return immutableEntry(input.getValue() * 2, input.getKey() + input.getKey());
      }
    }), equalTo(ImmutableMap.of(2, "aa", 4, "nullnull", 6, "cc")));
  }

  @Test public void transformEntriesContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "c");

    assertThat(ImmutableMaps.transform(source, new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
      @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
        return immutableEntry(input.getValue() + input.getValue(), input.getKey() * 2);
      }
    }), equalTo(ImmutableMap.of("aa", 2, "nullnull", 4, "cc", 6)));
  }

  @Test public void transformEntriesReturningNull() {
    assertThat(
      ImmutableMaps.transform(ImmutableMap.of(1, "a", 2, "b", 3, "c"), new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
        @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
          return input.getKey() % 2 == 0 ? null : immutableEntry(input.getValue() + input.getValue(), input.getKey() * 2);
        }
      }), equalTo(ImmutableMap.of("aa", 2, "cc", 6)));
  }

  @Test public void transformEntriesReturningNullKey() {
    assertThat(
      ImmutableMaps.transform(ImmutableMap.of(1, "a", 2, "b", 3, "c"), new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
        @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
          return immutableEntry(input.getKey() % 2 == 0 ? null : input.getValue() + input.getValue(), input.getKey() * 2);
        }
      }), equalTo(ImmutableMap.of("aa", 2, "cc", 6)));
  }

  @Test public void transformEntriesReturningNullValue() {
    assertThat(
      ImmutableMaps.transform(ImmutableMap.of(1, "a", 2, "b", 3, "c"), new Function<Map.Entry<Integer, String>, Map.Entry<String, Integer>>() {
        @Override public Map.Entry<String, Integer> apply(@Nullable Map.Entry<Integer, String> input) {
          return immutableEntry(input.getValue() + input.getValue(), input.getKey() % 2 == 0 ? null : input.getKey() * 2);
        }
      }), equalTo(ImmutableMap.of("aa", 2, "cc", 6)));
  }

  @Test(expected = IllegalArgumentException.class) public void transformEntriesReturningDuplicateKey() {
    ImmutableMaps.transform(ImmutableMap.of("a", 1, "b", 2, "c", 3), new Function<Map.Entry<String, Integer>, Map.Entry<Integer, String>>() {
      @Override public Map.Entry<Integer, String> apply(@Nullable Map.Entry<String, Integer> input) {
        return immutableEntry(input.getValue() % 2, input.getKey() + input.getKey());
      }
    });
  }

  @Test public void transformKeysAndValues() {
    assertThat(ImmutableMaps.transform(ImmutableMap.of("a", 1, "bb", 2, "ccc", 3), new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }), equalTo(ImmutableMap.of(1, true, 2, false, 3, true)));
  }

  @Test public void transformKeysAndValuesContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    assertThat(ImmutableMaps.transform(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }), equalTo(ImmutableMap.of(1, true, 0, false, 3, true)));
  }

  @Test public void transformKeysAndValuesContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    assertThat(ImmutableMaps.transform(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }), equalTo(ImmutableMap.of("(1)", 1, "(2)", 0, "(3)", 3)));
  }

  @Test public void transformKeysAndValuesReturningNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    assertThat(ImmutableMaps.transform(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }), equalTo(ImmutableMap.of(1, true, 3, true)));
  }

  @Test public void transformKeysAndValuesReturningNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    assertThat(ImmutableMaps.transform(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }), equalTo(ImmutableMap.of("(1)", 1, "(3)", 3)));
  }

  @Test(expected = IllegalArgumentException.class) public void transformKeysAndValuesReturningDuplicateKey() {
    ImmutableMaps.transform(ImmutableMap.of(1, "a", 2, "b", 3, "c"), new Function<Integer, Boolean>() {
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
    assertThat(ImmutableMaps.transformKey(ImmutableMap.of("a", 10, "bb", 20, "ccc", 30), new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }), equalTo(ImmutableMap.of(1, 10, 2, 20, 3, 30)));
  }

  @Test public void transformKeyContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 10);
    source.put(null, 20);
    source.put("ccc", 30);

    assertThat(ImmutableMaps.transformKey(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }), equalTo(ImmutableMap.of(1, 10, 0, 20, 3, 30)));
  }

  @Test public void transformKeyContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(1, "a");
    source.put(2, null);
    source.put(3, "ccc");

    assertThat(ImmutableMaps.transformKey(source, new Function<Integer, String>() {
      @Override public String apply(@Nullable Integer input) {
        return "(" + input + ")";
      }
    }), equalTo(ImmutableMap.of("(1)", "a", "(3)", "ccc")));
  }

  @Test public void transformKeyReturningNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 11);
    source.put(null, 12);
    source.put("ccc", 13);

    assertThat(ImmutableMaps.transformKey(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }), equalTo(ImmutableMap.of(1, 11, 3, 13)));
  }

  @Test(expected = IllegalArgumentException.class) public void transformKeyReturningDuplicateKey() {
    ImmutableMaps.transformKey(ImmutableMap.of(1, "a", 2, "b", 3, "c"), new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    });
  }

  @Test public void transformValue() {
    assertThat(ImmutableMaps.transformValue(ImmutableMap.of("a", 1, "bb", 2, "ccc", 3), new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }), equalTo(ImmutableMap.of("a", true, "bb", false, "ccc", true)));
  }

  @Test public void transformValueContainingNullKey() {
    Map<String, Integer> source = Maps.newHashMap();
    source.put("a", 1);
    source.put(null, 2);
    source.put("ccc", 3);

    assertThat(ImmutableMaps.transformValue(source, new Function<Integer, Boolean>() {
      @Override public Boolean apply(@Nullable Integer input) {
        return input != null && input % 2 != 0;
      }
    }), equalTo(ImmutableMap.of("a", true, "ccc", true)));
  }

  @Test public void transformValueContainingNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(11, "a");
    source.put(12, null);
    source.put(13, "ccc");

    assertThat(ImmutableMaps.transformValue(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? 0 : input.length();
      }
    }), equalTo(ImmutableMap.of(11, 1, 12, 0, 13, 3)));
  }

  @Test public void transformValueReturningNullValue() {
    Map<Integer, String> source = Maps.newHashMap();
    source.put(11, "a");
    source.put(12, null);
    source.put(13, "ccc");

    assertThat(ImmutableMaps.transformValue(source, new Function<String, Integer>() {
      @Override public Integer apply(@Nullable String input) {
        return input == null ? null : input.length();
      }
    }), equalTo(ImmutableMap.of(11, 1, 13, 3)));
  }

  @Test public void collectEntries() {
    assertThat(ImmutableMaps.collect(ImmutableMap.of(1, "a", 2, "bb", 3, "ccc"),
      new Function<Map.Entry<Integer, String>, Option<Map.Entry<String, Integer>>>() {
        @Override public Option<Map.Entry<String, Integer>> apply(@Nullable Map.Entry<Integer, String> input) {
          if (input == null || input.getKey() == null || input.getKey() % 2 == 0 || input.getValue() == null) {
            return Option.none();
          }
          return Option.some(immutableEntry(String.valueOf(input.getKey() * 2), input.getValue().length()));
        }
      }), equalTo(ImmutableMap.of("2", 1, "6", 3)));
  }

  @Test public void collectKeysAndValuesWithEitherReturningNone() {
    assertThat(ImmutableMaps.collect(ImmutableMap.of(1, "a", 2, "bbb", 3, "cc"), new Function<Integer, Option<String>>() {
      @Override public Option<String> apply(@Nullable Integer input) {
        return input != null && input % 2 > 0 ? Option.some(String.valueOf(input * 2)) : Option.none(String.class);
      }
    }, new Function<String, Option<Integer>>() {
      @Override public Option<Integer> apply(@Nullable String input) {
        return input != null && input.length() % 2 > 0 ? Option.some(input.length()) : Option.none(Integer.class);
      }
    }), equalTo(ImmutableMap.of("2", 1)));
  }

  @Test public void collectByKey() {
    assertThat(ImmutableMaps.collectByKey(ImmutableMap.of(1, "a", 2, "bb", 3, "cccc"), new Function<Integer, Option<String>>() {
      @Override public Option<String> apply(@Nullable Integer input) {
        return input != null && input % 2 > 0 ? Option.some(String.valueOf(input * 2)) : Option.none(String.class);
      }
    }), equalTo(ImmutableMap.of("2", "a", "6", "cccc")));
  }

  @Test public void collectByKeyContravariantKeyFunction() {
    assertThat(ImmutableMaps.collectByKey(ImmutableMap.of(new Child(2), "a", new Child(3), "bb", new Child(6), "cccc"),
      new Function<Parent, Option<String>>() {
        @Override public Option<String> apply(@Nullable Parent input) {
          return input.num() % 2 == 0 ? Option.some(String.valueOf(input.num())) : Option.<String> none();
        }
      }), equalTo(ImmutableMap.of("2", "a", "6", "cccc")));
  }

  @Test public void collectByValue() {
    assertThat(ImmutableMaps.collectByValue(ImmutableMap.of(1, "a", 2, "bb", 3, "ccccc"), new Function<String, Option<Integer>>() {
      @Override public Option<Integer> apply(@Nullable String input) {
        return input != null && input.length() % 2 > 0 ? Option.some(input.length()) : Option.none(Integer.class);
      }
    }), equalTo(ImmutableMap.of(1, 1, 3, 5)));
  }

  @Test public void collectByValueContravariantKeyFunction() {
    assertThat(
      ImmutableMaps.collectByValue(ImmutableMap.of(2, new Child(2), 3, new Child(3), 6, new Child(6)), new Function<Parent, Option<String>>() {
        @Override public Option<String> apply(@Nullable Parent input) {
          return input.num() % 2 == 0 ? Option.some(String.valueOf(input.num())) : Option.<String> none();
        }
      }), equalTo(ImmutableMap.of(2, "2", 6, "6")));
  }

  static <A> Iterable<A> list(A a) {
    @SuppressWarnings("unchecked")
    List<A> result = Arrays.asList(a);
    return result;
  }

  static <A> Iterable<A> list(A a1, A a2) {
    @SuppressWarnings("unchecked")
    List<A> result = Arrays.asList(a1, a2);
    return result;
  }

  static <A> Iterable<A> list(A a1, A a2, A a3) {
    @SuppressWarnings("unchecked")
    List<A> result = Arrays.asList(a1, a2, a3);
    return result;
  }

  static class GrandParent {
    int num() {
      return Integer.MIN_VALUE;
    }

    @Override public int hashCode() {
      return num();
    }

    @Override public boolean equals(Object obj) {
      return num() == ((GrandParent) obj).num();
    }
  }

  static class Parent extends GrandParent {
    final int num;

    Parent(int num) {
      this.num = num;
    }

    Parent() {
      this.num = -1;
    }

    int num() {
      return num;
    }
  }

  static class Child extends Parent {
    Child(int num) {
      super(num);
    }
  }
}
