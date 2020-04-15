package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

import static io.atlassian.fugue.Functions.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryTest {

  private static final Integer STARTING_VALUE = 1;
  private static final String EXCEPTION_MESSAGE = "exception message";
  private Try<Integer> successT = Checked.now(() -> STARTING_VALUE);
  private Try<Integer> failT = Checked.now(() -> {
    throw new IOException(EXCEPTION_MESSAGE);
  });
  private Function<Integer, Try<String>> f = n -> Checked.now(n::toString);
  private Function<String, Try<String>> g = s -> Checked.now(s::toUpperCase);
  private Function<Integer, Try<Integer>> unit = (Integer x) -> Checked.now(() -> x);

  @Test public void leftIdentity() throws Throwable {
    assertThat(unit.apply(STARTING_VALUE).flatMap(f), is(f.apply(STARTING_VALUE)));
  }

  @Test public void rightIdentity() {
    assertThat(successT.flatMap(x -> unit.apply(x)), is(successT));
    assertThat(failT.flatMap(x -> unit.apply(x)), is(failT));
  }

  @Test public void associativitySuccessCase() {
    Try<String> lhs = successT.flatMap(f).flatMap(g);
    Try<String> rhs = successT.flatMap(x -> f.apply(x).flatMap(g));

    assertThat(lhs, is(rhs));
  }

  @Test public void associativityFailureCase() {
    Try<String> lhs = failT.flatMap(f).flatMap(g);
    Try<String> rhs = failT.flatMap(x -> f.apply(x).flatMap(g));

    assertThat(lhs, is(rhs));
  }

  @Test public void sequenceReturnsFirstFailure() {
    Try<String> failed1 = Checked.now(() -> {
      throw new RuntimeException("FIRST");
    });
    Try<String> failed2 = Checked.now(() -> {
      throw new RuntimeException("SECOND");
    });
    Try<String> failed3 = Checked.now(() -> {
      throw new RuntimeException("THIRD");
    });

    Try<Iterable<String>> result = Try.sequence(Arrays.asList(failed1, failed2, failed3));

    assertThat(result.isFailure(), is(true));

    final Exception e = result.fold(identity(), x -> {
      throw new NoSuchElementException();
    });
    assertThat(e, instanceOf(RuntimeException.class));
    assertThat(e.getMessage(), is("FIRST"));
  }

  @Test public void sequenceReturnsValuesFromAllSuccesses() {
    Try<Iterable<Integer>> result = Try.sequence(IntStream.range(0, 10).mapToObj(i -> Checked.now(() -> i))::iterator);

    assertThat(result.isSuccess(), is(true));
    Iterable<Integer> vals = result.fold(f -> {
      throw new NoSuchElementException();
    }, identity());
    assertThat(vals, is(IntStream.range(0, 10).boxed().collect(toList())));
  }

  @Test public void delayedSequenceEvaluatesFirstFailure() {
    AtomicInteger called = new AtomicInteger(0);
    Try<String> failed1 = Checked.delay(() -> {
      throw new RuntimeException("FIRST " + called.addAndGet(1));
    });
    Try<String> failed2 = Checked.delay(() -> {
      throw new RuntimeException("SECOND " + called.addAndGet(10));
    });
    Try<String> failed3 = Checked.delay(() -> {
      throw new RuntimeException("THIRD " + called.addAndGet(100));
    });

    Try<Iterable<String>> result = Try.sequence(Arrays.asList(failed1, failed2, failed3));

    assertThat(result.isFailure(), is(true));

    final Exception e = result.fold(identity(), x -> {
      throw new NoSuchElementException();
    });
    assertThat(e, instanceOf(RuntimeException.class));
    assertThat(e.getMessage(), is("FIRST 1"));

    assertThat(called.get(), is(1));
  }

  @Test public void delayedSequenceReturnsValuesFromAllSuccesses() {
    Try<Iterable<Integer>> result = Try.sequence(IntStream.range(0, 10).mapToObj(i -> Checked.delay(() -> i))::iterator);

    assertThat(result.isSuccess(), is(true));
    Iterable<Integer> vals = result.fold(f -> {
      throw new NoSuchElementException();
    }, identity());
    assertThat(vals, is(IntStream.range(0, 10).boxed().collect(toList())));
  }

  @Test public void sequenceReturnsValuesFromAllSuccessesWithCustomCollector() {
    Try<Set<Integer>> result = Try.sequence(IntStream.range(0, 10).map(i -> i % 4).mapToObj(i -> Checked.now(() -> i)).collect(toList()), toSet());

    assertThat(result.isSuccess(), is(true));
    Set<Integer> vals = result.fold(f -> {
      throw new NoSuchElementException();
    }, identity());
    assertThat(vals, contains(0, 1, 2, 3));
  }

  @Test public void flattenNestedSuccess() {
    Try<Try<Integer>> nested = Checked.now(() -> successT);

    Try<Integer> flattened = Try.flatten(nested);

    assertThat(flattened, is(successT));
  }

  @Test public void flattenNestedFailure() {
    Try<Try<Integer>> nested = Checked.now(() -> failT);

    Try<Integer> flattened = Try.flatten(nested);

    assertThat(flattened.isFailure(), is(true));
    final Exception e = flattened.fold(identity(), x -> {
      throw new NoSuchElementException();
    });
    assertThat(e, instanceOf(IOException.class));
    assertThat(e.getMessage(), is(EXCEPTION_MESSAGE));
  }

}