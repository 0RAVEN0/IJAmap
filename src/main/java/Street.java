/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Class representing street with adittional functions
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Street {
    private final String id;
    private final List<Coordinate> coordinates;
    private final List<Stop> stops = new ArrayList<>();

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
        if (this.begin().getX() <= this.end().getX()) {
            if (this.begin().getY() <= this.end().getY()) {
                return stop.getCoordinate().getX() >= this.begin().getX()
                        && stop.getCoordinate().getY() >= this.begin().getY()
                        && stop.getCoordinate().getX() <= this.end().getX()
                        && stop.getCoordinate().getY() <= this.end().getY();
            }
            else {
                return stop.getCoordinate().getX() >= this.begin().getX()
                        && stop.getCoordinate().getY() <= this.begin().getY()
                        && stop.getCoordinate().getX() <= this.end().getX()
                        && stop.getCoordinate().getY() >= this.end().getY();
            }
        }
        else {
            if (this.begin().getY() <= this.end().getY()) {
                return stop.getCoordinate().getX() <= this.begin().getX()
                        && stop.getCoordinate().getY() >= this.begin().getY()
                        && stop.getCoordinate().getX() >= this.begin().getX()
                        && stop.getCoordinate().getY() <= this.end().getY();
            }
            else {
                return stop.getCoordinate().getX() <= this.begin().getX()
                        && stop.getCoordinate().getY() <= this.begin().getY()
                        && stop.getCoordinate().getX() >= this.begin().getX()
                        && stop.getCoordinate().getY() >= this.end().getY();
            }
        }
    }

    /**
     * Calculates which end of this street is closer to the given coordinates
     * @param coordinate Coordinate to be checked against
     * @return Closer of the two street ends
     */
    public Coordinate closerEndTo(Coordinate coordinate) {
        double distanceBegin = Math.sqrt(coordinate.diffX(this.begin()) * coordinate.diffX(this.begin())
                + coordinate.diffY(this.begin()) *  coordinate.diffY(this.begin()));
        double distanceEnd = Math.sqrt(coordinate.diffX(this.end()) * coordinate.diffX(this.end())
                + coordinate.diffY(this.end()) *  coordinate.diffY(this.end()));
        if (distanceBegin > distanceEnd) {
            return this.end();
        }
        else {
            return this.begin();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Street street = (Street) o;
        return getId().equals(street.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
