package com.atlassian.fugue;

import static com.atlassian.fugue.Functions.curry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;

class Monoids {
  private Monoids() {
    throw new UnsupportedOperationException();
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must
   * follow the monoidal laws.
   * 
   * @param sum The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final Function<A, Function<A, A>> sum, final A zero) {
    return new Monoid<A>(sum, zero);
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must
   * follow the monoidal laws.
   * 
   * @param sum The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final Function2<A, A, A> sum, final A zero) {
    return new Monoid<A>(curry(sum), zero);
  }

  /**
   * Constructs a monoid from the given semigroup and zero value, which must
   * follow the monoidal laws.
   * 
   * @param s The semigroup for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final Semigroup<A> s, final A zero) {
    return new Monoid<A>(s, zero);
  }

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
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoid(Semigroups.bigintAdditionSemigroup,
    BigInteger.ZERO);

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid = monoid(Semigroups.bigintMultiplicationSemigroup,
    BigInteger.ONE);

  /**
   * A monoid that adds big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalAdditionMonoid = monoid(Semigroups.bigdecimalAdditionSemigroup,
    BigDecimal.ZERO);

  /**
   * A monoid that multiplies big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalMultiplicationMonoid = monoid(
    Semigroups.bigdecimalMultiplicationSemigroup, BigDecimal.ONE);

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
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoid(Semigroups.exclusiveDisjunctionSemiGroup,
    false);

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoid(Semigroups.conjunctionSemigroup, true);

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = monoid(Semigroups.stringSemigroup, "");

  /**
   * A monoid that appends string buffers.
   */
  public static final Monoid<StringBuffer> stringBufferMonoid = monoid(Semigroups.stringBufferSemigroup,
    new StringBuffer());

  /**
   * A monoid that appends string builders.
   */
  public static final Monoid<StringBuilder> stringBuilderMonoid = monoid(Semigroups.stringBuilderSemigroup,
    new StringBuilder());

  /**
   * A monoid for functions.
   * 
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<Function<A, B>> functionMonoid(final Monoid<B> mb) {
    return monoid(Semigroups.<A, B> functionSemigroup(mb.semigroup()), Functions.<A, B> constant(mb.zero()));
  }

  /**
   * A monoid for lists.
   * 
   * @return A monoid for lists.
   */
  public static <A> Monoid<Iterable<A>> listMonoid() {
    return monoid(Semigroups.<A> iterableSemigroup(), Collections.<A> emptyList());
  }

  /**
   * A monoid for options.
   */
  public static <A> Monoid<Option<A>> optionMonoid() {
    return monoid(Semigroups.<A> optionSemigroup(), Option.<A> none());
  }

  /**
   * A monoid for options that take the first available value.
   */
  public static <A> Monoid<Option<A>> firstOptionMonoid() {
    return monoid(Semigroups.<A> firstOptionSemigroup(), Option.<A> none());
  }

  /**
   * A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoid(Semigroups.<A> lastOptionSemigroup(), Option.<A> none());
  }

  /**
   * A monoid for sets.
   * 
   * @param o An order for set elements.
   * @return A monoid for sets whose elements have the given order.
   */
  public static <A> Monoid<Set<A>> setMonoid() {
    return monoid(Semigroups.<A> setSemigroup(), Collections.<A> emptySet());
  }
}
