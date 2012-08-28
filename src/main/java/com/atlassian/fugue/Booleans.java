package com.atlassian.fugue;

import static com.atlassian.fugue.Functions.compose2;
import static com.atlassian.fugue.Functions.flip;
import static com.atlassian.fugue.Semigroups.disjunctionSemigroup;
import static com.atlassian.fugue.Semigroups.conjunctionSemigroup;
import static com.atlassian.fugue.Semigroups.exclusiveDisjunctionSemiGroup;
import static com.atlassian.fugue.Semigroups.sum;

import com.google.common.base.Function;

/**
 * Useful Functions for boolean types
 * 
 * @since 1.2
 */
final class Booleans {
  private Booleans() {
    throw new UnsupportedOperationException();
  }


  /**
   * Curried form of logical "inclusive or" (disjunction).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> or = sum(disjunctionSemigroup);

  /**
   * Curried form of logical "and" (conjunction).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> and = sum(conjunctionSemigroup);


  /**
   * Curried form of logical xor (nonequivalence).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> xor = sum(exclusiveDisjunctionSemiGroup);

  /**
   * Logical negation.
   */
  public static final Function<Boolean, Boolean> not = new Function<Boolean, Boolean>() {
    public Boolean apply(final Boolean p) {
      return !p;
    }
  };

  /**
   * Curried form of logical "only if" (material implication).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> implies = Functions.curry(new Function2<Boolean, Boolean, Boolean>() {
    public Boolean apply(final Boolean p, final Boolean q) {
      return !p || q;
    }
  });

  /**
   * Curried form of logical "if" (reverse material implication).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> if_ = flip(implies);

  /**
   * Curried form of logical "if and only if" (biconditional, equivalence).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> iff = compose2(not, xor);

  /**
   * Curried form of logical "not implies" (nonimplication).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> nimp = compose2(not, implies);

  /**
   * Curried form of logical "not if" (reverse nonimplication).
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> nif = compose2(not, if_);

  /**
   * Curried form of logical "not or".
   */
  public static final Function<Boolean, Function<Boolean, Boolean>> nor = compose2(not, or);

  /**
   * Returns true if all the elements of the given list are true.
   *
   * @param l A list to check for all the elements being true.
   * @return true if all the elements of the given list are true. False otherwise.
   */
  public static boolean and(final Iterable<Boolean> l) {
    return Monoids.conjunctionMonoid.sumLeft(l);
  }

  /**
   * Returns true if any element of the given list is true.
   *
   * @param l A list to check for any element being true.
   * @return true if any element of the given list is true. False otherwise.
   */
  public static boolean or(final Iterable<Boolean> l) {
    return Monoids.disjunctionMonoid.sumLeft(l);
  }

  /**
   * Negates the given predicate.
   *
   * @param p A predicate to negate.
   * @return The negation of the given predicate.
   */
  public static <A> Function<A, Boolean> not(final Function<A, Boolean> p) {
    return Functions.compose(not, p);
  }
}
