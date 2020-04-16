package io.atlassian.fugue;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OptionCollectorTest {

  @Test public void toOptionFlatten_sequenceOfNones_returnsEmptyList() {
    assertThat(Stream.of(none(), none(), none()).collect(FugueCollectors.flatten()), is(emptyList()));
  }

  @Test public void toOptionFlatten_sequenceOfNoneAndSomeValues_returnsCollectedSomeValues() {
    assertThat(asList(some(1), some(2), none(Integer.class)).stream().collect(FugueCollectors.flatten()), is(asList(1, 2)));
  }

  @Test public void toOptionFlattenCustomCollector_sequenceOfNoneAndSomeValues_returnsCollectedSomeValues() {
    List<Option<Integer>> sequence = asList(some(1), some(2), none(Integer.class));
    shuffle(sequence);
    assertThat(sequence.stream().collect(FugueCollectors.flatten(toSet())), is(new HashSet<>(asList(1, 2))));
  }

}
