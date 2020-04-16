package io.atlassian.fugue;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EitherRightCollectorTest {

  @Test public void toEitherRight_sequenceOfRightValues_returnsRightOfSequenceOfRightValues() {
    assertThat(Stream.of(right(1), right(2), right(3)).collect(FugueCollectors.toEitherRight()), is(right(asList(1, 2, 3))));
  }

  @Test public void toEitherRightCustomCollector_sequenceOfRightValues_returnsRightOfSequenceOfRightValues() {
    assertThat(Stream.of(right(1), right(2), right(2)).collect(FugueCollectors.toEitherRight(toSet())), is(right(new HashSet<>(asList(1, 2)))));
  }

  @Test public void toEitherRight_sequenceOfLeftAndRightValues_returnsFirstLeftValue() {
    List<Either<Integer, Integer>> sequence = asList(right(1), right(2), left(3));
    shuffle(sequence);
    assertThat(sequence.stream().collect(FugueCollectors.toEitherRight()), is(left(3)));
  }

}
