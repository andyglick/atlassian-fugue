package com.atlassian.fugue;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.SettableFuture;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PromisesTest {

    @Mock private PromiseCallback<Object> doneCallback;
    @Mock private PromiseCallback<Throwable> failCallback;
    @Mock private FutureCallback<Object> futureCallback;

    @Test public void testPromiseOfInstance() {
        final Object instance = new Object();
        final Promise<Object> promise = Promises.ofInstance(instance);

        assertThat(promise.isDone(), is(true));
        assertThat(promise.isCancelled(), is(false));
        assertThat(promise.claim(), is(instance));

        promise.done(doneCallback);
        verify(doneCallback).handle(instance);

        promise.fail(failCallback);
        verifyZeroInteractions(failCallback);

        promise.then(futureCallback);
        verify(futureCallback).onSuccess(instance);
        verifyNoMoreInteractions(futureCallback);
    }

    @Test public void testPromiseRejected() {
        final Throwable instance = new Throwable();
        final Promise<Object> promise = Promises.rejected(instance, Object.class);

        assertThat(promise.isDone(), is(true));
        assertThat(promise.isCancelled(), is(false));
        try {
            promise.claim();
        } catch (RuntimeException e) {
            assertSame(instance, e.getCause());
        }

        promise.done(doneCallback);
        verifyZeroInteractions(doneCallback);

        promise.fail(failCallback);
        verify(failCallback).handle(instance);

        promise.then(futureCallback);
        verify(futureCallback).onFailure(instance);
        verifyNoMoreInteractions(futureCallback);
    }

    @Test public void testPromiseOfListenableFutureSettingValue() {

        final SettableFuture<Object> future = SettableFuture.create();
        final Promise<Object> promise = Promises.forListenableFuture(future);

        // register call backs
        promise.done(doneCallback);
        promise.fail(failCallback);
        promise.then(futureCallback);

        assertThat(promise.isDone(), is(false));
        assertThat(promise.isCancelled(), is(false));

        final Object instance = new Object();
        future.set(instance);

        assertThat(promise.isDone(), is(true));
        assertThat(promise.isCancelled(), is(false));
        assertThat(promise.claim(), is(instance));

        verify(doneCallback).handle(instance);
        verifyZeroInteractions(failCallback);

        verify(futureCallback).onSuccess(instance);
        verifyNoMoreInteractions(futureCallback);
    }

    @Test public void testPromiseOfListenableFutureSettingException() {

        final SettableFuture<Object> future = SettableFuture.create();
        final Promise<Object> promise = Promises.forListenableFuture(future);

        // register call backs
        promise.done(doneCallback);
        promise.fail(failCallback);
        promise.then(futureCallback);

        assertThat(promise.isDone(), is(false));
        assertThat(promise.isCancelled(), is(false));

        final Throwable instance = new Throwable();
        future.setException(instance);

        assertThat(promise.isDone(), is(true));
        assertThat(promise.isCancelled(), is(false));
        try {
            promise.claim();
        } catch (RuntimeException t) {
            assertSame(instance, t.getCause());
        }

        verifyZeroInteractions(doneCallback);
        verify(failCallback).handle(instance);

        verify(futureCallback).onFailure(instance);
        verifyNoMoreInteractions(futureCallback);
    }

    @Test public void testTransformPromiseSettingValue() {
        final SettableFuture<Object> future = SettableFuture.create();

        final Promise<Object> originalPromise = Promises.forListenableFuture(future);
        final Promise<SomeObject> transformedPromise = Promises.transform(originalPromise, new Function<Object, SomeObject>() {
            @Override public SomeObject apply(Object input) {
                return new SomeObject(input);
            }
        });

        assertThat(originalPromise.isDone(), is(false));
        assertThat(originalPromise.isCancelled(), is(false));

        assertThat(transformedPromise.isDone(), is(false));
        assertThat(transformedPromise.isCancelled(), is(false));

        final Object instance = new Object();
        future.set(instance);
        assertThat(originalPromise.isDone(), is(true));
        assertThat(originalPromise.isCancelled(), is(false));

        assertThat(originalPromise.claim(), is(instance));

        assertThat(transformedPromise.isDone(), is(true));
        assertThat(transformedPromise.isCancelled(), is(false));

        final PromiseCallback<SomeObject> someObjectCallback = mock(PromiseCallback.class);
        transformedPromise.done(someObjectCallback);
        transformedPromise.fail(failCallback);

        assertThat(transformedPromise.claim().object, is(instance));
        verify(someObjectCallback).handle(argThat(new SomeObjectMatcher(instance)));
        verifyZeroInteractions(failCallback);
    }

    @Test public void testTransformPromiseSettingException() {
        final SettableFuture<Object> future = SettableFuture.create();

        final Promise<Object> originalPromise = Promises.forListenableFuture(future);
        final Promise<SomeObject> transformedPromise = Promises.transform(originalPromise, new Function<Object, SomeObject>() {
            @Override public SomeObject apply(Object input) {
                return new SomeObject(input);
            }
        });

        assertThat(originalPromise.isDone(), is(false));
        assertThat(originalPromise.isCancelled(), is(false));

        assertThat(transformedPromise.isDone(), is(false));
        assertThat(transformedPromise.isCancelled(), is(false));

        final Throwable instance = new Throwable();
        future.setException(instance);
        assertThat(originalPromise.isDone(), is(true));
        assertThat(originalPromise.isCancelled(), is(false));

        try {
            originalPromise.claim();
        } catch (RuntimeException e) {
            assertSame(instance, e.getCause());
        }

        assertThat(transformedPromise.isDone(), is(true));
        assertThat(transformedPromise.isCancelled(), is(false));

        final PromiseCallback<SomeObject> someObjectCallback = mock(PromiseCallback.class);
        transformedPromise.done(someObjectCallback);
        transformedPromise.fail(failCallback);

        try {
            transformedPromise.claim();
        } catch (RuntimeException e) {
            assertSame(instance, e.getCause());
        }
        verifyZeroInteractions(someObjectCallback);
        verify(failCallback).handle(instance);
    }

    @Test public void testWhenPromiseSettingValue() {

        final SettableFuture<Object> future1 = SettableFuture.create();
        final SettableFuture<Object> future2 = SettableFuture.create();

        final Promise<Object> promise1 = Promises.forListenableFuture(future1);
        final Promise<Object> promise2 = Promises.forListenableFuture(future2);

        final Promise<List<Object>> whenPromise = Promises.when(promise1, promise2);
        final PromiseCallback<List<Object>> doneCallback = mock(PromiseCallback.class);
        whenPromise.done(doneCallback);
        whenPromise.fail(failCallback);

        assertThat(promise1.isDone(), is(false));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(false));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(false));
        assertThat(whenPromise.isCancelled(), is(false));

        final Object instance1 = new Object();
        future1.set(instance1);

        assertThat(promise1.isDone(), is(true));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(false));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(false));
        assertThat(whenPromise.isCancelled(), is(false));

        verifyZeroInteractions(doneCallback);
        verifyZeroInteractions(failCallback);

        final Object instance2 = new Object();
        future2.set(instance2);

        assertThat(promise1.isDone(), is(true));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(true));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(true));
        assertThat(whenPromise.isCancelled(), is(false));

        assertThat(whenPromise.claim(), contains(instance1, instance2));
        verify(doneCallback).handle(Lists.newArrayList(instance1, instance2));
        verifyZeroInteractions(failCallback);
    }

    @Test public void testWhenPromiseSettingException() {

        final SettableFuture<Object> future1 = SettableFuture.create();
        final SettableFuture<Object> future2 = SettableFuture.create();

        final Promise<Object> promise1 = Promises.forListenableFuture(future1);
        final Promise<Object> promise2 = Promises.forListenableFuture(future2);

        final Promise<List<Object>> whenPromise = Promises.when(promise1, promise2);
        final PromiseCallback<List<Object>> doneCallback = mock(PromiseCallback.class);
        whenPromise.done(doneCallback);
        whenPromise.fail(failCallback);

        assertThat(promise1.isDone(), is(false));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(false));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(false));
        assertThat(whenPromise.isCancelled(), is(false));

        final Throwable throwable = new Throwable();
        future1.setException(throwable);

        assertThat(promise1.isDone(), is(true));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(false));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(true));
        assertThat(whenPromise.isCancelled(), is(false));

        verifyZeroInteractions(doneCallback);
        verify(failCallback).handle(throwable);

        final Throwable instance2 = new Throwable();
        future2.setException(instance2);

        assertThat(promise1.isDone(), is(true));
        assertThat(promise1.isCancelled(), is(false));

        assertThat(promise2.isDone(), is(true));
        assertThat(promise2.isCancelled(), is(false));

        assertThat(whenPromise.isDone(), is(true));
        assertThat(whenPromise.isCancelled(), is(false));

        verifyZeroInteractions(doneCallback);
        verifyNoMoreInteractions(failCallback);

        try {
            whenPromise.claim();
        } catch (RuntimeException e) {
            assertSame(throwable, e.getCause());
        }
    }

    private static class SomeObject {
        public final Object object;

        private SomeObject(Object object) {
            this.object = object;
        }
    }

    private static final class SomeObjectMatcher extends BaseMatcher<SomeObject>
    {
        private final Object instance;

        public SomeObjectMatcher(Object instance)
        {
            this.instance = instance;
        }

        @Override
        public boolean matches(Object item)
        {
            return ((SomeObject) item).object.equals(instance);
        }

        @Override
        public void describeTo(Description description)
        {
            description.appendText("SomeObject matcher");
        }
    }
}
