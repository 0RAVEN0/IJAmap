package main.java;

import java.util.Objects;

public class Coordinate {
    private int xPos;
    private int yPos;

    private Coordinate(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public static Coordinate create(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        }
        return new Coordinate(x,y);
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public int diffX(Coordinate c) {
        return this.xPos-c.xPos;
    }

    public int diffY(Coordinate c) {
        return this.yPos-c.yPos;
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
        return xPos == that.xPos &&
                yPos == that.yPos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPos, yPos);
    }
}
