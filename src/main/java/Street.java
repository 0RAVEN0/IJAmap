package main.java;

import java.util.ArrayList;
import java.util.List;

public class Street {
    private String id;
    private List<Coordinate> coordinates = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Street() {};

    public Street(String id, List<Coordinate> coordinates){
        for (int i = 0; i < coordinates.size()-1; i++) {
            if (coordinates.get(i).getX() != coordinates.get(i + 1).getX() && coordinates.get(i).getY() != coordinates.get(i + 1).getY()) {
                throw new IllegalArgumentException("Streets can only have right angle turns");
            }
        }

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

    public boolean addStop(Stop stop) {
        if (this.isWithinStreet(stop)) {
            stops.add(stop);
            stop.setStreet(this);
            return true;
        }

        return false;
    }

    public Coordinate begin() {
        return this.coordinates.get(0);
    }

    public Coordinate end() {
        return this.coordinates.get((this.coordinates.size())-1);
    }

    public boolean follows(Street s) {
        return ( this.begin().equals(s.begin()) ||
                this.begin().equals(s.end()) ||
                this.end().equals(s.begin()) ||
                this.end().equals(s.end()) );
    }

    public boolean isWithinStreet(Stop stop) {
        return ( (stop.getCoordinate().isGreaterOrEqual(this.begin()) && stop.getCoordinate().isLessOrEqual(this.end()))
        || (stop.getCoordinate().isLessOrEqual(this.begin()) && stop.getCoordinate().isGreaterOrEqual(this.end())) );
    }

}
