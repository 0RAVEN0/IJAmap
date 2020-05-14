package main.java;

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ShapeCircle {

    private final List<Circle> circleArray = new ArrayList<>();
    private Circle busCircle = null;

    public List<Circle> drawCircle(List<Line> line){

        /**
         * ---> nastavíš si stred kruhu (x-ovu a y-ovu suradnisu stredu) plus polomer kruhu.
         * line.get(0).getStops().get(0).getCoordinate().getX() týmto sposobom
         * sa posuvam v tom .yaml subore na to miesto ktoré chcem čiže coordinaty.
         * Choď do Controller. --->
         */

        for (int lineSize = 0; lineSize < line.size(); lineSize++) {
            for (int i = 0; i < line.get(lineSize).getStops().size(); i++) {
                busCircle = new Circle();
                busCircle.setCenterX(line.get(lineSize).getStops().get(i).getCoordinate().getX());
                busCircle.setCenterY(line.get(lineSize).getStops().get(i).getCoordinate().getY());
                busCircle.setRadius(5);

                circleArray.add(busCircle);
            }
        }

        return circleArray;
    }
}
