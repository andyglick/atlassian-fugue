package com.atlassian.fugue.optic.law;

import com.atlassian.fugue.*;
import com.atlassian.fugue.optic.PPrism;
import com.atlassian.fugue.optic.internal.IsEq;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.atlassian.fugue.optic.internal.IsEq.isEq;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class PrismLaws<S, A> {

  private final PPrism<S, S, A, A> prism;

  public PrismLaws(PPrism<S, S, A, A> prism) {
    this.prism = prism;
  }

  /**
   * if a Prism match you can always go back to the source
   */
  public IsEq<S> partialRoundTripOneWay(S s) {
    return isEq(prism.getOrModify(s).fold(Function.identity(), prism::reverseGet), s);
  }

  /**
   * reverseGet produces a value
   */
  public IsEq<Option<A>> roundTripOtherWay(A a) {
    return isEq(prism.getOption(prism.reverseGet(a)), Option.some(a));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(prism.modify(Function.<A>identity()).apply(s), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifySupplierFPoint(S s) {
    return isEq(prism.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
    return isEq(prism.<String>modifyEitherF(Eithers.toRight()).apply(s), Either.right(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Option<S>> modifyOptionFPoint(S s) {
    return isEq(prism.modifyOptionF(Options.toOption()).apply(s), Option.some(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
    return isEq(prism.modifyPairF(a -> Pair.pair(a, a)).apply(s), Pair.pair(s, s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyStreamFPoint(S s) {
    return isEq(prism.modifyStreamF(Stream::of).apply(s).collect(toList()), Collections.singletonList(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifyFunctionFPoint(S s) {
    return isEq(prism.<String>modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyIterableFPoint(S s) {
    return isEq(stream(spliteratorUnknownSize(prism.modifyIterableF(Collections::singleton).apply(s).iterator(), ORDERED), false).collect(toList()),
      Collections.singletonList(s));
  }

  /**
   * setOption only succeeds when the Prism is matching
   */
  public IsEq<Option<S>> setOption(S s, A a) {
    return isEq(prism.setOption(a).apply(s), prism.getOption(s).map(__ -> prism.set(a).apply(s)));
  }

  /**
   * modifyOption  with id is isomorphomic to isMatching
   */
  public IsEq<Option<S>> modifyOptionIdentity(S s) {
    return isEq(prism.modifyOption(Function.identity()).apply(s), prism.getOption(s).map(__ -> s));
  }
}
