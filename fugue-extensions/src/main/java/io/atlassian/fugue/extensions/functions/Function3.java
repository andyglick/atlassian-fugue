package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Function3<A, B, C, Z> {

  Z apply(A a, B b, C c);

}
