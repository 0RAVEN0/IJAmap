/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Map coordinates representation
 */
package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Coordinate {
    private int x;
    private int y;

    /**
     * Constructor that checks if all coordinates are positive
     * @param x x position
     * @param y y position
     */
    @JsonCreator
    public Coordinate(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Both x and y coordinates must be positive");
        }
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Calculates difference between x positions of two coordinates (this.x - that.x)
     * @param c Coordinate to take x from
     * @return Returns the difference (this.x - that.x)
     */
    public int diffX(Coordinate c) {
        return this.x -c.x;
    }

    /**
     * Calculates difference between y positions of two coordinates (this.y - that.y)
     * @param c Coordinate to take y from
     * @return Returns the difference (this.y - that.y)
     */
    public int diffY(Coordinate c) {
        return this.y -c.y;
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
                "xPos=" + x +
                ", yPos=" + y +
                '}';
    }
}
