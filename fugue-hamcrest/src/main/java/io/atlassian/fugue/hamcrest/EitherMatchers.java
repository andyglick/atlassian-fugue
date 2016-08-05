package io.atlassian.fugue.hamcrest;

import io.atlassian.fugue.Either;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static io.atlassian.fugue.Unit.Unit;
import static java.util.Objects.requireNonNull;

public final class EitherMatchers {

  private EitherMatchers() {
    throw new UnsupportedOperationException();
  }

  public static <L> Matcher<Either<L, ?>> isLeft(Matcher<? super L> subMatcher) {
    return new LeftMatcher<>(requireNonNull(subMatcher, "subMatcher"));
  }

  public static <R> Matcher<Either<?, R>> isRight(Matcher<? super R> subMatcher) {
    return new RightMatcher<>(requireNonNull(subMatcher, "subMatcher"));
  }

  private static class LeftMatcher<L> extends TypeSafeMatcher<Either<L, ?>> {

    private final Matcher<? super L> subMatcher;

    private LeftMatcher(Matcher<? super L> subMatcher) {
      this.subMatcher = subMatcher;
    }

    @Override protected boolean matchesSafely(Either<L, ?> actual) {
      return actual.left().exists(subMatcher::matches);
    }

    @Override public void describeTo(Description description) {
      description.appendText("left that ");
      subMatcher.describeTo(description);
    }

    @Override protected void describeMismatchSafely(Either<L, ?> actual, Description mismatchDescription) {
      actual.fold(left -> {
        mismatchDescription.appendText("was left that ");
        subMatcher.describeMismatch(left, mismatchDescription);
        return Unit();
      }, right -> {
        mismatchDescription.appendText("was right");
        return Unit();
      });
    }
  }

  private static class RightMatcher<R> extends TypeSafeMatcher<Either<?, R>> {

    private final Matcher<? super R> subMatcher;

    private RightMatcher(Matcher<? super R> subMatcher) {
      this.subMatcher = subMatcher;
    }

    @Override protected boolean matchesSafely(Either<?, R> actual) {
      return actual.right().exists(subMatcher::matches);
    }

    @Override public void describeTo(Description description) {
      description.appendText("right that ");
      subMatcher.describeTo(description);
    }

    @Override protected void describeMismatchSafely(Either<?, R> actual, Description mismatchDescription) {
      actual.fold(left -> {
        mismatchDescription.appendText("was left");
        return Unit();
      }, right -> {
        mismatchDescription.appendText("was right that ");
        subMatcher.describeMismatch(right, mismatchDescription);
        return Unit();
      });
    }
  }
}
