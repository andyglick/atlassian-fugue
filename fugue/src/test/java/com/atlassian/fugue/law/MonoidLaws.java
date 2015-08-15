package com.atlassian.fugue.law;

import com.atlassian.fugue.Monoid;
import com.atlassian.fugue.Semigroup;

import static com.atlassian.fugue.law.IsEq.isEq;

/**
 * Laws for a monoid
 */
public interface MonoidLaws<A> extends SemigroupLaws<A> {

  Monoid<A> monoid();

  @Override default Semigroup<A> semigroup() {
    return monoid();
  }

  default IsEq<A> monoidLeftIdentity(A x) {
    return isEq(x, monoid().append(monoid().empty(), x));
  }

  default IsEq<A> monoidRightIdentity(A x) {
    return isEq(x, monoid().append(x, monoid().empty()));
  }

  static <A> MonoidLaws<A> monoidLaws(Monoid<A> monoid) {
    return () -> monoid;
  }

}
