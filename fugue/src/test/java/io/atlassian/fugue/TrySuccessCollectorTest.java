package io.atlassian.fugue;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static io.atlassian.fugue.Try.failure;
import static java.util.Arrays.asList;
import static io.atlassian.fugue.Try.successful;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TrySuccessCollectorTest {

    @Test public void toTrySuccess_sequenceOfSuccessValues_returnsRightOfSequenceOfSuccessValues() {
        assertThat(Stream.of(successful(1), successful(2), successful(3)).collect(FugueCollectors.toTrySuccess()), is(successful(asList(1, 2, 3))));
    }

    @Test public void toTrySuccessCustomCollector_sequenceOfSuccessValues_returnsSuccessOfSequenceOfRightValues() {
        assertThat(Stream.of(successful(1), successful(2), successful(2)).collect(FugueCollectors.toTrySuccess(toSet())), is(successful(new HashSet<>(asList(1, 2)))));
    }

    @Test public void toTrySuccess_sequenceOfLeftAndRightValues_returnsFirstFailureValue() {
        Exception failure = new Exception();
        List<Try<Integer>> sequence = asList(successful(1), successful(2), failure(failure));
        shuffle(sequence);
        assertThat(sequence.stream().collect(FugueCollectors.toTrySuccess()), is(failure(failure)));
    }

}