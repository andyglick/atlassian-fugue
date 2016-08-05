package io.atlassian.fugue.hamcrest;

import io.atlassian.fugue.Option;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static io.atlassian.fugue.Unit.Unit;
import static java.util.Objects.requireNonNull;

public final class OptionMatchers {

  private OptionMatchers() {
    throw new UnsupportedOperationException();
  }

  public static <L> Matcher<Option<?>> isNone() {
    return NoneMatcher.INSTANCE;
  }

  public static <T> Matcher<Option<T>> isSome(Matcher<? super T> subMatcher) {
    return new SomeMatcher<>(requireNonNull(subMatcher, "subMatcher"));
  }

  private static class NoneMatcher extends TypeSafeMatcher<Option<?>> {

    private static final Matcher<Option<?>> INSTANCE = new NoneMatcher();

    private NoneMatcher() {}

    @Override protected boolean matchesSafely(Option<?> actual) {
      return actual.isEmpty();
    }

    @Override public void describeTo(Description description) {
      description.appendText("none");
    }

    @Override protected void describeMismatchSafely(Option<?> actual, Description mismatchDescription) {
      actual.forEach(item -> mismatchDescription.appendText("was some"));
    }
  }

  private static class SomeMatcher<T> extends TypeSafeMatcher<Option<T>> {

    private final Matcher<? super T> subMatcher;

    private SomeMatcher(Matcher<? super T> subMatcher) {
      this.subMatcher = subMatcher;
    }

    @Override protected boolean matchesSafely(Option<T> actual) {
      return actual.exists(subMatcher::matches);
    }

    @Override public void describeTo(Description description) {
      description.appendText("some that ");
      subMatcher.describeTo(description);
    }

    @Override protected void describeMismatchSafely(Option<T> actual, Description mismatchDescription) {
      actual.fold(() -> {
        mismatchDescription.appendText("was none");
        return Unit();
      }, item -> {
        mismatchDescription.appendText("was some that ");
        subMatcher.describeMismatch(item, mismatchDescription);
        return Unit();
      });
    }
  }

}
