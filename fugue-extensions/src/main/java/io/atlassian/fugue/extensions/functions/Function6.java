package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Function6<A, B, C, D, E, F, Z> {

  Z apply(A a, B b, C c, D d, E e, F f);

}
