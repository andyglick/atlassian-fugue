package com.atlassian.fugue;

import static com.atlassian.fugue.EitherRightProjectionTest.reverseToEither;
import static com.atlassian.fugue.UtilityFunctions.addOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EitherRightBiasTest {
  @Test public void mapRight() {
    Either<String, Integer> either = Either.right(3);
    assertThat(either.map(addOne), is(Either.<String, Integer> right(4)));
  }

  @Test public void mapLeft() {
    Either<String, Integer> either = Either.left("foo");
    assertThat(either.map(addOne), is(either));
  }

  @Test public void flatMapRight() {
    Either<Integer, String> either = Either.right("!foo");
    assertThat(either.flatMap(reverseToEither), is(Either.<Integer, String> right("oof!")));
  }

  @Test public void flatMapLeft() {
    Either<Integer, String> either = Either.left(5);
    assertThat(either.flatMap(reverseToEither), is(either));
  }
}
