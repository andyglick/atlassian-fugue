package io.atlassian.fugue.hamcrest;

import io.atlassian.fugue.Try;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import static io.atlassian.fugue.Unit.Unit;
import static java.util.Objects.requireNonNull;

public final class TryMatchers {

  private TryMatchers() {
    throw new UnsupportedOperationException();
  }

  public static Matcher<Try<?>> isFailure(Matcher<? super Exception> subMatcher) {
    return new FailureMatcher(requireNonNull(subMatcher, "subMatcher"));
  }

  public static Matcher<Try<?>> isFailure() {
    return new FailureMatcher(Matchers.any(Exception.class));
  }

  public static <A> Matcher<Try<A>> isSuccessful(Matcher<? super A> subMatcher) {
    return new SuccessfulMatcher<>(requireNonNull(subMatcher, "subMatcher"));
  }

  private static class FailureMatcher extends TypeSafeMatcher<Try<?>> {
    private final Matcher<? super Exception> subMatcher;

    private FailureMatcher(Matcher<? super Exception> subMatcher) {
      this.subMatcher = subMatcher;
    }

    @Override protected boolean matchesSafely(Try<?> actual) {
      return actual.toEither().left().exists(subMatcher::matches);
    }

    @Override public void describeTo(Description description) {
      description.appendText("failure that ");
      subMatcher.describeTo(description);
    }

    @Override protected void describeMismatchSafely(Try<?> actual, Description mismatchDescription) {
      actual.fold(left -> {
        mismatchDescription.appendText("was failure that ");
        subMatcher.describeMismatch(left, mismatchDescription);
        return Unit();
      }, right -> {
        mismatchDescription.appendText("was successful");
        return Unit();
      });
    }
  }

  private static class SuccessfulMatcher<A> extends TypeSafeMatcher<Try<A>> {

    private final Matcher<? super A> subMatcher;

    private SuccessfulMatcher(Matcher<? super A> subMatcher) {
      this.subMatcher = subMatcher;
    }

    @Override protected boolean matchesSafely(Try<A> actual) {
      return actual.toEither().right().exists(subMatcher::matches);
    }

    @Override public void describeTo(Description description) {
      description.appendText("successful that ");
      subMatcher.describeTo(description);
    }

    @Override protected void describeMismatchSafely(Try<A> actual, Description mismatchDescription) {
      actual.fold(left -> {
        mismatchDescription.appendText("was failure");
        return Unit();
      }, right -> {
        mismatchDescription.appendText("was successful that ");
        subMatcher.describeMismatch(right, mismatchDescription);
        return Unit();
      });
    }
  }
}
