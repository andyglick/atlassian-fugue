package io.atlassian.fugue;

import org.junit.Test;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EitherLeftBiasTest {
  private final Either<String, Integer> l = left("heyaa!");
  private final Either<String, Integer> r = right(12);

  @Test public void leftOr_valueIsALeft_returnsLeftValue() {
    assertThat(l.leftOr(Functions.constant("yo dude")), is("heyaa!"));
  }

  @Test public void leftOr_valueIsARight_returnsRightTransformerResult() {
    assertThat(r.leftOr(Functions.constant("yo dude")), is("yo dude"));
  }
}
