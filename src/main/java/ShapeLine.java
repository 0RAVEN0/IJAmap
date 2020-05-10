/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Graphic representations of line shape for javaFX
 */
package main.java;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphic representations of line shape for javaFX
 */
public class ShapeLine {
    private final List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private final List<Text> textArray = new ArrayList<>();

    private final String[] color = {"CYAN","CORAL","GOLD","FUCHSIA","DARKGREEN","DARKCYAN","BLUEVIOLET","MAGENTA","MAROON","OLIVE","DARKBLUE","RED","BLUE","GREEN","YELLOW","PINK","ORANGE","BROWN","PURPLE","GREY"};

    private javafx.scene.shape.Line line = null;
    private Text text = null;


    /**
     * Draw all lines into Pane from StreetReader class
     */
    public List<Line> drawLine(List<Street> streets){
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
        for (Street street : streets) {
            if (street.getCoordinates().get(0).getX() == street.getCoordinates().get(1).getX()) {
                text = new Text(Math.abs(street.getCoordinates().get(0).getX() - 10), Math.abs(street.getCoordinates().get(0).getY() + street.getCoordinates().get(1).getY()) / 2.0, street.getId());
                text.setRotate(-90);
            } else if (street.getCoordinates().get(0).getY() == street.getCoordinates().get(1).getY()) {
                text = new Text(Math.abs(street.getCoordinates().get(0).getX() + street.getCoordinates().get(1).getX()) / 2.0, Math.abs(street.getCoordinates().get(0).getY() - 10), street.getId());
            } else {
                text = new Text(Math.abs(street.getCoordinates().get(0).getX() + street.getCoordinates().get(1).getX()) / 2.0, Math.abs(street.getCoordinates().get(0).getY() + street.getCoordinates().get(1).getY()) / 2.0, street.getId());
            }

            text.setFont(Font.font("Verdana", 12));
            textArray.add(text);
        }

        return textArray;

    }
}
