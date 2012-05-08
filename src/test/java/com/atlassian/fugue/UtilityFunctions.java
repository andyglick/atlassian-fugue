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

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;

import com.google.common.base.Function;

import java.lang.reflect.Constructor;

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

  static <A> Function<Class<A>, Either<Exception, A>> defaultCtor() {
    return new Function<Class<A>, Either<Exception, A>>() {
      @Override public Either<Exception, A> apply(final Class<A> klass) {
        try {
          final Constructor<A> declaredConstructor = klass.getDeclaredConstructor();
          declaredConstructor.setAccessible(true);
          return right(declaredConstructor.newInstance());
        } catch (final Exception e) {
          return left(e);
        }
      }
    };
  }
}
