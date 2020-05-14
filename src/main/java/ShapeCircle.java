package main.java;

import javafx.scene.Cursor;
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
}
