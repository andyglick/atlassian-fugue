package com.atlassian.fugue;

import static com.atlassian.fugue.Iterables.foldLeft;

import com.google.common.base.Function;


/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must
 * follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @since 1.2
 */
public final class Monoid<A> {
  private final Semigroup<A> semi;
  private final A zero;

  Monoid(final Semigroup<A> sum, final A zero) {
    this.semi = sum;
    this.zero = zero;
  }

  Monoid(final Function<A, Function<A, A>> sum, final A zero) {
    this(Semigroups.semigroup(sum), zero);
  }

  /**
   * Returns a semigroup projection of this monoid.
   *
   * @return A semigroup projection of this monoid.
   */
  public Semigroup<A> semigroup() {
    return semi;
  }

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The of the two given arguments.
   */
  public A sum(final A a1, final A a2) {
    return semi.append(a1, a2);
  }

  /**
   * Returns a function that sums the given value according to this monoid.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this monoid.
   */
  public Function<A, A> sum(final A a1) {
    return Semigroups.sum(a1, semi);
  }

  /**
   * Returns a function that sums according to this monoid.
   *
   * @return A function that sums according to this monoid.
   */
  public Function<A, Function<A, A>> sum() {
    return Semigroups.sum(semi);
  }

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  public A zero() {
    return zero;
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
//  public A sumRight(final Iterable<A> as) {
//    return Iterables.foldRight(sum, zero);
//  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
//  public A sumRight(final Stream<A> as) {
//    return as.foldRight(new F2<A, P1<A>, A>() {
//      public A f(final A a, final P1<A> ap1) {
//        return sum(a, ap1._1());
//      }
//    }, zero);
//  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final Iterable<A> as) {
    return foldLeft(as, zero, semi);
  }

  /**
   * Returns a function that sums the given values with left-fold.
   *
   * @return a function that sums the given values with left-fold.
   */
  public Function<Iterable<A>, A> sumLeft() {
    return new Function<Iterable<A>, A>() {
      public A apply(final Iterable<A> as) {
        return sumLeft(as);
      }
    };
  }
}