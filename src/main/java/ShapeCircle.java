package main.java;

import javafx.scene.shape.Circle;

import java.util.List;

public class ShapeCircle {

    private Circle busCircle = new Circle();

    public Circle drawCircle(List<Line> line){

        /**
         * ---> nastavíš si stred kruhu (x-ovu a y-ovu suradnisu stredu) plus polomer kruhu.
         * line.get(0).getStops().get(0).getCoordinate().getX() týmto sposobom
         * sa posuvam v tom .yaml subore na to miesto ktoré chcem čiže coordinaty.
         * Choď do Controller. --->
         */
        busCircle.setCenterX(line.get(0).getStops().get(0).getCoordinate().getX());
        busCircle.setCenterY(line.get(0).getStops().get(0).getCoordinate().getY());
        busCircle.setRadius(5);

        return busCircle;
    }
}
