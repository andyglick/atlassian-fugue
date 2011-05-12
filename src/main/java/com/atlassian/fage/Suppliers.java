package com.atlassian.fage;

import static com.atlassian.fage.Suppliers.Nulls.nullSupplier;

import com.google.common.base.Supplier;

public class Suppliers
{
    public static <T> Supplier<T> ofInstance(final T instance)
    {
        return com.google.common.base.Suppliers.ofInstance(instance);
    }

    static Supplier<Boolean> alwaysTrue()
    {
        return SupplyTrue.INSTANCE;
    }

    static Supplier<Boolean> alwaysFalse()
    {
        return SupplyFalse.INSTANCE;
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

    static <A> Supplier<A> alwaysNull()
    {
        return nullSupplier();
    }

    enum Nulls implements Supplier<Object>
    {
        NULL;

        public Object get()
        {
            return null;
        }

        @SuppressWarnings("unchecked")
        static <A> Supplier<A> nullSupplier()
        {
            return (Supplier<A>) Nulls.NULL;
        }
    };
}
