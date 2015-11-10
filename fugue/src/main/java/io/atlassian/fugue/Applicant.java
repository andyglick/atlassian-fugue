package io.atlassian.fugue;

import java.util.function.Consumer;

/**
 * A thing upon which side-effects may be applied.
 *
 * @param <A> the type of thing to supply to the effect.
 */
@FunctionalInterface public interface Applicant<A> {
  /**
   * Perform the given {@link java.util.function.Consumer} (side-effect) for
   * each contained element.
   *
   * @param consumer the {@link java.util.function.Consumer} to apply on each
   * element
   */
  void forEach(Consumer<? super A> consumer);
}
