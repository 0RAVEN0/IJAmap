package main.java;


import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.scene.text.*;

public class Line {
    private String id;
    private List<Stop> stops = new ArrayList<>();
    private List<Street> streets = new ArrayList<>();
    private List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private List<Text> textArray = new ArrayList<>();

    private javafx.scene.shape.Line line = null;
    private Text text = null;

    Line(String id) {
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

    /**
     * Draw all lines into Pane from streetReader class
     */
    public List<javafx.scene.shape.Line> drawLine(List<Street> streets){
        //textArray.add(streets.get(0).getId());
        for (int streetSize = 0; streetSize < streets.size(); streetSize++) {
            for (int coorSize = 0; coorSize < streets.get(streetSize).getCoordinates().size() - 1; coorSize++) {
                line = new javafx.scene.shape.Line();
                line.setStartX(streets.get(streetSize).getCoordinates().get(coorSize).getX());
                line.setStartY(streets.get(streetSize).getCoordinates().get(coorSize).getY());
                line.setEndX(streets.get(streetSize).getCoordinates().get(coorSize + 1).getX());
                line.setEndY(streets.get(streetSize).getCoordinates().get(coorSize + 1).getY());

                lineArray.add(line);
            }
        }

        return lineArray;
    }

    public List<Text> drawText(List<Street> streets){
        for (int streetSize = 0; streetSize < streets.size(); streetSize++) {

            text = new Text(Math.abs(streets.get(streetSize).getCoordinates().get(0).getX() + streets.get(streetSize).getCoordinates().get(1).getX())/2.0, Math.abs(streets.get(streetSize).getCoordinates().get(streetSize).getY() - 10), streets.get(streetSize).getId());

            textArray.add(text);
        }

        return textArray;
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
