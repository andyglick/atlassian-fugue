package io.atlassian.fugue.quickcheck;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.atlassian.fugue.Either;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeThat;

@RunWith(JUnitQuickcheck.class) public class EitherPropertiesTest {

  @Property public <E, A> void satisfiesFunctorIdentityLaw(Either<E, A> fa) {
    assertThat(fa.map(Function.identity()), equalTo(fa));
  }

  @Property public <E, A, B, C> void satisfiesFunctorCompositeLaw(Either<E, A> fa, Function<A, B> f, Function<B, C> g) {
    Function<A, B> f1 = assumeResultNotNull(f);
    Function<B, C> g1 = assumeResultNotNull(g);
    assertThat(fa.map(f1).map(g1), equalTo(fa.map(g1.compose(f1))));
  }

  @Property public <E, A> void satisfiesMonadRightIdentityLaw(Either<E, A> fa) {
    assertThat(fa.flatMap(Either::right), equalTo(fa));
  }

  @Property public <E, A, B> void satisfiesMonadLeftIdentityLaw(A a, Function<A, Either<E, B>> f) {
    assumeThat("Either does not support leftIdentity for null values", a, notNullValue());
    assertThat(Either.right(a).flatMap(f), equalTo(f.apply(a)));
  }

  @Property public <E, A, B, C> void satisfiesApplicativeCompositionLaw(Either<E, Function<B, C>> fbc, Either<E, Function<A, B>> fab, Either<E, A> fa) {
    Either<E, Function<B, C>> fbc1 = fbc.map(EitherPropertiesTest::assumeResultNotNull);
    Either<E, Function<A, B>> fab1 = fab.map(EitherPropertiesTest::assumeResultNotNull);
    Either<E, Function<A, C>> fac = fab1.ap(fbc1.map(bc -> bc::compose));
    assertThat(fa.ap(fab1).ap(fbc1), equalTo(fa.ap(fac)));
  }

  private static <A, B> Function<A, B> assumeResultNotNull(Function<A, B> f) {
    return a -> {
      B b = f.apply(a);
      assumeThat(b, notNullValue());
      return b;
    };
  }

}
