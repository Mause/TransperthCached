package com.lysdev.transperthcached.livetimes;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public class NodeListIterator implements Iterator<Node>, Iterable<Node> {
    private NodeList nodeList;
    private int idx;

    public NodeListIterator(NodeList nodeList) {
        this.nodeList = nodeList;
        this.idx = 0;
    }

    public static NodeListIterator iterator(NodeList nodeList) {
        return new NodeListIterator(nodeList);
    }

    public NodeListIterator iterator() {
        return this;
    }

    public Node next() {
        return nodeList.item(idx++);
    }

    public boolean hasNext() {
        return idx != nodeList.getLength();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
