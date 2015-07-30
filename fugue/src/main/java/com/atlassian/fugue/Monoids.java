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

import static com.atlassian.fugue.Monoid.monoid;

/**
 * {@link Monoid} instances and factories.
 */
public final class Monoids {

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAdditionMonoid = monoid(Semigroups.intAdditionSemigroup, 0);
  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplicationMonoid = monoid(Semigroups.intMultiplicationSemigroup, 1);
  /**
   * A monoid that adds doubles.
   */
  public static final Monoid<Double> doubleAdditionMonoid = monoid(Semigroups.doubleAdditionSemigroup, 0.0);
  /**
   * A monoid that multiplies doubles.
   */
  public static final Monoid<Double> doubleMultiplicationMonoid = monoid(Semigroups.doubleMultiplicationSemigroup, 1.0);
  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoid(Semigroups.bigintAdditionSemigroup, BigInteger.ZERO);
  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid = monoid(Semigroups.bigintMultiplicationSemigroup, BigInteger.ONE);
  /**
   * A monoid that adds big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalAdditionMonoid = monoid(Semigroups.bigdecimalAdditionSemigroup, BigDecimal.ZERO);
  /**
   * A monoid that multiplies big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalMultiplicationMonoid = monoid(Semigroups.bigdecimalMultiplicationSemigroup, BigDecimal.ONE);
  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAdditionMonoid = monoid(Semigroups.longAdditionSemigroup, 0L);
  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplicationMonoid = monoid(Semigroups.longMultiplicationSemigroup, 1L);
  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunctionMonoid = monoid(Semigroups.disjunctionSemigroup, false);
  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoid(Semigroups.exclusiveDisjunctionSemiGroup, false);
  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoid(Semigroups.conjunctionSemigroup, true);
  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = new Monoid<String>() {
    @Override public String zero() {
      return "";
    }

    @Override public String sum(String a1, String a2) {
      return a1.concat(a2);
    }

    @Override public String sum(Iterable<String> strings) {
      StringBuilder sb = new StringBuilder();
      for (String s : strings) {
        sb.append(s);
      }
      return sb.toString();
    }
  };
  /**
   * A monoid for the Unit value.
   */
  public static final Monoid<Unit> unitSemigroup = monoid(Semigroups.unitSemigroup, Unit.VALUE);

  private Monoids() {
  }

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> functionMonoid(final Monoid<B> mb) {
    return monoid(Semigroups.functionSemigroup(mb), f -> mb.zero());
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> listMonoid() {
    return new Monoid<List<A>>() {
      @Override public List<A> sum(final List<A> a1, final List<A> a2) {
        return Semigroups.<A>listSemigroup().sum(a1, a2);
      }

      @Override public List<A> zero() {
        return Collections.emptyList();
      }

      @Override public List<A> sum(final Stream<List<A>> ll) {
        return ll.flatMap(l -> l.stream()).collect(Collectors.toList());
      }

      @Override public List<A> sum(final Iterable<List<A>> ll) {
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
      @Override public Iterable<A> zero() {
        return Collections.emptyList();
      }

      @Override public Iterable<A> sum(Iterable<A> l1, Iterable<A> l2) {
        return Iterables.flatten(Arrays.asList(l1, l2));
      }

      @Override public Iterable<A> sum(Iterable<Iterable<A>> iterables) {
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
      @Override public Stream<A> zero() {
        return Stream.empty();
      }

      @Override public Stream<A> sum(Stream<A> a1, Stream<A> a2) {
        return Stream.concat(a1, a2);
      }

      @Override public Stream<A> sum(Stream<Stream<A>> as) {
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
    return monoid(Semigroups.optionSemigroup(), Option.none());
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoid(Semigroups.lastOptionSemigroup(), Option.none());
  }

}
