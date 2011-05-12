package com.atlassian.fage;

import static com.atlassian.fage.Option.none;
import static com.atlassian.fage.Option.some;
import static com.google.common.base.Functions.compose;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;

import java.util.NoSuchElementException;

public class OptionTest
{
    @Test
    public void foldOnNoneReturnsValueFromSupplier()
    {
        assertThat(none().fold(Suppliers.ofInstance("a"), Functions.toStringFunction()), is(equalTo("a")));
    }

    @Test
    public void foldOnSomeReturnsValueAfterFunctionIsApplied()
    {
        assertThat(some(1).fold(Suppliers.ofInstance(0), increment()), is(equalTo(2)));
    }

    @Test
    public void isDefinedIsTrueForSome()
    {
        assertTrue(some("a").isDefined());
    }

    @Test
    public void isDefinedIsFalseForNone()
    {
        assertFalse(none().isDefined());
    }

    @Test
    public void getOnSomeReturnsValue()
    {
        assertThat(some(1).get(), is(equalTo(1)));
    }

    @Test(expected = NoSuchElementException.class)
    public void getOnNoneThrowsException()
    {
        none().get();
    }

    @Test
    public void getOrElseOnSomeReturnsValue()
    {
        assertThat(some(1).getOrElse(0), is(equalTo(1)));
    }

    @Test
    public void getOrElseOnNoneReturnsElseValue()
    {
        assertThat(none(Integer.class).getOrElse(0), is(equalTo(0)));
    }

    @Test
    public void getOrElseOnNoneReturnsValueFromSupplier()
    {
        assertThat(none(Integer.class).getOrElse(Suppliers.ofInstance(0)), is(equalTo(0)));
    }

    @Test
    public void iteratorOverSomeContainsOnlyValue()
    {
        assertThat(some(1), contains(1));
    }

    @Test
    public void noneIsEmptyIterable()
    {
        assertThat(none(), is(emptyIterable()));
    }

    @Test
    public void mapAppliesFunctionToSomeValue()
    {
        assertThat(some(1).map(increment()), is(equalTo(some(2))));
    }

    @Test
    public void mapOverNoneDoesNothing()
    {
        assertThat(none(Integer.class).map(increment()), is(equalTo(none(Integer.class))));
    }

    @Test
    public void flatMapAppliesFunctionToSomeValue()
    {
        assertThat(some(1).flatMap(liftedIncrement()), is(equalTo(some(2))));
    }

    @Test
    public void flatMapOverNoneDoesNothing()
    {
        assertThat(none(Integer.class).flatMap(liftedIncrement()), is(equalTo(none(Integer.class))));
    }

    @Test
    public void equalSomesAreEqual()
    {
        assertTrue(some(2).equals(some(2)));
    }

    @Test
    public void nonEqualSomesAreNotEqual()
    {
        assertFalse(some(1).equals(some(2)));
    }

    @Test
    public void hashCodesFromEqualSomesAreEqual()
    {
        assertTrue(some(1).hashCode() == some(1).hashCode());
    }

    //
    // scaffolding
    //

    private Function<Integer, Option<Integer>> liftedIncrement()
    {
        return compose(Functions.<Integer> option(), increment());
    }

    private Function<Integer, Integer> increment()
    {
        return new Function<Integer, Integer>()
        {
            public Integer apply(final Integer i)
            {
                return i + 1;
            }
        };
    }
}
