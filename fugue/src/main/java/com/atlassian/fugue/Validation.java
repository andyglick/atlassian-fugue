package com.atlassian.fugue;

import com.google.common.base.Function;

import javax.annotation.Nonnull;

import static com.atlassian.jira.plugin.devstatus.util.Validations.*;
import static com.atlassian.jira.plugin.devstatus.util.ValidationAlgebra.*;

/**
 * A few notes:
 * 1. I would expect this file would be split into three
 *      a) A Validation interface
 *      b) A Validations syntax interface with the static utility methods
 *      c) A ValidationN interface/namespace declaring the algebraic data-type
 * 2. I would expect the utility methods to be expanded to arities of at least 7-8.
 * 3. The ADTs should probably override equals/hashcode for structural equality
 * 4. I have not gone through the FJ/Scala/Haskell Validation classes to see if I can find any other useful methods.
 */
public abstract class Validation<EE,A> {
    @Nonnull
    public abstract <B> B fold(@Nonnull Function<EE, B> onError, @Nonnull Function<A, B> onSuccess);

    @Nonnull
    public abstract <B> Validation<EE, B> map(@Nonnull Function<A, B> f);

    @Nonnull
    public abstract <B> Validation<EE, B> ap(@Nonnull SemiGroup<EE> s, @Nonnull Validation<EE, Function<A, B>> ff);

    @Nonnull
    abstract <B> B match(@Nonnull Function<Failure<EE,A>, B> onError, @Nonnull Function<Success<EE,A>, B> onSuccess);
}
