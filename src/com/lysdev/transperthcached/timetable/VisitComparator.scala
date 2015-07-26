package com.lysdev.transperthcached.timetable

import java.util.Comparator

class VisitComparator extends Comparator[Visit] {
    def compare(v1: Visit, v2: Visit) : Int = {
        if      (v1.getTime.isBefore(v2.getTime)) -1
        else if (v1.getTime.isAfter(v2.getTime))   1
        else                                       0
    }
}
