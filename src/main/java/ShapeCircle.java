package main.java;

import javafx.scene.Cursor;
import javafx.scene.shape.Circle;

public class ShapeCircle {

    private Circle busCircle;

    public Circle drawCircle(Coordinate position){
        busCircle = new Circle();
        busCircle.setTranslateX(position.getX() + busCircle.getTranslateX());
        busCircle.setTranslateY(position.getY() + busCircle.getTranslateY());
        busCircle.setRadius(5);
        busCircle.setCursor(Cursor.HAND);

        return busCircle;
    }
}
