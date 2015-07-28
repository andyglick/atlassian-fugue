package com.atlassian.fugue.optic.law;

import com.atlassian.fugue.optic.PSetter;
import com.atlassian.fugue.optic.internal.IsEq;

import java.util.function.Function;

import static com.atlassian.fugue.optic.internal.IsEq.isEq;

public final class SetterLaws<S, A> {

  private final PSetter<S, S, A, A> setter;

  public SetterLaws(PSetter<S, S, A, A> setter) {
    this.setter = setter;
  }

  /**
   * set idempotent
   */
  public IsEq<S> setIdempotent(S s, A a) {
    return isEq(setter.set(a).apply(setter.set(a).apply(s)), setter.set(a).apply(s));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(setter.modify(Function.<A>identity()).apply(s), s);
  }

}
