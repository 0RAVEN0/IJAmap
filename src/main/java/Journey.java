/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: A single journey path from one end of the line to the other with all departure times
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A single journey path from one end of the line to the other with all departure times
 */
public class Journey {
    private final String id;
    private List<Stop> stops;
    private final List<LocalTime> starts = new ArrayList<>();
    private final List<Schedule> sequence;
    private final Stop originStop;

    /**
     * Constructor for a journey
     * @param id A unique ID
     * @param originStop A starting (first) stop for this journey
     * @param starts A list of departure times (in HH:MM format)
     * @param sequence A sequence of arrival and departure times bound to given stops (in the same order)
     */
    @JsonCreator
    public Journey(@JsonProperty("id") String id, @JsonProperty("originStop") Stop originStop,
                   @JsonProperty("starts") List<String> starts, @JsonProperty("sequence") List<Schedule> sequence) {
        this.id = id;
        this.originStop = originStop;
        for(String start : starts) {
            this.starts.add(LocalTime.parse(start));
        }
        this.sequence = sequence;
    }

    public String getId() {
        return id;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public List<LocalTime> getStarts() {
        return starts;
    }

    public List<Schedule> getSequence() {
        return sequence;
    }

    public Stop getOriginStop() {
        return originStop;
    }

    /**
     * Gets the first (starting) stop in the journey
     * @return First (starting) stop in the journey
     */
    public Stop firstStop() {
        return stops.get(0);
    }

    /**
     * Gets the last (destination) stop in the journey
     * @return Last (destination) stop in the journey
     */
    public Stop lastStop() {
        return stops.get(stops.size()-1);
    }

    /**
     * Checks if the amount of stops given corresponds to the amount of schedules given in sequence
     */
    public void checkSequence() {
        if (this.stops.size() != this.sequence.size()) {
            throw new IllegalArgumentException("The number of stops and schedule times does not match");
        }
    }
}
