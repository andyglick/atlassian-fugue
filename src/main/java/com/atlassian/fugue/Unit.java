package com.atlassian.fugue;

/** 
 * The Unit value
 * 
 * @since 1.2
 */
public final class Unit {
  private static final Unit INSTANCE = new Unit();

  private Unit() {}

  /**
   * The only value of the unit type.
   *
   * @return The only value of the unit type.
   */
  public static Unit unit() {
    return INSTANCE;
  }
}
