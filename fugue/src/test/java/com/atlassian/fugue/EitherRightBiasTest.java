package com.atlassian.fugue;

import static com.atlassian.fugue.EitherRightProjectionTest.reverseToEither;
import static com.atlassian.fugue.UtilityFunctions.addOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EitherRightBiasTest {
  @Test public void mapRight() {
    assertThat(Either.<String, Integer> right(3).map(addOne), is(Either.<String, Integer> right(4)));
  }

  @Test public void mapLeft() {
    assertThat(Either.<String, Integer> left("foo").map(addOne), is(Either.<String, Integer> left("foo")));
  }

  @Test public void flatMapRight() {
    assertThat(Either.<Integer, String> right("!foo").flatMap(reverseToEither), is(Either.<Integer, String> right("oof!")));
  }

  @Test public void flatMapLeft() {
    assertThat(Either.<Integer, String> left(5).flatMap(reverseToEither), is(Either.<Integer, String> left(5)));
  }

  @Test public void leftMapRight() {
    assertThat(Either.<Integer, String> right("foo").leftMap(addOne), is(Either.<Integer, String> right("foo")));
  }

  @Test public void leftMapLeft() {
    assertThat(Either.<Integer, String> left(3).leftMap(addOne), is(Either.<Integer, String> left(4)));
  }
}
