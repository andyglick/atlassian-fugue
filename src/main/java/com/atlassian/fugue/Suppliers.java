package com.atlassian.fugue;

import com.google.common.base.Supplier;

public class Suppliers
{
    /**
     * @return a supplier that always supplies {@code instance}.
     */
    public static <T> Supplier<T> ofInstance(final T instance)
    {
        return com.google.common.base.Suppliers.ofInstance(instance);
    }

    /**
     * @return a supplier that always supplies {@code true}.
     */
    public static Supplier<Boolean> alwaysTrue()
    {
        return SupplyTrue.INSTANCE;
    }

    /**
     * @return a supplier that always supplies {@code false}.
     */
    public static Supplier<Boolean> alwaysFalse()
    {
        return SupplyFalse.INSTANCE;
    }

    /**
     * @return a supplier that always supplies {@code null}.
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
    }
}
