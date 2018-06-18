package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Predicate3<A, B, C> {

  boolean test(A a, B b, C c);

}
