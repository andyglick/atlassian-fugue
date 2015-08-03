package com.atlassian.fugue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.atlassian.fugue.Iterables.flatten;
import static com.atlassian.fugue.Monoid.monoid;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Semigroups.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Stream.empty;

/**
 * {@link Monoid} instances and factories.
 */
public final class Monoids {

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAdditionMonoid = monoid(intAdditionSemigroup, 0);

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplicationMonoid = monoid(intMultiplicationSemigroup, 1);

  /**
   * A monoid that adds doubles.
   */
  public static final Monoid<Double> doubleAdditionMonoid = monoid(doubleAdditionSemigroup, 0.0);

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoid(bigintAdditionSemigroup, BigInteger.ZERO);

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid = monoid(bigintMultiplicationSemigroup, BigInteger.ONE);

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAdditionMonoid = monoid(longAdditionSemigroup, 0L);

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplicationMonoid = monoid(longMultiplicationSemigroup, 1L);

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunctionMonoid = monoid(disjunctionSemigroup, false);

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoid(exclusiveDisjunctionSemiGroup, false);

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoid(conjunctionSemigroup, true);

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = new Monoid<String>() {
    @Override public String zero() {
      return "";
    }

    @Override public String sum(String a1, String a2) {
      return stringSemigroup.sum(a1, a2);
    }

    @Override public String sumIterable(Iterable<String> strings) {
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
  public static final Monoid<Unit> unitMonoid = monoid(unitSemigroup, Unit.VALUE);

  private Monoids() {
  }

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> functionMonoid(final Monoid<B> mb) {
    return monoid(functionSemigroup(mb), f -> mb.zero());
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
        return emptyList();
      }

      @Override public List<A> sumStream(final Stream<List<A>> ll) {
        return ll.flatMap(l -> l.stream()).collect(Collectors.toList());
      }

      @Override public List<A> sumIterable(final Iterable<List<A>> ll) {
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
        return emptyList();
      }

      @Override public Iterable<A> sum(Iterable<A> l1, Iterable<A> l2) {
        return Semigroups.<A>iterableSemigroup().sum(l1, l2);
      }

      @Override public Iterable<A> sumIterable(Iterable<Iterable<A>> iterables) {
        return flatten(iterables);
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
        return empty();
      }

      @Override public Stream<A> sum(Stream<A> a1, Stream<A> a2) {
        return Semigroups.<A>streamSemigroup().sum(a1, a2);
      }

      @Override public Stream<A> sumStream(Stream<Stream<A>> as) {
        return as.flatMap(identity());
      }
    };
  }

  /**
   * A monoid for options (that take the first available value).
   *
   * @return A monoid for options (that take the first available value).
   */
  public static <A> Monoid<Option<A>> optionMonoid() {
    return monoid(optionSemigroup(), none());
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoid(Semigroups.lastOptionSemigroup(), none());
  }

}
