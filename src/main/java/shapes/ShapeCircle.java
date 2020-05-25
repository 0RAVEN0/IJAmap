package main.java.shapes;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.java.map.Coordinate;

import java.util.List;

/**
 * Methods for drawing a circle on the map
 */
public class ShapeCircle {

    /**
     * Draws a circle but doesn't display it on the map yet
     * @param position Coordinate position of the circle
     * @param id A unique id
     * @return A circle object with set coordinates and id
     */
    public static Circle drawCircle(Coordinate position, String id){
        Circle circle = new Circle(position.getX(), position.getY(), 7);
        circle.setId(id);
        circle.setCursor(Cursor.HAND);
        return circle;
    }

    /**
     * Displays circle on the map
     * @param busCircle The circle to display
     * @param circleId id of the given circle
     * @param circles list of circles where the circle will be added
     * @param mapWindow the pane with street map
     */
    public static void display(Circle busCircle, String circleId, List<Circle> circles, Pane mapWindow) {
        Circle tempCircle = null;
        for (Circle circle : circles) {
            if (circle.getId().equals(circleId)) {
                mapWindow.getChildren().remove(circle);
                tempCircle = circle;
            }
        }
        if (tempCircle != null) {
            circles.remove(tempCircle);
        }

        busCircle.setFill(Color.LAVENDER);
        busCircle.setStroke(Color.PURPLE);
        circles.add(busCircle);
        mapWindow.getChildren().add(busCircle);
    }
}
