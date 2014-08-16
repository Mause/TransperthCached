package com.lysdev.transperthcached.livetimes;

import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;

public class Trip {
    private String run;
    private int uid;
    private boolean cancelled = false;
    private int num_cars = 0;
    private LocalTime schedule;
    private DateTime actual;
    private int delay;
    private String destination;
    private ArrayList<String> pattern;
    private String line;
    private String link;

    public Trip(
        String run,
        int uid,
        boolean cancelled,
        int num_cars,
        LocalTime schedule,
        DateTime actual,
        int delay,
        String destination,
        ArrayList<String> pattern,
        String line,
        String link
        ) {

        this.run = run;
        this.uid = uid;
        this.cancelled = cancelled;
        this.num_cars = num_cars;
        this.schedule = schedule;
        this.actual = actual;
        this.delay = delay;
        this.destination = destination;
        this.pattern = pattern;
        this.line = line;
        this.link = link;
    }

    public String getRun()                { return this.run; }
    public int getUID()                   { return this.uid; }
    public boolean getCancelled()         { return this.cancelled; }
    public int getNumCars()               { return this.num_cars; }
    public LocalTime getScheduledTime()   { return this.schedule; }
    public DateTime getActualTime()       { return this.actual; }
    public int getDelay()                 { return this.delay; }
    public String getDestination()        { return this.destination; }
    public ArrayList<String> getPattern() { return this.pattern; }
    public String getLine()               { return this.line; }
    public String getLink()               { return this.link; }

    public String toString() {
        return String.format(
            "Run %s on line %s with %d cars",
            getRun(),
            getLine(),
            getNumCars()
        );
    }

    public static Trip fromNode(Node node) {
        return fromElement((Element) node);
    }

    public static Trip fromElement(Element el) {
        GetWrapper enode = new GetWrapper(el);

        return new Trip(
            enode.get("Run"),                      // String run,
            Integer.parseInt(enode.get("Uid")),    // int uid,
            enode.get("Cancelled").equals("True"), // boolean cancelled,
            Integer.parseInt(enode.get("Ncar")),   // int num_cars,
            DateTimeFormat.forPattern("HH:mm:ss").parseLocalTime(
                enode.get("Schedule")
            ),   // DateTime schedule,
            DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime(
                enode.get("Actual")
            ),      // DateTime actual,
            Integer.parseInt(enode.get("Delay")),  // int delay,
            enode.get("Destination"),              // String destination,
            new ArrayList<String>(
                Arrays.asList(enode.get("Pattern").split(","))
            ),       // ArrayList<String> pattern,
            enode.get("Line"),                     // String line,
            enode.get("Link")                      // String link
        );
    }
}
