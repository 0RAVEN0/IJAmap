/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Class representing a single stop
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Stop {
    private String id;
    private Coordinate coordinate = null;
    private Street street = null;
    private String name;
    private String streetID;

    @JsonCreator
    public Stop(@JsonProperty("id") String id,@JsonProperty("coordinate") Coordinate coordinate,@JsonProperty("streetID") String streetID) {
        this.coordinate = coordinate;
        this.id = id;
        this.streetID = streetID;
    }

    public Stop(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Street getStreet() {
        return street;
    }

    public String getStreetID() {
        return streetID;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return id.equals(stop.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "stop(" + id + ')';
    }
}
