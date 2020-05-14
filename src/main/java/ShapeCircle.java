package main.java;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ShapeCircle {

    private Circle busCircle = new Circle();

    public Circle drawCircle(Coordinate position){

        busCircle.setCenterX(position.getX());
        busCircle.setCenterY(position.getY());
        busCircle.setRadius(5);

        return busCircle;
    }
}
