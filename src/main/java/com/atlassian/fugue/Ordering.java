package com.atlassian.fugue;

/**
 * The comparison of two instances of a type may have one of three orderings;
 * less than, equal or greater than.
 * 
 * @since 1.2
 */
public enum Ordering {
  /** Less than. */
  LT,

  /** Equal. */
  EQ,

  /** Greater than. */
  GT
}