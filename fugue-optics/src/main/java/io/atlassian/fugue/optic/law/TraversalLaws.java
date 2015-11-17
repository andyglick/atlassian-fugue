package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.*;
import io.atlassian.fugue.optic.PTraversal;
import io.atlassian.fugue.optic.internal.IsEq;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.atlassian.fugue.optic.internal.IsEq.isEq;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class TraversalLaws<S, A> {

  private final PTraversal<S, S, A, A> traversal;

  public TraversalLaws(PTraversal<S, S, A, A> traversal) {
    this.traversal = traversal;
  }

  /**
   * get what you set.
   */
  public IsEq<List<A>> setGetAll(S s, A a) {
    return isEq(traversal.getAll(traversal.set(a).apply(s)).collect(Collectors.toList()),
      traversal.getAll(s).map(__ -> a).collect(Collectors.toList()));
  }

  /**
   * set idempotent
   */
  public IsEq<S> setIdempotent(S s, A a) {
    return isEq(traversal.set(a).apply(traversal.set(a).apply(s)), traversal.set(a).apply(s));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(traversal.modify(Function.<A> identity()).apply(s), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifySupplierFPoint(S s) {
    return isEq(traversal.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
    return isEq(traversal.<String> modifyEitherF(Eithers.toRight()).apply(s), Either.right(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Option<S>> modifyOptionFPoint(S s) {
    return isEq(traversal.modifyOptionF(Options.toOption()).apply(s), Option.some(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
    return isEq(traversal.modifyPairF(a -> Pair.pair(a, a)).apply(s), Pair.pair(s, s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyStreamFPoint(S s) {
    return isEq(traversal.modifyStreamF(Stream::of).apply(s).collect(toList()), Collections.singletonList(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifyFunctionFPoint(S s) {
    return isEq(traversal.<String> modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyIterableFPoint(S s) {
    return isEq(
      stream(spliteratorUnknownSize(traversal.modifyIterableF(Collections::singleton).apply(s).iterator(), ORDERED), false).collect(toList()),
      Collections.singletonList(s));
  }

  /**
   * headOption returns the first element of getAll
   */
  public IsEq<Option<A>> headOption(S s) {
    return isEq(traversal.headOption(s), traversal.getAll(s).findFirst().map(Options.toOption()).orElse(Option.none()));
  }

}
