/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Graphic representations of line shape for javaFX
 */
package main.java.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.map.Street;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphic representations of line shape for javaFX
 */
public class ShapeLine {
    private final List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private final List<Text> textArray = new ArrayList<>();

    private javafx.scene.shape.Line line = null;

    private Text text = null;


    /**
     * Draw all lines into Pane from Reader class
     * @param streets A list of streets
     * @return A list of drawn lines
     */
    public List<Line> drawLine(List<Street> streets,String color){
        for (int streetSize = 0; streetSize < streets.size(); streetSize++) {
            for (int coorSize = 0; coorSize < streets.get(streetSize).getCoordinates().size() - 1; coorSize++) {
                line = new javafx.scene.shape.Line();
                line.setId(streets.get(streetSize).getId());
                line.setStartX(streets.get(streetSize).getCoordinates().get(coorSize).getX());
                line.setStartY(streets.get(streetSize).getCoordinates().get(coorSize).getY());
                line.setEndX(streets.get(streetSize).getCoordinates().get(coorSize + 1).getX());
                line.setEndY(streets.get(streetSize).getCoordinates().get(coorSize + 1).getY());
                line.setStrokeWidth(3);
                line.setStroke(Color.valueOf(color));
                line.setStrokeLineCap(StrokeLineCap.ROUND);

                lineArray.add(line);
            }
        }

        return lineArray;
    }
}
