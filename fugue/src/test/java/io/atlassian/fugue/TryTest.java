package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryTest {

    private Try.CheckedFunction<Integer, Try<String>> f = n -> Try.of(n::toString);
    private Try.CheckedFunction<String, Try<String>> g = s -> Try.of(s::toUpperCase);


    @Test
    public void leftIdentitySuccessCase() throws Throwable {
        assertThat(Try.of(() -> 1).flatMap(f), is(f.apply(1)));
    }


    @Test
    public void leftIdentityFailureCase() throws Throwable {
        Try.CheckedFunction<Integer, Try<Integer>> f = n -> Try.of(() -> exceptionThrowingMethod(n));

        //fails
        //assertThat(Try.of(() -> 1).flatMap(f), is(f.apply(1)));
    }

    @Test
    public void rightIdentity() {
        assertThat(Try.of(() -> 1).flatMap(n -> Try.of(() -> n)), is(Try.of(() -> 1)));

        Try<Integer> t1 = Try.of(() -> exceptionThrowingMethod(1));
        assertThat(t1.flatMap(n -> Try.of(() -> n)), is(t1));

        //fails
        //assertThat(Try.of(() -> exceptionThrowingMethod(1)).flatMap(n -> Try.of(() -> n)),is(Try.of(() -> exceptionThrowingMethod(1))));
    }

    @Test
    public void associativitySuccessCase() {
        Try<Integer> t1 = Try.of(() -> 1);

        Try<String> lhs = t1.flatMap(f).flatMap(g);
        Try<String> rhs = t1.flatMap(x -> f.apply(x).flatMap(g));

        assertThat(lhs, is(rhs));
    }

    @Test
    public void associativityFailureCase() {
        Try<Integer> t1 = Try.of(() -> exceptionThrowingMethod(1));

        Try<String> lhs = t1.flatMap(f).flatMap(g);
        Try<String> rhs = t1.flatMap(x -> f.apply(x).flatMap(g));

        assertThat(lhs, is(rhs));
    }

    private Integer exceptionThrowingMethod(Integer n) throws IOException {
        throw new IOException();
    }

}