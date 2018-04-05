package info.xiancloud.core;

/**
 * A functional interface (callback) that accepts a single value.
 *
 * @param <T> the value type
 * @author happyyangyuan
 */
public interface Handler<T> {
    /**
     * Consume the given value.
     *
     * @param t the value
     */
    void handle(T t);
}
