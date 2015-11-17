package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.*;
import io.atlassian.fugue.optic.PLens;
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

public final class LensLaws<S, A> {

  private final PLens<S, S, A, A> lens;

  public LensLaws(PLens<S, S, A, A> lens) {
    this.lens = lens;
  }

  /**
   * set what you get
   */
  public IsEq<S> getSet(S s) {
    return isEq(lens.set(lens.get(s)).apply(s), s);
  }

  /**
   * get what you set
   */
  public IsEq<A> setGet(S s, A a) {
    return isEq(lens.get(lens.set(a).apply(s)), a);
  }

  /**
   * set idempotent
   */
  public IsEq<S> setIdempotent(S s, A a) {
    return isEq(lens.set(a).apply(lens.set(a).apply(s)), lens.set(a).apply(s));
  }

  /**
   * modify id = id
   */
  public IsEq<S> modifyIdentity(S s) {
    return isEq(lens.modify(Function.<A> identity()).apply(s), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifySupplierFPoint(S s) {
    return isEq(lens.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
    return isEq(lens.<String> modifyEitherF(Eithers.toRight()).apply(s), Either.right(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Option<S>> modifyOptionFPoint(S s) {
    return isEq(lens.modifyOptionF(Options.toOption()).apply(s), Option.some(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
    return isEq(lens.modifyPairF(a -> Pair.pair(a, a)).apply(s), Pair.pair(s, s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyStreamFPoint(S s) {
    return isEq(lens.modifyStreamF(Stream::of).apply(s).collect(toList()), Collections.singletonList(s));
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<S> modifyFunctionFPoint(S s) {
    return isEq(lens.<String> modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
  }

  /**
   * modifyF Applicative.point(_) = Applicative.point(_)
   */
  public IsEq<List<S>> modifyIterableFPoint(S s) {
    return isEq(stream(spliteratorUnknownSize(lens.modifyIterableF(Collections::singleton).apply(s).iterator(), ORDERED), false).collect(toList()),
      Collections.singletonList(s));
  }
}
