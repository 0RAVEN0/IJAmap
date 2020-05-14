/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Representation of a line crossing one or more streets with multiple stops
 */
package main.java;


import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a line crossing one or more streets with multiple stops
 */
public class Line {
    private final String id;
    private final List<Stop> stops;
    private final List<Street> streets = new ArrayList<>();
    private final List<Journey> journeys;

    /**
     * Constructor for a line
     * @param id A unique ID
     * @param stops A list of stops that the line contains
     * @param journeys A list of journeys that the line contains
     */
    @JsonCreator
    public Line(@JsonProperty("id") String id, @JsonProperty("stops") List<Stop> stops, @JsonProperty("journeys") List<Journey> journeys) {
        this.id = id;
        if (stops.size() < 2) {
            throw new IllegalArgumentException("At least 2 stops are necessary to form a line. (Line ID: \"" + this.getId() +"\")");
        }
        this.stops = stops.stream().distinct().collect(Collectors.toList());
        this.journeys = journeys;
        for (Journey journey : this.journeys) {
            if (journey.getOriginStop() == this.stops.get(0)) {
                journey.setStops(this.stops);
            }
            else if (journey.getOriginStop() == this.stops.get(this.stops.size()-1)) {
                List<Stop> reversedStops = new ArrayList<>(this.stops);
                Collections.reverse(reversedStops);
                journey.setStops(reversedStops);
            }
            else {
                throw new IllegalArgumentException("The originStation in journey \""+journey.getId()+"\" of line \""
                +this.getId()+"\" must be either the first or the last stop of the line.");
            }
            journey.checkSequence();
        }
    }

    public String getId() {
        return id;
    }


    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    /**
     * Adds street to this line
     * @param street Street to be added
     * @return Returns true upon success, false upon failure
     */
    public boolean addStreet(Street street) {
        if (stops.isEmpty()) {
            return false;
        }
        if (streets.isEmpty()) {
            streets.add(street);
        }
        if (streets.contains(street)) {
            return true;
        }
        if (!street.follows(this.streets.get(this.streets.size()-1))) {
           return false;
        }
        streets.add(street);
        return true;
    }

    public Journey findJourneyByID(String id) {
        for (Journey journey : this.journeys) {
            if (journey.getId().equals(id)) {
                return journey;
            }
        }
        return null;
    }

    /**
     * Adds stop to this line
     * @param stop Stop to be added
     * @return Returns true upon success, false upon failure
     */
    public boolean addStop(Stop stop) {
        if (this.stops.isEmpty()) {
            stops.add(stop);
            streets.add(stop.getStreet());
            return true;
        }
        if (this.stops.contains(stop)) {
            return true;
        }
        if (!stop.getStreet().follows(this.streets.get(this.streets.size()-1))) {
            return false;
        }
        this.stops.add(stop);
        this.streets.add(stop.getStreet());
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
