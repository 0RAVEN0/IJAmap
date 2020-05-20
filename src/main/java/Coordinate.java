/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Map coordinates representation
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * An xy coordinates on the map
 */
public class Coordinate {
    private double x;
    private double y;

    /**
     * Constructor that checks if all coordinates are positive
     * @param x x position
     * @param y y position
     */
    @JsonCreator
    public Coordinate(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Both x and y coordinates must be positive");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Alternate constructor that allows negative values (used only for calculating distance)
     * @param x x distance
     * @param y y distance
     * @param diff dummy flag to differ the constructor
     */
    public Coordinate(double x, double y, boolean diff) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Calculates difference between x positions of two coordinates (this.x - that.x)
     * @param c Coordinate to take x from
     * @return Returns the difference (this.x - that.x)
     */
    public double diffX(Coordinate c) {
        return this.x -c.getX();
    }

    /**
     * Calculates difference between y positions of two coordinates (this.y - that.y)
     * @param c Coordinate to take y from
     * @return Returns the difference (this.y - that.y)
     */
    public double diffY(Coordinate c) {
        return this.y -c.getY();
    }

    /**
     * Negates both x and y coordinates
     */
    public void negate() {
        this.x = -x;
        this.y = -y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
