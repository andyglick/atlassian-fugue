package io.atlassian.fugue.quickcheck;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.atlassian.fugue.Option;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

@RunWith(JUnitQuickcheck.class) public class OptionPropertiesTest {

  @Property public <A> void satisfiesFunctorIdentityLaw(Option<A> fa) {
    assertThat(fa.map(Function.identity()), equalTo(fa));
  }

  @Property public <A, B, C> void satisfiesFunctorCompositeLaw(Option<A> fa, Function<A, B> f, Function<B, C> g) {
    assertThat(fa.map(f).map(g), equalTo(fa.map(g.compose(f))));
  }

  @Property public <A> void satisfiesMonadRightIdentityLaw(Option<A> fa) {
    assertThat(fa.flatMap(Option::some), equalTo(fa));
  }

  @Property public <A, B> void satisfiesMonadLeftIdentityLaw(A a, Function<A, Option<B>> f) {
    assumeThat("Option does not support leftIdentity for null values", a, notNullValue());
    assertThat(Option.some(a).flatMap(f), equalTo(f.apply(a)));
  }

  @Property public <A, B, C> void satisfiesMonadAssociativeLaw(Option<A> fa, Function<A, Option<B>> f, Function<B, Option<C>> g) {
    assertThat(fa.flatMap(f).flatMap(g), equalTo(fa.flatMap(a -> f.apply(a).flatMap(g))));
  }

}