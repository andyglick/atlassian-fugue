package com.atlassian.fugue;

import static com.atlassian.fugue.Functions.compose;
import static com.atlassian.fugue.Functions.flip;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import com.google.common.base.Function;

/**
 * Tests for equality between two objects.
 * 
 * @version %build.number%
 */
public final class Equal<A> {
  private final Function<A, Function<A, Boolean>> f;

  private Equal(final Function<A, Function<A, Boolean>> f) {
    this.f = f;
  }

  /**
   * Returns <code>true</code> if the two given arguments are equal,
   * <code>false</code> otherwise.
   * 
   * @param a1 An object to test for equality against another.
   * @param a2 An object to test for equality against another.
   * @return <code>true</code> if the two given arguments are equal,
   * <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return f.apply(a1).apply(a2);
  }

  /**
   * First-class equality check.
   * 
   * @return A function that returns <code>true</code> if the two given
   * arguments are equal.
   */
  public Function2<A, A, Boolean> eq() {
    return new Function2<A, A, Boolean>() {
      public Boolean apply(final A a, final A a1) {
        return eq(a, a1);
      }
    };
  }

  /**
   * Partially applied equality check.
   * 
   * @param a An object to test for equality against another.
   * @return A function that returns <code>true</code> if the given argument
   * equals the argument to this method.
   */
  public Function<A, Boolean> eq(final A a) {
    return new Function<A, Boolean>() {
      public Boolean apply(final A a1) {
        return eq(a, a1);
      }
    };
  }

  /**
   * Maps the given function across this equal as a contravariant functor.
   * 
   * @param f The function to map.
   * @return A new equal.
   */
  public <B> Equal<B> comap(final Function<B, A> f) {
    return equal(compose(flip(compose(this.f, f)), f));
  }

  /**
   * Constructs an equal instance from the given function.
   * 
   * @param f The function to construct the equal with.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equal(final Function<A, Function<A, Boolean>> f) {
    return new Equal<A>(f);
  }

  /**
   * Returns an equal instance that uses the {@link Object#equals(Object)}
   * method to test for equality.
   * 
   * @return An equal instance that uses the {@link Object#equals(Object)}
   * method to test for equality.
   */
  public static <A> Equal<A> anyEqual() {
    return new Equal<A>(new Function<A, Function<A, Boolean>>() {
      public Function<A, Boolean> apply(final A a1) {
        return new Function<A, Boolean>() {
          public Boolean apply(final A a2) {
            return a1.equals(a2);
          }
        };
      }
    });
  }

  /**
   * An equal instance for the <code>boolean</code> type.
   */
  public static final Equal<Boolean> booleanEqual = anyEqual();

  /**
   * An equal instance for the <code>byte</code> type.
   */
  public static final Equal<Byte> byteEqual = anyEqual();

  /**
   * An equal instance for the <code>char</code> type.
   */
  public static final Equal<Character> charEqual = anyEqual();

  /**
   * An equal instance for the <code>double</code> type.
   */
  public static final Equal<Double> doubleEqual = anyEqual();

  /**
   * An equal instance for the <code>float</code> type.
   */
  public static final Equal<Float> floatEqual = anyEqual();

  /**
   * An equal instance for the <code>int</code> type.
   */
  public static final Equal<Integer> intEqual = anyEqual();

  /**
   * An equal instance for the <code>BigInteger</code> type.
   */
  public static final Equal<BigInteger> bigintEqual = anyEqual();

  /**
   * An equal instance for the <code>BigDecimal</code> type.
   */
  public static final Equal<BigDecimal> bigdecimalEqual = anyEqual();

  /**
   * An equal instance for the <code>long</code> type.
   */
  public static final Equal<Long> longEqual = anyEqual();

  /**
   * An equal instance for the <code>short</code> type.
   */
  public static final Equal<Short> shortEqual = anyEqual();

  /**
   * An equal instance for the {@link String} type.
   */
  public static final Equal<String> stringEqual = anyEqual();

