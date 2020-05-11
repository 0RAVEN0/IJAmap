/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Class representing street with adittional functions
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Street {
    private String id;
    private List<Coordinate> coordinates;
    private List<Stop> stops = new ArrayList<>();

    /**
     * Constructor for the street
     * @param id ID of the street
     * @param coordinates List of coordinates of the street
     */
    @JsonCreator
    public Street(@JsonProperty("id") String id,@JsonProperty("coordinates") List<Coordinate> coordinates){
        this.id = id;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<Stop> getStops() {
        return stops;
    }

    /**
     * Adds stop to the street
     * @param stop Stop to be added
     * @return Returns true upon success, false upon failure
     */
    public boolean addStop(Stop stop) {
        if (this.stops.contains(stop)) {
            return true;
        }
        if (this.isWithinStreet(stop)) {
            stops.add(stop);
            stop.setStreet(this);
            return true;
        }

        return false;
    }

    /**
     * Gets the beginning coordinate of the street
     * @return Beginning coordinate
     */
    public Coordinate begin() {
        return this.coordinates.get(0);
    }

    /**
     * Gets the ending coordinate of the street
     * @return Ending coordinate
     */
    public Coordinate end() {
        return this.coordinates.get((this.coordinates.size())-1);
    }

    /**
     * Checks if the given street follows this street
     * @param s Street to be checked
     * @return Returns true if the given street follows this street, false if not
     */
    public boolean follows(Street s) {
        return ( this.begin().equals(s.begin()) ||
                this.begin().equals(s.end()) ||
                this.end().equals(s.begin()) ||
                this.end().equals(s.end()) );
    }

    /**
     * Checks if the stop is within street coordinates
     * @param stop Stop to be checked
     * @return Returns true if stop is within this street, false if not
     */
    public boolean isWithinStreet(Stop stop) {
        return ( (stop.getCoordinate().isGreaterOrEqual(this.begin()) && stop.getCoordinate().isLessOrEqual(this.end()))
        || (stop.getCoordinate().isLessOrEqual(this.begin()) && stop.getCoordinate().isGreaterOrEqual(this.end())) );
    }

}
