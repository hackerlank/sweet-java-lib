package org.zt.sweet.utils;


import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * <p>
 *   Used to implement callback based result passing of a promised computation.
 *   Can be converted to a future using the future() method.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Promise<T> extends PromiseCallback<T> {

    ArrayList<PromiseCallback<T>> callbacks = new ArrayList<PromiseCallback<T>>(1);
    T value;
    Throwable error;
    Future<T> future=null;

    private class PromiseFuture extends PromiseCallback<T> implements Future<T> {
        CountDownLatch latch = new CountDownLatch(1);

        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return latch.getCount() == 0;
        }

        public T get() throws InterruptedException, ExecutionException {
            latch.await();
            return value();
        }

        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (latch.await(timeout, unit)) {
                return value();
            } else {
                throw new TimeoutException();
            }
        }

        public void onComplete(T value, Throwable error) {
            latch.countDown();
        }

        private T value() throws ExecutionException {
            if( error!=null ) {
                throw new ExecutionException(error);
            }
            return value;
        }
    }

    public Future<T> future() {
        if( future == null ) {
            PromiseFuture future = new PromiseFuture();
            watch(future);
            this.future = future;
        }
        return future;
    }

    public void watch(PromiseCallback<T> callback) {
        if (callback == null)
            throw new IllegalArgumentException("callback cannot be null");
        boolean queued = false;
        synchronized (this) {
            if (callbacks != null) {
                callbacks.add(callback);
                queued = true;
            }
        }
        if (!queued) {
            callback.onComplete(value, error);
        }
    }

    @Override
    public void onComplete(T value, Throwable error) {
        if( value!=null && error !=null ) {
            throw new IllegalArgumentException("You can not have both a vaule and error");
        }
        ArrayList<PromiseCallback<T>> callbacks;
        synchronized (this) {
            callbacks = this.callbacks;
            if (callbacks != null) {
                this.value = value;
                this.error = error;
                this.callbacks = null;
            }
        }
        if (callbacks != null) {
            for (PromiseCallback<T> callback : callbacks) {
                callback.onComplete(this.value, this.error);
            }
        }
    }

}
