package com.lysdev.transperthcached.activities.train;

import android.content.Intent;

public enum Direction {
    TO, FROM;

    public static Direction from_val(int ordinal) {
        return values()[ordinal];
    }
    public static Direction from_val(String name, Intent intent) {
        return from_val(intent.getIntExtra(name, Direction.FROM.ordinal()));
    }
}
