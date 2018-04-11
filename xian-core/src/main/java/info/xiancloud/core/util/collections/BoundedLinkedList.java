package info.xiancloud.core.util.collections;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionException;

/**
 * Bounded linked list. Thread-not-safe.
 *
 * @author happyyangyuan
 */
public class BoundedLinkedList<E> extends LinkedList<E> {

    private int capacity = 1000;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean add(E o) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        return super.add(o);
    }

    @Override
    public void add(int index, E element) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        return super.addAll(c);
    }

    @Override
    public void addFirst(E e) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        super.addFirst(e);
    }

    @Override
    public void addLast(E e) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        super.addLast(e);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (size() > capacity)
            throw new RejectedExecutionException("Beyond capacity: " + size());
        return super.addAll(index, c);
    }
}
