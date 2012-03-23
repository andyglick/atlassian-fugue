package com.atlassian.fugue;

import com.google.common.base.Function;

public class UtilityFunctions {
  public static Function<Integer, Integer> addOne = new Function<Integer, Integer>() {
    public Integer apply(final Integer integer) {
      return integer + 1;
    }
  };

  public static Function<Boolean, String> bool2String = new Function<Boolean, String>() {
    public String apply(final Boolean b) {
      return String.valueOf(b);
    }
  };
  public static Function<Integer, String> int2String = new Function<Integer, String>() {
    public String apply(final Integer i) {
      return String.valueOf(i);
    }
  };

  public static Function<String, String> reverse = new Function<String, String>() {
    public String apply(final String from) {
      return new StringBuilder(from).reverse().toString();
    }
  };
}
