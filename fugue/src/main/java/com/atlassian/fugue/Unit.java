package com.atlassian.fugue;

/**
 * An alternative to {@link Void} that is actually once inhabited (whereas Void
 * is inhabited by null, which causes NPEs).
 * 
 * @since 2.2
 */
public enum Unit {
  VALUE
}
