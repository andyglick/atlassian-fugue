package com.atlassian.fugue.mango;

/**
 * Created by anund on 2/14/15.
 */
public class Preconditions {
  private Preconditions() {}

  public static <A> A checkNotNull(A a, String message) {
    if (a == null) {
      throw new NullPointerException(message);
    }
    return a;
  }

  public static <A> A checkNotNull(A a) {
    return Preconditions.checkNotNull(a, a.getClass().getName() + " must not be null");
  }

  public static void checkArgument(boolean check, String message) {
    if (!check) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkArgument(boolean check) {
    checkArgument(check, "Condition must be true but returned false instead");
  }

  public static void checkState(boolean check, String message) {
    if (!check) {
      throw new IllegalStateException(message);
    }
  }

  public static void checkState(boolean check) {
    checkState(check, "State check must not be false");
  }
}
