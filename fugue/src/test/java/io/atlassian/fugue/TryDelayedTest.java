package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryDelayedTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test public void success() {
    Try<Integer> t = Checked.delay(() -> 1);
    assertThat(t.isSuccess(), is(true));
    assertThat(t.isFailure(), is(false));
  }

  @Test public void failure() {
    Try<Integer> t = Checked.delay(() -> {
      throw new Exception("ex");
    });
    assertThat(t.isSuccess(), is(false));
    assertThat(t.isFailure(), is(true));
  }

  @Test public void onlyEvaluatedOnce() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> t = Checked.delay(() -> evaluated.addAndGet(1));
    t.isSuccess(); // evaluated at the first call
    t.isFailure(); // evaluation result is already cached.
    assertThat(evaluated.get(), is(1));
  }

  @Test public void flatMapDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> evaluated.addAndGet(1));
    Try<Integer> b = a.flatMap(i -> Checked.now(() -> i * 10));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(10));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void mapDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> evaluated.addAndGet(1));
    Try<Integer> b = a.map(i -> i * 10);
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(10));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void recoverDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.addAndGet(1);
      throw new Exception();
    });
    Try<Integer> b = a.recover(e -> 0);
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(0));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void recoverWithDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.addAndGet(1);
      throw new Exception();
    });
    Try<Integer> b = a.recoverWith(e -> Try.successful(0));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(0));
    assertThat(evaluated.get(), is(1));
  }
}