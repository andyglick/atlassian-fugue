package com.atlassian.fugue.collect;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;

public class UtilityFunctions {
  public static final BiFunction<Integer, Integer, Integer> product = (a, b) -> a * b;

  public static Function<Boolean, String> bool2String = String::valueOf;

  public static BiFunction<String, Integer, Option<Character>> charAt = (s, i) -> s != null && i != null && i >= 0
      && i < s.length() ? Option.some(s.charAt(i)) : Option.<Character> none();

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
