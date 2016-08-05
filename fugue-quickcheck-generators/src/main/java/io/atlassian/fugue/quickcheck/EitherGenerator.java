package io.atlassian.fugue.quickcheck;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.atlassian.fugue.Either;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Produces values of type {@link Either}.
 */
public class EitherGenerator extends ComponentizedGenerator<Either<?, ?>> {

  @SuppressWarnings("unchecked") public EitherGenerator() {
    super((Class) Either.class);
  }

  @Override public Either<?, ?> generate(SourceOfRandomness random, GenerationStatus status) {
    boolean trial = random.nextBoolean();
    if (trial) {
      Object left;
      do {
        left = componentGenerators().get(0).generate(random, status);
      } while (left == null);
      return Either.left(left);
    } else {
      Object right;
      do {
        right = componentGenerators().get(1).generate(random, status);
      } while (right == null);
      return Either.right(right);
    }
  }

  @Override public List<Either<?, ?>> doShrink(SourceOfRandomness random, Either<?, ?> larger) {
    return larger.fold(left -> componentGenerators().get(0).shrink(random, left).stream().filter(l -> l != null).map(Either::left),
      right -> componentGenerators().get(1).shrink(random, right).stream().filter(r -> r != null).map(Either::right)).collect(Collectors.toList());
  }

  @Override public int numberOfNeededComponents() {
    return 2;
  }

}
