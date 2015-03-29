package com.atlassian.fugue.test;

import com.atlassian.fugue.Option;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class FunctionMatch {
  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
                                                      Function<? super A, ? extends Option<? extends B>> f2) {
    @SuppressWarnings("unchecked")
    Matcher<A, B> result = matcher(f1, f2);
    return result;
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
                                                      Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3) {
    @SuppressWarnings("unchecked")
    Matcher<A, B> result = matcher(f1, f2, f3);
    return result;
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @param f4 partial function, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
                                                      Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3,
                                                      Function<? super A, ? extends Option<? extends B>> f4) {
    return new Matcher<>(Arrays.<Function<? super A, ? extends Option<? extends B>>> asList(f1,
        f2, f3, f4));
  }

  /**
   * Creates a stack of matcher functions and returns the first result that
   * matches.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param f1 partial function, tried in order.
   * @param f2 partial function, tried in order.
   * @param f3 partial function, tried in order.
   * @param f4 partial function, tried in order.
   * @param f5 partial function, tried in order.
   * @param fs partial functions, tried in order.
   * @return a PartialFunction that composes all the functions and tries each
   * one in sequence.
   * @since 1.2
   */
  // TODO there has to be a better way to do this with Arrays or some such
  @SafeVarargs
  public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1,
                                                      Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3,
                                                      Function<? super A, ? extends Option<? extends B>> f4, Function<? super A, ? extends Option<? extends B>> f5,
                                                      Function<? super A, ? extends Option<? extends B>>... fs) {

    @SuppressWarnings("unchecked")
    Function<? super A, ? extends Option<? extends B>>[] matchingFunctions = new Function[5 + fs.length];
    matchingFunctions[0] = f1; matchingFunctions[1] = f2; matchingFunctions[2] = f3; matchingFunctions[3] = f4; matchingFunctions[4] = f5;
    System.arraycopy(fs, 0, matchingFunctions, 5, fs.length);

    return new Matcher<>(Arrays.asList(matchingFunctions));
  }

  /* utility copy function */
  @SafeVarargs
  private static <A, B> Matcher<A, B> matcher(Function<? super A, ? extends Option<? extends B>>... fs) {
    @SuppressWarnings("unchecked")
    Function<? super A, ? extends Option<? extends B>>[] dest = new Function[fs.length];

    System.arraycopy(fs, 0, dest, 0, fs.length);
    for(Function<? super A, ? extends Option<? extends B>> f : fs){
      if( f == null){
        throw new NullPointerException("function value was null");
      }
    }

    return new Matcher<>(Arrays.asList(dest));
  }

  static class Matcher<A, B> implements Function<A, Option<B>> {
    private final Iterable<Function<? super A, ? extends Option<? extends B>>> fs;

    Matcher(Iterable<Function<? super A, ? extends Option<? extends B>>> fs) {
      this.fs = requireNonNull(fs);

      if(isEmpty(fs)){
        throw new IllegalArgumentException("Condition must be true but returned false instead");
      }
    }

    private boolean isEmpty(Iterable<Function<? super A, ? extends Option<? extends B>>> fs){
      if (fs instanceof Collection) {
        return ((Collection<?>) fs).isEmpty();
      }
      return !fs.iterator().hasNext();
    }

    public Option<B> apply(A a) {
      for (Function<? super A, ? extends Option<? extends B>> f : fs) {
        @SuppressWarnings("unchecked")
        Option<B> b = (Option<B>) f.apply(a);
        if (b.isDefined())
          return b;
      }
      return Option.none();
    }

    @Override public String toString() {
      return "Matcher";
    }

    @Override public int hashCode() {
      return fs.hashCode();
    }
  }
}
