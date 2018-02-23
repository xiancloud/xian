package info.xiancloud.plugin.util.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 一颗简单的树数据结构
 *
 * @author happyyangyuan
 */
public class Tree<T> {
    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
    }

    public Node<T> getRoot() {
        return root;
    }
}
