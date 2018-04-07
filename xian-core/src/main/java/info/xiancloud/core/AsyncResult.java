package info.xiancloud.core;


import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Encapsulates the result of an asynchronous operation.
 * <p>
 * Many operations in Vert.x APIs provide results back by passing an instance of this in a {@link java.util.function.Consumer}.
 * <p>
 * The result can either have failed or succeeded.
 * <p>
 * If it failed then the cause of the failure is available with {@link #cause}.
 * <p>
 * If it succeeded then the actual result is available with {@link #result}
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@SuppressWarnings("unused")
public interface AsyncResult<T> {

    /**
     * The result of the operation. This will be null if the operation failed.
     *
     * @return the result or null if the operation failed.
     */
    T result();

    /**
     * A Throwable describing failure. This will be null if the operation succeeded.
     *
     * @return the cause or null if the operation succeeded.
     */
    Throwable cause();

    /**
     * Did it succeed?
     *
     * @return true if it succeded or false otherwise
     */
    boolean succeeded();

    /**
     * Did it fail?
     *
     * @return true if it failed or false otherwise
     */
    boolean failed();

    /**
     * Apply a {@code mapper} function on this async result.<p>
     * <p>
     * The {@code mapper} is called with the completed value and this mapper returns a value. This value will complete the result returned by this method call.<p>
     * <p>
     * When this async result is failed, the failure will be propagated to the returned async result and the {@code mapper} will not be called.
     *
     * @param mapper the mapper function
     * @return the mapped async result
     */
    default <U> AsyncResult<U> map(Function<T, U> mapper) {
        if (mapper == null) {
            throw new NullPointerException();
        }
        return new AsyncResult<U>() {
            @Override
            public U result() {
                if (succeeded()) {
                    return mapper.apply(AsyncResult.this.result());
                } else {
                    return null;
                }
            }

            @Override
            public Throwable cause() {
                return AsyncResult.this.cause();
            }

            @Override
            public boolean succeeded() {
                return AsyncResult.this.succeeded();
            }

            @Override
            public boolean failed() {
                return AsyncResult.this.failed();
            }
        };
    }

    /**
     * Map the result of this async result to a specific {@code value}.<p>
     * <p>
     * When this async result succeeds, this {@code value} will succeeed the async result returned by this method call.<p>
     * <p>
     * When this async result fails, the failure will be propagated to the returned async result.
     *
     * @param value the value that eventually completes the mapped async result
     * @return the mapped async result
     */
    default <V> AsyncResult<V> map(V value) {
        return map(t -> value);
    }

    /**
     * Map the result of this async result to {@code null}.<p>
     * <p>
     * This is a convenience for {@code asyncResult.map((T) null)} or {@code asyncResult.map((Void) null)}.<p>
     * <p>
     * When this async result succeeds, {@code null} will succeeed the async result returned by this method call.<p>
     * <p>
     * When this async result fails, the failure will be propagated to the returned async result.
     *
     * @return the mapped async result
     */
    @SuppressWarnings("unused")
    default <V> AsyncResult<V> mapEmpty() {
        return map((V) null);
    }

    /**
     * Apply a {@code mapper} function on this async result.<p>
     * <p>
     * The {@code mapper} is called with the failure and this mapper returns a value. This value will complete the result returned by this method call.<p>
     * <p>
     * When this async result is succeeded, the value will be propagated to the returned async result and the {@code mapper} will not be called.
     *
     * @param mapper the mapper function
     * @return the mapped async result
     */
    default AsyncResult<T> otherwise(Function<Throwable, T> mapper) {
        if (mapper == null) {
            throw new NullPointerException();
        }
        return new AsyncResult<T>() {
            @Override
            public T result() {
                if (AsyncResult.this.succeeded()) {
                    return AsyncResult.this.result();
                } else if (AsyncResult.this.failed()) {
                    return mapper.apply(AsyncResult.this.cause());
                } else {
                    return null;
                }
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return AsyncResult.this.succeeded() || AsyncResult.this.failed();
            }

            @Override
            public boolean failed() {
                return false;
            }
        };
    }

    /**
     * Map the failure of this async result to a specific {@code value}.<p>
     * <p>
     * When this async result fails, this {@code value} will succeeed the async result returned by this method call.<p>
     * <p>
     * When this async succeeds, the result will be propagated to the returned async result.
     *
     * @param value the value that eventually completes the mapped async result
     * @return the mapped async result
     */
    default AsyncResult<T> otherwise(T value) {
        return otherwise(err -> value);
    }

    /**
     * Map the failure of this async result to {@code null}.<p>
     * <p>
     * This is a convenience for {@code asyncResult.otherwise((T) null)}.<p>
     * <p>
     * When this async result fails, the {@code null} will succeeed the async result returned by this method call.<p>
     * <p>
     * When this async succeeds, the result will be propagated to the returned async result.
     *
     * @return the mapped async result
     */
    @SuppressWarnings("unused")
    default AsyncResult<T> otherwiseEmpty() {
        return otherwise(err -> null);
    }

    /**
     * create a new asyncResult instance from the unit response.
     *
     * @param unitResponse the source unit response object.
     * @param <E>          the generic type
     * @return the newly created asyncResult
     */
    @SuppressWarnings("unused")
    static <E> AsyncResult<E> fromUnitResponse(UnitResponse unitResponse) {
        return fromUnitResponse(unitResponse, null);
    }

    /**
     * @param unitResponse the source unit response object.
     * @param converter    the converter to convert the data in the unit response.
     * @param <E>          the generic type
     * @return the newly created async result.
     */
    static <E> AsyncResult<E> fromUnitResponse(UnitResponse unitResponse, Callable<E> converter) {
        return fromUnitResponse(unitResponse, converter, null);
    }

    @SuppressWarnings("unchecked")
    static <E> AsyncResult<E> fromUnitResponse(UnitResponse unitResponse, Callable<E> resultConverter, Callable<Throwable> exceptionConverter) {
        return new AsyncResult<E>() {
            @Override
            public E result() {
                if (unitResponse.succeeded())
                    if (resultConverter != null)
                        try {
                            return resultConverter.call();
                        } catch (Exception e) {
                            LOG.error(e);
                            throw new RuntimeException(e);
                        }
                    else
                        return (E) unitResponse.getData();
                return null;
            }

            @Override
            public Throwable cause() {
                if (exceptionConverter != null) {
                    try {
                        return exceptionConverter.call();
                    } catch (Exception e) {
                        LOG.error(e);
                        throw new RuntimeException(e);
                    }
                }
                return unitResponse.dataToException();
            }

            @Override
            public boolean succeeded() {
                return unitResponse.succeeded();
            }

            @Override
            public boolean failed() {
                return !unitResponse.succeeded();
            }
        };
    }

    /**
     * @param sourceResult the source result
     * @param converter    converter
     * @param <E>          the targeted type
     * @param <F>          the source type
     * @return the newly created asyncResult converted from sourceResult.
     */
    static <E, F> AsyncResult<E> from(AsyncResult<F> sourceResult, Callable<E> converter) {
        return new AsyncResult<E>() {
            @Override
            public E result() {
                try {
                    return converter.call();
                } catch (Exception e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Throwable cause() {
                return sourceResult.cause();
            }

            @Override
            public boolean succeeded() {
                return sourceResult.succeeded();
            }

            @Override
            public boolean failed() {
                return sourceResult.failed();
            }
        };
    }
}
