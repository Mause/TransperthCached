package com.lysdev.transperthcached.timetable;

import java.util.Comparator;

public class VisitComparator implements Comparator<Visit> {
    public int compare(Visit v1, Visit v2) {
        if      (v1.time.isBefore(v2.time)) return  1;
        else if (v1.time.isAfter(v2.time))  return -1;
        else                           return  0;
    }
}
