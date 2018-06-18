package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Predicate4<A, B, C, D> {

  boolean test(A a, B b, C c, D d);

}
