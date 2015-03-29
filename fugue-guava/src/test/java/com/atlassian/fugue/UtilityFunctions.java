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

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;


public class UtilityFunctions {

  public static final BiFunction<Integer, Integer, Integer> product = (a, b) -> a * b;

  static <A> Function<Class<A>, Either<Exception, A>> defaultCtor() {
    return klass -> {
      try {
        final Constructor<A> declaredConstructor = klass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return right(declaredConstructor.newInstance());
      } catch (final Exception e) {
        return left(e);
      }
    };
  }

}
