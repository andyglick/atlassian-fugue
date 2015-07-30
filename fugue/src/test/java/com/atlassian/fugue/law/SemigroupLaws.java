package com.atlassian.fugue.law;

import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a semigroup
 */
public interface SemigroupLaws<A> {

  Semigroup<A> semigroup();

  default IsEq<A> semigroupAssociative(A x,A y, A z) {
    return isEq(semigroup().sum(semigroup().sum(x, y), z), semigroup().sum(x, semigroup().sum(y, z)));
  }

}
