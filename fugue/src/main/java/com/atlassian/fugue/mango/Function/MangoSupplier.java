package com.atlassian.fugue.mango.Function;

import java.util.function.Supplier;

/**
 * Created by alex's magic intellij wizard around 1788 A.D.
 * <p/>
 * Perhaps this was a shim of a shim... I don't think we need it :)
 */
public abstract class MangoSupplier<A> implements Supplier<A>
{
    public A apply(Object v) {
        return get();
    }

    abstract public A get();
}