  /**
   * An equal instance for the {@link StringBuffer} type.
   */
  public static final Equal<StringBuffer> stringBufferEqual = new Equal<StringBuffer>(
    new Function<StringBuffer, Function<StringBuffer, Boolean>>() {
      public Function<StringBuffer, Boolean> apply(final StringBuffer sb1) {
        return new Function<StringBuffer, Boolean>() {
          public Boolean apply(final StringBuffer sb2) {
            if (sb1.length() == sb2.length()) {
              for (int i = 0; i < sb1.length(); i++)
                if (sb1.charAt(i) != sb2.charAt(i))
                  return false;
              return true;
            } else
              return false;
          }
        };
      }
    });

  /**
   * An equal instance for the {@link StringBuilder} type.
   */
  public static final Equal<StringBuilder> stringBuilderEqual = new Equal<StringBuilder>(
    new Function<StringBuilder, Function<StringBuilder, Boolean>>() {
      public Function<StringBuilder, Boolean> apply(final StringBuilder sb1) {
        return new Function<StringBuilder, Boolean>() {
          public Boolean apply(final StringBuilder sb2) {
            if (sb1.length() == sb2.length()) {
              for (int i = 0; i < sb1.length(); i++)
                if (sb1.charAt(i) != sb2.charAt(i))
                  return false;
              return true;
            } else
              return false;
          }
        };
      }
    });

  /**
   * An equal instance for the {@link Either} type.
   * 
   * @param ea Equality across the left side of {@link Either}.
   * @param eb Equality across the right side of {@link Either}.
   * @return An equal instance for the {@link Either} type.
   */
  public static <A, B> Equal<Either<A, B>> eitherEqual(final Equal<A> ea, final Equal<B> eb) {
    return new Equal<Either<A, B>>(new Function<Either<A, B>, Function<Either<A, B>, Boolean>>() {
      public Function<Either<A, B>, Boolean> apply(final Either<A, B> e1) {
        return new Function<Either<A, B>, Boolean>() {
          public Boolean apply(final Either<A, B> e2) {
            return e1.isLeft() && e2.isLeft() && ea.f.apply(e1.left().get()).apply(e2.left().get()) || e1.isRight()
              && e2.isRight() && eb.f.apply(e1.right().get()).apply(e2.right().get());
          }
        };
      }
    });
  }

  /**
   * An equal instance for the {@link Iterable} type.
   * 
   * @param ea Equality across the elements of the iterable.
   * @return An equal instance for the {@link Iterable} type.
   */
  public static <A> Equal<Iterable<A>> iterableEqual(final Equal<A> equals) {
    return new Equal<Iterable<A>>(new Function<Iterable<A>, Function<Iterable<A>, Boolean>>() {
      public Function<Iterable<A>, Boolean> apply(final Iterable<A> a1) {
        return new Function<Iterable<A>, Boolean>() {
          public Boolean apply(final Iterable<A> a2) {
            Iterator<A> x1 = a1.iterator();
            Iterator<A> x2 = a2.iterator();
            while (x1.hasNext() && x2.hasNext()) {
              if (!equals.eq(x1.next(), x2.next()))
                return false;
            }
            return x1.hasNext() && x2.hasNext();
          }
        };
      }
    });
  }

  /**
   * An equal instance for the {@link Option} type.
   * 
   * @param ea Equality across the element of the option.
   * @return An equal instance for the {@link Option} type.
   */
  public static <A> Equal<Option<A>> optionEqual(final Equal<A> ea) {
    return new Equal<Option<A>>(new Function<Option<A>, Function<Option<A>, Boolean>>() {
      public Function<Option<A>, Boolean> apply(final Option<A> o1) {
        return new Function<Option<A>, Boolean>() {
          public Boolean apply(final Option<A> o2) {
            return o1.isEmpty() && o2.isEmpty() || o1.isDefined() && o2.isDefined()
              && ea.f.apply(o1.get()).apply(o2.get());
          }
        };
      }
    });
  }
}