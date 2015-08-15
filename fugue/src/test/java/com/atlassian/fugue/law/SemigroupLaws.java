package com.atlassian.fugue.law;

import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a semigroup
 */
public interface SemigroupLaws<A> {

  Semigroup<A> semigroup();

  default IsEq<A> semigroupAssociative(A x, A y, A z) {
    return isEq(semigroup().append(semigroup().append(x, y), z), semigroup().append(x, semigroup().append(y, z)));
  }

  static <A> SemigroupLaws<A> semigroupLaws(Semigroup<A> semigroup) {
    return () -> semigroup;
  }

}
