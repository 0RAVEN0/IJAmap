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
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;

public class Line {
    private String id;
    private List<Stop> stops = new ArrayList<>();
    private List<Street> streets = new ArrayList<>();
    private final List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private final List<Text> textArray = new ArrayList<>();
    
    private final String[] color = {"CYAN","CORAL","GOLD","FUCHSIA","IVORY","LAVENDER","LINEN","MAGENTA","MAROON","OLIVE","DARKBLUE","RED","BLUE","GREEN","YELLOW","PINK","ORANGE","BROWN","PURPLE","GREY"};

    private javafx.scene.shape.Line line = null;
    private Text text = null;

    @JsonCreator
    public Line(String id) {
        this.id = id;
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

    /**
     * Draw all lines into Pane from StreetReader class
     */
    public List<javafx.scene.shape.Line> drawLine(List<Street> streets){
        for (int streetSize = 0; streetSize < streets.size(); streetSize++) {
            for (int coorSize = 0; coorSize < streets.get(streetSize).getCoordinates().size() - 1; coorSize++) {
                line = new javafx.scene.shape.Line();
                line.setStartX(streets.get(streetSize).getCoordinates().get(coorSize).getX());
                line.setStartY(streets.get(streetSize).getCoordinates().get(coorSize).getY());
                line.setEndX(streets.get(streetSize).getCoordinates().get(coorSize + 1).getX());
                line.setEndY(streets.get(streetSize).getCoordinates().get(coorSize + 1).getY());
                line.setStroke(Color.valueOf(color[streetSize]));
                line.setStrokeWidth(3);
                line.setCursor(Cursor.HAND);

                lineArray.add(line);
            }
        }

        return lineArray;
    }

    /**
     * Draw name of street into Pane from StreetReader class
     */
    public List<Text> drawText(List<Street> streets){
        for (int streetSize = 0; streetSize < streets.size(); streetSize++) {
            if (streets.get(streetSize).getCoordinates().get(0).getX() == streets.get(streetSize).getCoordinates().get(1).getX()){
                text = new Text(Math.abs(streets.get(streetSize).getCoordinates().get(0).getX() - 10), Math.abs(streets.get(streetSize).getCoordinates().get(0).getY() + streets.get(streetSize).getCoordinates().get(1).getY()) / 2.0, streets.get(streetSize).getId());
                text.setRotate(-90);
            }

            else if (streets.get(streetSize).getCoordinates().get(0).getY() == streets.get(streetSize).getCoordinates().get(1).getY()) {
                text = new Text(Math.abs(streets.get(streetSize).getCoordinates().get(0).getX() + streets.get(streetSize).getCoordinates().get(1).getX()) / 2.0, Math.abs(streets.get(streetSize).getCoordinates().get(0).getY() - 10), streets.get(streetSize).getId());
            }

            else{
                text = new Text(Math.abs(streets.get(streetSize).getCoordinates().get(0).getX() + streets.get(streetSize).getCoordinates().get(1).getX()) / 2.0, Math.abs(streets.get(streetSize).getCoordinates().get(0).getY() + streets.get(streetSize).getCoordinates().get(1).getY()) / 2.0, streets.get(streetSize).getId());
            }

            text.setFont(Font.font ("Verdana", 12));
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
