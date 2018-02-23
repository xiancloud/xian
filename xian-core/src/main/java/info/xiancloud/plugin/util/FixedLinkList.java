package info.xiancloud.plugin.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 固定长度的队列,超长时,自动丢弃旧数据
 *
 * @author happyyangyuan
 */
public class FixedLinkList<T> extends LinkedList<T> {

    private int maxSize = 100;

    public FixedLinkList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T o) {
        if (super.add(o)) {
            if (size() == maxSize) {
                removeFirst();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new RuntimeException("不支持");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new RuntimeException("不支持");
    }
}
