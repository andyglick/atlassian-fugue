package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.law.IsEq;
import io.atlassian.fugue.optic.POptional;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.atlassian.fugue.law.IsEq.isEq;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class OptionalLaws<S, A> {

  private final POptional<S, S, A, A> optional;

  public OptionalLaws(POptional<S, S, A, A> optional) {
    this.optional = optional;
  }

  /**
   * set what you get
   */
  public IsEq<S> getOptionSet(S s) {
    return isEq(optional.getOrModify(s).fold(Function.identity(), a -> optional.set(a).apply(s)), s);
  }

  /**
   * get what you set
   */
  public IsEq<Option<A>> setGetOption(S s, A a) {
    return isEq(optional.getOption(optional.set(a).apply(s)), optional.getOption(s).map(__ -> a));
  }

  /**
   * set idempotent
   */
  public IsEq<S> setIdempotent(S s, A a) {
    return isEq(optional.set(a).apply(optional.set(a).apply(s)), optional.set(a).apply(s));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(optional.modify(Function.<A> identity()).apply(s), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifySupplierFPoint(S s) {
    return isEq(optional.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
    return isEq(optional.<String> modifyEitherF(Eithers.toRight()).apply(s), Either.right(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Option<S>> modifyOptionFPoint(S s) {
    return isEq(optional.modifyOptionF(Options.toOption()).apply(s), Option.some(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
    return isEq(optional.modifyPairF(a -> Pair.pair(a, a)).apply(s), Pair.pair(s, s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifyFunctionFPoint(S s) {
    return isEq(optional.<String> modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyIterableFPoint(S s) {
    return isEq(stream(spliteratorUnknownSize(optional.modifyIterableF(Collections::singleton).apply(s).iterator(), ORDERED), false)
      .collect(toList()), Collections.singletonList(s));
  }

  /**
   * setOption only succeeds when the Optional is matching
   */
  public IsEq<Option<S>> setOption(S s, A a) {
    return isEq(optional.setOption(a).apply(s), optional.getOption(s).map(__ -> optional.set(a).apply(s)));
  }

  /**
   * modifyOption with id is isomorphomic to isMatching
   */
  public IsEq<Option<S>> modifyOptionIdentity(S s) {
    return isEq(optional.modifyOption(Function.identity()).apply(s), optional.getOption(s).map(__ -> s));
  }
}
