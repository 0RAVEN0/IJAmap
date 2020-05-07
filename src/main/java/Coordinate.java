package main.java;

import java.util.Objects;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinate {
    private int x;
    private int y;

    public Coordinate() {};

    public Coordinate(int x, int y) {
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

    public int diffX(Coordinate c) {
        return this.x -c.x;
    }

    public int diffY(Coordinate c) {
        return this.y -c.y;
    }

    public boolean isGreaterOrEqual(Coordinate coordinate) {
        return ( coordinate.getX() <= this.getX() && coordinate.getY() <= this.getY() );
    }

    public boolean isLessOrEqual(Coordinate coordinate) {
        return ( coordinate.getX() >= this.getX() && coordinate.getY() >= this.getY() );
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
