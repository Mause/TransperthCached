package com.lysdev.transperthcached.livetimes

import org.w3c.dom.NodeList
import org.w3c.dom.Node


object NodeListExtras {
    implicit def nodeListIterator(nl : NodeList) = {
        for (i <- 0 to (nl.getLength() - 1)) yield nl.item(i)
    }
}


class NodeListIteratorActual(val nodeList: NodeList) extends java.util.Iterator[Node] {
    var idx : Int = 0
    def next : Node = {
        this.idx += 1
        this.nodeList.item(idx)
    }

    def hasNext = idx != nodeList.getLength()

    def remove() {
        throw new UnsupportedOperationException()
    }
}


class NodeListIterator(val nodeList : NodeList)
                       extends Object
                       with java.lang.Iterable[Node] {

    def iterator = new NodeListIteratorActual(this.nodeList)
}
