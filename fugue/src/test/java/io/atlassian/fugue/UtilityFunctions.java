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
package io.atlassian.fugue;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;

public class UtilityFunctions {
  public static final Predicate<Integer> isEven = dividableBy(2);

  public static final BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;

  public static final BiFunction<Integer, Integer, Integer> subtract = (a, b) -> a - b;

  public static final BiFunction<Integer, Integer, Integer> product = (a, b) -> a * b;

  public static Predicate<Integer> dividableBy(final int div) {
    return input -> input % div == 0;
  }

  public static Function<Integer, Integer> addOne = integer -> integer + 1;

  public static Function<Integer, Integer> square = input -> input * input;

  public static Function<Boolean, String> bool2String = String::valueOf;
  public static Function<Integer, String> int2String = String::valueOf;

  public static Function<String, String> reverse = from -> new StringBuilder(from).reverse().toString();

  public static BiFunction<String, Integer, Option<Character>> charAt = (s, i) -> s != null && i != null && i >= 0
    && i < s.length() ? Option.some(s.charAt(i)) : Option.<Character> none();

  public static Function<Pair<String, Integer>, Option<String>> leftOfString = pair -> pair != null
    && pair.left() != null && pair.right() != null && pair.right() >= 0 && pair.right() <= pair.left().length() ? Option
    .some(pair.left().substring(0, pair.right())) : Option.<String> none();

  public static Function<String, Function<Integer, Boolean>> hasMinLength = text -> min -> (text == null ? "" : text)
    .length() >= (min == null ? 0 : min);

  public static Function<Object, String> toStringFunction() {
    return Object::toString;
  }

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
