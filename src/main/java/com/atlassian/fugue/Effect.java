package com.atlassian.fugue;

/**
 * Contains the a side-effect that may be applied
 * 
 * @param <A>
 */
public interface Effect<A> {
  /**
   * Perform the side-effect.
   */
  void apply(A a);

  /**
   * A thing upon which side-effects may be applied.
   * 
   * @param <A> the type of thing to supply to the effect.
   */
  public interface Applicant<A> {
    /**
     * Perform the given side-effect for each contained element.
     */
    void foreach(Effect<A> effect);
  }
}
