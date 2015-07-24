package com.lysdev.transperthcached.livetimes;

import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.List;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormat;

public class Trip {
    private String run;
    private int uid;
    private boolean cancelled;
    private int num_cars;
    private DateTime schedule;
    private DateTime actual;
    private int delay;
    private String destination;
    private List<String> pattern;
    private String line;
    private String link;
    private List<String> patternFullDisplay;
    private int minutesDelayTime;
    private String state; // Pred-icted, or Plat-form
    private String displayDelayTime;
    private String lineFull;
    private String platform;
    private LocalTime actualDisplayTime24;

    public Trip(
        List<String> pattern,
        List<String> patternFullDisplay,
        int minutesDelayTime,
        boolean cancelled,
        DateTime actual,
        DateTime schedule,
        int delay,
        int num_cars,
        int uid,
        String destination,
        String run,
        String state,
        String displayDelayTime,
        String line,
        String lineFull,
        String link,
        String platform,
        LocalTime actualDisplayTime24) {

        this.pattern = pattern;
        this.patternFullDisplay = patternFullDisplay;
        this.minutesDelayTime = minutesDelayTime;
        this.cancelled = cancelled;
        this.actual = actual;
        this.schedule = schedule;
        this.delay = delay;
        this.num_cars = num_cars;
        this.uid = uid;
        this.destination = destination;
        this.run = run;
        this.state = state;
        this.displayDelayTime = displayDelayTime;
        this.line = line;
        this.lineFull = lineFull;
        this.link = link;
        this.platform = platform;
        this.actualDisplayTime24 = actualDisplayTime24;
    }

    public DateTime     getActual()              { return this.actual; }
    public LocalTime    getActualDisplayTime24() { return this.actualDisplayTime24; }
    public boolean      getCancelled()           { return this.cancelled; }
    public int          getDelay()               { return this.delay; }
    public String       getDestination()         { return this.destination; }
    public String       getDisplayDelayTime()    { return this.displayDelayTime; }
    public String       getLine()                { return this.line; }
    public String       getLineFull()            { return this.lineFull; }
    public String       getLink()                { return this.link; }
    public int          getMinutesDelayTime()    { return this.minutesDelayTime; }
    public int          getNumCars()             { return this.num_cars; }
    public List<String> getPattern()             { return this.pattern; }
    public List<String> getPatternFullDisplay()  { return this.patternFullDisplay; }
    public String       getPlatform()            { return this.platform; }
    public String       getRun()                 { return this.run; }
    public DateTime     getSchedule()            { return this.schedule; }
    public String       getState()               { return this.state; }
    public int          getUid()                 { return this.uid; }

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

    public static DateTime parse_scheduled_correctly(String s) {
        DateTimeFormatter formatter = ISODateTimeFormat.hourMinuteSecond();
        LocalTime dt_schedule;
        DateTime ref_data = DateTime.now();
        try {
            dt_schedule = formatter.parseLocalTime(s);
        } catch (IllegalFieldValueException ifve) {
            String[] parts = s.split(":");
            Integer parsed = Integer.parseInt(parts[0]);
            if (parsed > 23) {
                Log.d("TransperthCached", "Well fuck");
                parts[0] = Integer.toString(parsed % 24);
            }
            dt_schedule = formatter.parseLocalTime(
                parts[0] + ":" +
                parts[1] + ":" +
                parts[2]
            );
            ref_data = ref_data.plusDays(1);
        }

        return (
            ref_data
            .withTime(
                dt_schedule.getHourOfDay(),
                dt_schedule.getMinuteOfHour(),
                dt_schedule.getSecondOfMinute(),
                0
            )
        );
    }

    public static Trip fromElement(Element el) {
        GetWrapper enode = new GetWrapper(el);
        DateTimeFormatter actualP = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        // someone inside transperth decided the best way to show that a time
        // was tomorrow, would be to simply allow the hours to go above 23.
        // we mend that here
        DateTime dt_schedule = parse_scheduled_correctly(enode.get("Schedule"));

        return new Trip(
            Arrays.asList(enode.get("Pattern").split(",")),
            Arrays.asList(enode.get("PatternFullDisplay").split(",")),
            Integer.parseInt(enode.get("MinutesDelayTime")),
            enode.get("Cancelled").equals("True"),
            actualP.parseDateTime(enode.get("Actual")),
            dt_schedule,
            Integer.parseInt(enode.get("Delay")),
            Integer.parseInt(enode.get("Ncar")),
            Integer.parseInt(enode.get("Uid")),
            enode.get("Destination"),
            enode.get("Run"),
            enode.get("State"),
            enode.get("DisplayDelayTime"),
            enode.get("Line"),
            enode.get("LineFull"),
            enode.get("Link"),
            enode.get("Platform"),
            DateTimeFormat.forPattern("HH:mm").parseLocalTime(enode.get("actualDisplayTime24"))
        );
    }
}
