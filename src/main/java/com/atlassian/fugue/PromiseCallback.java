package com.atlassian.fugue;

import com.google.common.annotations.Beta;

/**
 * Callback to handle values of successful or failed {@link Promise promises}
 *
 * @param <V> the promised type through the {@link Promise}
 * @see Promise
 * @since 1.2
 */
@Beta
public interface PromiseCallback<V> {
    void handle(V value);
}
