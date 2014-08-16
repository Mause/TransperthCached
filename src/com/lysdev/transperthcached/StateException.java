package com.lysdev.transperthcached;

import java.lang.Exception;

public class StateException extends Exception {
    private String title;

    public StateException(String title, String msg) {
        super(msg);
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
