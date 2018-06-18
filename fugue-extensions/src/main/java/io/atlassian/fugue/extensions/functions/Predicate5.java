package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Predicate5<A, B, C, D, E> {

  boolean test(A a, B b, C c, D d, E e);

}
