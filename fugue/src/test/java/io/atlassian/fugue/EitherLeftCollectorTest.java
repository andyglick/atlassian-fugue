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

public class EitherLeftCollectorTest {

  @Test public void toEitherLeft_sequenceOfLeftValues_returnsLeftOfSequenceOfLeftValues() {
    assertThat(Stream.of(left(1), left(2), left(3)).collect(FugueCollectors.toEitherLeft()), is(left(asList(1, 2, 3))));
  }

  @Test public void toEitherLeftCustomCollector_sequenceOfLeftValues_returnsLeftOfSequenceOfLeftValues() {
    assertThat(Stream.of(left(1), left(2), left(2)).collect(FugueCollectors.toEitherLeft(toSet())), is(left(new HashSet<>(asList(1, 2)))));
  }

  @Test public void toEitherLeft_sequenceOfLeftAndRightValues_returnsFirstRightValue() {
    List<Either<Integer, Integer>> sequence = asList(left(1), left(2), right(3));
    shuffle(sequence);
    assertThat(sequence.stream().collect(FugueCollectors.toEitherLeft()), is(right(3)));
  }

}
