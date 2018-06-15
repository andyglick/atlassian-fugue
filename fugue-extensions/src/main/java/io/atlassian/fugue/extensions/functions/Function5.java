package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Function5<A, B, C, D, E, Z> {

  Z apply(A a, B b, C c, D d, E e);

}
