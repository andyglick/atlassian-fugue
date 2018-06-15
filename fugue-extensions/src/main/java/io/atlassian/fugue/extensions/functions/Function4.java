package io.atlassian.fugue.extensions.functions;

@FunctionalInterface public interface Function4<A, B, C, D, Z> {

  Z apply(A a, B b, C c, D d);

}
