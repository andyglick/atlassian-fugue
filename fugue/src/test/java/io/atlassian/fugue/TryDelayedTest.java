package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.notNullValue;
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

  @Test public void orElseSuccessInstanceDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> evaluated.addAndGet(1));
    Try<Integer> b = a.orElse(Try.successful(19));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(1));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void orElseSuccessSupplierDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> evaluated.addAndGet(1));
    Try<Integer> b = a.orElse(() -> Try.successful(19));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(1));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void orElseFailureInstanceDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.incrementAndGet();
      throw new RuntimeException();
    });
    Try<Integer> b = a.orElse(Try.successful(19));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(19));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void orElseFailureSupplierDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.incrementAndGet();
      throw new RuntimeException();
    });
    Try<Integer> b = a.orElse(() -> Try.successful(19));
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(19));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void filterOrElseTrueDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.addAndGet(1);
      return 4;
    });
    Try<Integer> b = a.filterOrElse(v -> true, IllegalStateException::new);
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(4));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void filterOrElseFalseDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> evaluated.addAndGet(1));
    Try<Integer> b = a.filterOrElse(v -> false, IllegalStateException::new);
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(-1));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void filterOrElseFailureDoesNotEvaluate() {
    AtomicInteger evaluated = new AtomicInteger(0);
    Try<Integer> a = Checked.delay(() -> {
      evaluated.incrementAndGet();
      throw new RuntimeException();
    });
    Try<Integer> b = a.filterOrElse(v -> true, IllegalStateException::new);
    assertThat(evaluated.get(), is(0));
    assertThat(b.getOrElse(() -> -1), is(-1));
    assertThat(evaluated.get(), is(1));
  }

  @Test public void toOption() {
    Try<Integer> t = Checked.delay(() -> 1);
    assertThat(t.toOption(), is(Option.some(1)));
  }

  @Test public void toOptionNone() {
    Try<Integer> t = Checked.delay(() -> {
      throw new RuntimeException();
    });
    assertThat(t.toOption(), is(Option.none()));
  }

  @Test public void toEitherRight() {
    Try<Integer> t = Checked.delay(() -> 1);
    assertThat(t.toEither(), is(Either.right(1)));
  }

  @Test public void toEitherLeft() {
    RuntimeException runtimeException = new RuntimeException();
    Try<Integer> t = Checked.delay(() -> {
      throw runtimeException;
    });
    assertThat(t.toEither(), is(Either.left(runtimeException)));
  }

  @Test public void toOptional() {
    Try<Integer> t = Checked.delay(() -> 1);
    assertThat(t.toOptional(), is(Optional.of(1)));
  }

  @Test public void toOptionalEmpty() {
    Try<Integer> t = Checked.delay(() -> {
      throw new RuntimeException();
    });
    assertThat(t.toOptional(), is(Optional.empty()));
  }

  @Test public void toStream() {
    Try<Integer> t = Checked.delay(() -> 1);
    Stream<Integer> stream = t.toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), contains(1));
  }

  @Test public void toStreamEmpty() {
    Try<Integer> t = Checked.delay(() -> {
      throw new RuntimeException();
    });
    Stream<Integer> stream = t.toStream();
    assertThat(stream, notNullValue());
    assertThat(stream.collect(toList()), emptyIterable());
  }

  @Test public void forEach() {
    Try<Integer> t = Checked.delay(() -> 1);
    final AtomicInteger invoked = new AtomicInteger(0);
    t.forEach(invoked::set);

    assertThat(invoked.get(), is(1));
  }

  @Test public void forEachEmpty() {
    Try<Integer> t = Checked.delay(() -> {
      throw new RuntimeException();
    });
    final AtomicInteger invoked = new AtomicInteger(0);
    t.forEach(invoked::set);
    assertThat(invoked.get(), is(0));
  }

  @Test public void iteratorNotEmpty() {
    Try<Integer> t = Checked.delay(() -> 18);
    Iterator<Integer> iterator = t.iterator();
    assertThat(iterator.hasNext(), is(true));
    assertThat(iterator.next(), is(18));
    assertThat(iterator.hasNext(), is(false));
  }

  @Test public void iteratorEmpty() {
    Try<Integer> t = Checked.delay(() -> {
      throw new RuntimeException();
    });
    Iterator<Integer> iterator = t.iterator();
    assertThat(iterator.hasNext(), is(false));
  }

}