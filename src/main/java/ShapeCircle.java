package main.java;

import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ShapeCircle {

    public static Circle drawCircle(Coordinate position, String id){
        Circle circle = new Circle(position.getX(), position.getY(), 5);
        circle.setId(id);
        circle.setCursor(Cursor.HAND);
        return circle;
    }

    public static void display(Circle busCircle, String circleId, List<Circle> circles, Pane mapWindow) {
        for (Circle circle : circles) {
            if (circle.getId().equals(circleId)) {
                mapWindow.getChildren().remove(circle);
            }
        }
        busCircle.setFill(Color.LAVENDER);
        busCircle.setStroke(Color.PURPLE);
        circles.add(busCircle);
        mapWindow.getChildren().add(busCircle);
    }
}
