package main.java;

import javafx.scene.shape.Circle;

public class ShapeCircle {

    private Circle busCircle = new Circle();

    public Circle drawCircle(Coordinate position){

        busCircle.setCenterX(position.getX());
        busCircle.setCenterY(position.getY());
        busCircle.setRadius(5);

        return busCircle;
    }
}
