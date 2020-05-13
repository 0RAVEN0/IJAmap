/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Class representing a single stop
 */
package main.java;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

/**
 * Class representing a single stop
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Stop {
    private final String id;
    private final Coordinate coordinate;
    private Street street = null;
    private String name;

    /**
     * Constructor for a stop
     * @param id A unique ID
     * @param coordinate x and y coordinates for the stop
     */
    @JsonCreator
    public Stop(@JsonProperty("id") String id,@JsonProperty("coordinate") Coordinate coordinate) {
        this.coordinate = coordinate;
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

    public void setStreet(Street street) {
        this.street = street;
    }

    /**
     * Calculates both x and y distance from the given coordinate
     * @param coordinate Distant coordinate to be checked against
     * @return Coordinate object with respective x and y distances
     */
    public Coordinate distance(Coordinate coordinate) {
        return new Coordinate(coordinate.diffX(this.getCoordinate()), coordinate.diffY(this.getCoordinate()), true);
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
