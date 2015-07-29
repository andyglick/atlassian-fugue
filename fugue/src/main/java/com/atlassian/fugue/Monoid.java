package com.atlassian.fugue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall  x y z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 */
public interface Monoid<A> extends Semigroup<A> {

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  A zero();

  /**
   * Sums the given values.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public default A sum(final Iterable<A> as) {
    A m = zero();
    for (A a : as) {
      m = sum(m, a);
    }
    return m;
  }

  /**
   * Sums the given values.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public default A sum(final Stream<A> as) {
    return as.reduce(zero(), this);
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>)
   *
   * @param n multiplier
   * @param a the value to multiply
   * @return <code>a</code> summed <code>n</code> times. If <code>n <= 0</code>, returns <code>zero()</code>
   */
  public default A multiply(final int n, final A a) {
    A m = zero();
    for (int i = 0; i < n; i++) {
      m = sum(m, a);
    }
    return m;
  }

  /**
   * Intersperses the given value between each two elements of the stream, and sums the result.
   *
   * @param as An stream of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public default A join(final Stream<A> as, final A a) {
    return as.reduce((a1, a2) -> sum(a1, sum(a, a2))).orElse(zero());
  }

  /**
   * Intersperses the given value between each two elements of the collection, and sums the result.
   *
   * @param as An stream of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public default A join(final Iterable<A> as, final A a) {
    return sum(Iterables.intersperse(as, a));
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal laws.
   *
   * @param semigroup The semigroup for the monoid.
   * @param zero      The zero for the monoid.
   * @return A monoid instance that uses the given semigroup and zero value.
   */
  public static <A> Monoid<A> monoid(final Semigroup<A> semigroup, final A zero) {
    return new Monoid<A>() {

      @Override
      public A sum(final A a1, final A a2) {
        return semigroup.sum(a1, a2);
      }

      @Override
      public A zero() {
        return zero;
      }
    };
  }

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAdditionMonoid = monoid(Semigroup.intAdditionSemigroup, 0);

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplicationMonoid = monoid(Semigroup.intMultiplicationSemigroup, 1);

  /**
   * A monoid that adds doubles.
   */
  public static final Monoid<Double> doubleAdditionMonoid = monoid(Semigroup.doubleAdditionSemigroup, 0.0);

  /**
   * A monoid that multiplies doubles.
   */
  public static final Monoid<Double> doubleMultiplicationMonoid = monoid(Semigroup.doubleMultiplicationSemigroup, 1.0);

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoid(Semigroup.bigintAdditionSemigroup, BigInteger.ZERO);

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid = monoid(Semigroup.bigintMultiplicationSemigroup, BigInteger.ONE);

  /**
   * A monoid that adds big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalAdditionMonoid = monoid(Semigroup.bigdecimalAdditionSemigroup, BigDecimal.ZERO);

  /**
   * A monoid that multiplies big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalMultiplicationMonoid = monoid(Semigroup.bigdecimalMultiplicationSemigroup, BigDecimal.ONE);

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAdditionMonoid = monoid(Semigroup.longAdditionSemigroup, 0L);

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplicationMonoid = monoid(Semigroup.longMultiplicationSemigroup, 1L);

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunctionMonoid = monoid(Semigroup.disjunctionSemigroup, false);

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoid(Semigroup.exclusiveDisjunctionSemiGroup, false);

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoid(Semigroup.conjunctionSemigroup, true);

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = new Monoid<String>() {
    @Override
    public String zero() {
      return "";
    }

    @Override
    public String sum(String a1, String a2) {
      return a1.concat(a2);
    }

    @Override
    public String sum(Iterable<String> strings) {
      StringBuilder sb = new StringBuilder();
      for (String s : strings) {
        sb.append(s);
      }
      return sb.toString();
    }
  };

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> functionMonoid(final Monoid<B> mb) {
    return monoid(Semigroup.functionSemigroup(mb), f -> mb.zero());
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> listMonoid() {
    return new Monoid<List<A>>() {
      @Override
      public List<A> sum(final List<A> a1, final List<A> a2) {
        return Semigroup.<A>listSemigroup().sum(a1, a2);
      }

      @Override
      public List<A> zero() {
        return Collections.emptyList();
      }

      @Override
      public List<A> sum(final Stream<List<A>> ll) {
        return ll.flatMap(l -> l.stream()).collect(Collectors.toList());
      }

      @Override
      public List<A> sum(final Iterable<List<A>> ll) {
        final List<A> r = new ArrayList<>();
        for (final List<A> l : ll) {
          r.addAll(l);
        }
        return r;
      }
    };
  }

  /**
   * A monoid for iterables.
   *
   * @return A monoid for iterables.
   */
  public static <A> Monoid<Iterable<A>> iterableMonoid() {
    return new Monoid<Iterable<A>>() {
      @Override
      public Iterable<A> zero() {
        return Collections.emptyList();
      }

      @Override
      public Iterable<A> sum(Iterable<A> l1, Iterable<A> l2) {
        return Iterables.flatten(Arrays.asList(l1, l2));
      }

      @Override
      public Iterable<A> sum(Iterable<Iterable<A>> iterables) {
        return Iterables.flatten(iterables);
      }
    };
  }

  /**
   * A monoid for streams.
   *
   * @return A monoid for streams.
   */
  public static <A> Monoid<Stream<A>> streamSemigroup() {
    return new Monoid<Stream<A>>() {
      @Override
      public Stream<A> zero() {
        return Stream.empty();
      }

      @Override
      public Stream<A> sum(Stream<A> a1, Stream<A> a2) {
        return Stream.concat(a1, a2);
      }

      @Override
      public Stream<A> sum(Stream<Stream<A>> as) {
        return as.flatMap(Function.identity());
      }
    };
  }

  /**
   * A monoid for options (that take the first available value).
   *
   * @return A monoid for options (that take the first available value).
   */
  public static <A> Monoid<Option<A>> optionMonoid() {
    return monoid(Semigroup.optionSemigroup(), Option.none());
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoid(Semigroup.lastOptionSemigroup(), Option.none());
  }

  /**
   * A monoid for unary products.
   *
   * @param sa A monoid for the product's type.
   * @return A monoid for the unary product.
   */
  public static <P, A> Monoid<P> p1Monoid(final Function<P, A> getA, final Monoid<A> sa, final Function<A, P> toP) {
    return monoid(Semigroup.p1Semigroup(getA, sa, toP), toP.apply(sa.zero()));
  }

  /**
   * A monoid for binary products.
   *
   * @param sa A monoid for the product's first type.
   * @param sb A monoid for the product's second type.
   * @return A monoid for the binary product.
   */
  public static <P, A, B> Monoid<P> p2Monoid(final Function<P, A> getA, final Monoid<A> sa, final Function<P, B> getB, final Monoid<B> sb,
    final BiFunction<A, B, P> toP) {
    return monoid(Semigroup.p2Semigroup(getA, sa, getB, sb, toP), toP.apply(sa.zero(), sb.zero()));
  }

  /**
   * A monoid for a {@link Pair}
   *
   * @param sa A monoid for the pair left type.
   * @param sb A monoid for the pair right type.
   * @return A monoid a pair.
   */
  public static <A, B> Semigroup<Pair<A, B>> pairMonoid(final Monoid<A> sa, final Monoid<B> sb) {
    return monoid(Semigroup.pairSemigroup(sa, sb), Pair.pair(sa.zero(), sb.zero()));
  }

  /**
   * A monoid for the Unit value.
   */
  public static final Monoid<Unit> unitSemigroup = monoid(Semigroup.unitSemigroup, Unit.VALUE);

}