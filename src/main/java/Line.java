package main.java;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private String id;
    private List<Stop> stops = new ArrayList<>();
    private List<Street> streets = new ArrayList<>();

    private Line(String id) {
        this.id = id;
    }

    public static Line defaultLine(String id) {
        return new Line(id);
    }

    public boolean addStreet(Street street) {
        if (stops.isEmpty()) {
            return false;
        }
        if (!street.follows(this.streets.get(this.streets.size()-1))) {
           return false;
        }
        streets.add(street);
        return true;
    }

    public boolean addStop(Stop stop) {
        if (this.stops.isEmpty()) {
            stops.add(stop);
            streets.add(stop.getStreet());
            return true;
        }
        if (!stop.getStreet().follows(this.streets.get(this.streets.size()-1))) {
            return false;
        }
        stops.add(stop);
        streets.add(stop.getStreet());
        return true;
    }

    public List<SimpleImmutableEntry<Street, Stop>> getRoute() {
        if (stops.isEmpty()) {
            return null;
        }

        List<SimpleImmutableEntry<Street, Stop>> route = new ArrayList<>();
        boolean match = false;
        for (Street street : streets) {
            for (Stop stop : stops) {
                if (stop.getStreet().equals(street)) {
                    match = true;
                    route.add(new SimpleImmutableEntry<>(street, stop));
                    break;
                }
            }
            if (!match) {
                route.add(new SimpleImmutableEntry<>(street,null));
            }
            else {
                match = false;
            }
        }
        return route;
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
