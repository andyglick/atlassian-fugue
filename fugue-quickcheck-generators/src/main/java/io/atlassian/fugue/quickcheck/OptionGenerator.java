package io.atlassian.fugue.quickcheck;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.atlassian.fugue.Option;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Produces values of type {@link Option}.
 */
public class OptionGenerator extends ComponentizedGenerator<Option<?>> {

  public OptionGenerator() {
    super((Class) Option.class);
  }

  @Override public Option<?> generate(SourceOfRandomness random, GenerationStatus status) {
    double trial = random.nextDouble();
    if (trial < 0.25) {
      return Option.none();
    } else {
      Object item;
      do {
        item = componentGenerators().get(0).generate(random, status);
      } while (item == null);
      return Option.some(item);
    }
  }

  @Override public List<Option<?>> doShrink(SourceOfRandomness random, Option<?> larger) {
    return larger
      .fold(
        Collections::emptyList,
        largerItem -> Stream.concat(Stream.of(Option.none()),
          componentGenerators().get(0).shrink(random, largerItem).stream().filter(item -> item != null).map(Option::some)).collect(
          Collectors.toList()));
  }

  @Override public int numberOfNeededComponents() {
    return 1;
  }

}
