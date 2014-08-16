package com.lysdev.transperthcached.livetimes;

import org.w3c.dom.Element;


public class GetWrapper {
    private Element enode;

    public GetWrapper(Element enode) { this.enode = enode; }

    public String get(String tag) {
        return enode.getElementsByTagName(tag).item(0).getTextContent();
    }
}
