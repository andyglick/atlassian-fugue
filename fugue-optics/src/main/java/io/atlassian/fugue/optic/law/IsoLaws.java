package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.*;
import io.atlassian.fugue.law.IsEq;
import io.atlassian.fugue.optic.PIso;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.atlassian.fugue.law.IsEq.isEq;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class IsoLaws<S, A> {

  private final PIso<S, S, A, A> iso;

  public IsoLaws(PIso<S, S, A, A> iso) {
    this.iso = iso;
  }

  /**
   * get and reverseGet forms an Isomorphism: round trip one way
   */
  public IsEq<S> roundTripOneWay(S s) {
    return isEq(iso.reverseGet(iso.get(s)), s);
  }

  /**
   * get and reverseGet forms an Isomorphism: round trip other way
   */
  public IsEq<A> roundTripOtherWay(A a) {
    return isEq(iso.get(iso.reverseGet(a)), a);
  }

  /**
   * set is a weaker version of reverseGet
   */
  public IsEq<S> set(S s, A a) {
    return isEq(iso.set(a).apply(s), iso.reverseGet(a));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(iso.modify(Function.<A> identity()).apply(s), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifySupplierFPoint(S s) {
    return isEq(iso.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
    return isEq(iso.<String> modifyEitherF(Eithers.toRight()).apply(s), Either.right(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Option<S>> modifyOptionFPoint(S s) {
    return isEq(iso.modifyOptionF(Options.toOption()).apply(s), Option.some(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
    return isEq(iso.modifyPairF(a -> Pair.pair(a, a)).apply(s), Pair.pair(s, s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifyFunctionFPoint(S s) {
    return isEq(iso.<String> modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyIterableFPoint(S s) {
    return isEq(stream(spliteratorUnknownSize(iso.modifyIterableF(Collections::singleton).apply(s).iterator(), ORDERED), false).collect(toList()),
      Collections.singletonList(s));
  }
}
