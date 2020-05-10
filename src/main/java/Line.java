/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Representation of a line crossing one or more streets with multiple stops
 */
package main.java;


import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a line crossing one or more streets with multiple stops
 */
public class Line {
    private String id;
    private List<Stop> stops;
    private List<Street> streets;
    private List<String> streetIDs;

    @JsonCreator
    public Line(@JsonProperty("id") String id, @JsonProperty("stops") List<Stop> stops,@JsonProperty("streetIDs") List<String> streetIDs) {
        this.id = id;
        this.stops = stops;
        this.streetIDs = streetIDs;
    }

    public String getId() {
        return id;
    }

    public List<String> getStreetIDs() {
        return streetIDs;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }

    /**
     * Adds street to this line
     * @param street Street to be added
     * @return Returns true upon success, false upon failure
     */
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

    /**
     * Adds stop to this line
     * @param stop Stop to be added
     * @return Returns true upon success, false upon failure
     */
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
