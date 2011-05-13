package com.atlassian.fage;

import com.google.common.base.Supplier;

public class Suppliers
{
    /**
     * Returns a supplier that always supplies {@code instance}.
     */
    public static <T> Supplier<T> ofInstance(final T instance)
    {
        return com.google.common.base.Suppliers.ofInstance(instance);
    }

    /**
     * Returns a supplier that always supplies {@code true}.
     */
    public static Supplier<Boolean> alwaysTrue()
    {
        return SupplyTrue.INSTANCE;
    }

    /**
     * Returns a supplier that always supplies {@code false}.
     */
    public static Supplier<Boolean> alwaysFalse()
    {
        return SupplyFalse.INSTANCE;
    }

    /**
     * Returns a supplier that always supplies {@code null}.
     */
    public static <A> Supplier<A> alwaysNull()
    {
        @SuppressWarnings("unchecked")
        final Supplier<A> result = (Supplier<A>) Nulls.NULL;
        return result;
    }

    private enum SupplyTrue implements Supplier<Boolean>
    {
        INSTANCE;

        public Boolean get()
        {
            return true;
        }
    }

    private enum SupplyFalse implements Supplier<Boolean>
    {
        INSTANCE;

        public Boolean get()
        {
            return false;
        }
    }

    enum Nulls implements Supplier<Object>
    {
        NULL;

        public Object get()
        {
            return null;
        }
    };
}
