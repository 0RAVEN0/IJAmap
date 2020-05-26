/**
 * Author: Michal Vanka (xvanka00) Romana Dzubarova (xdzuba00)
 * Contents: An object representing a bus
 */
package main.java.transport;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import main.java.map.Coordinate;
import main.java.map.Stop;
import main.java.shapes.ShapeCircle;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * An object representing a bus
 */
public class Bus {
    private final String id;
    private Coordinate position;
    private final Journey journey;
    private final Line line;
    private Circle busCircle = null;
    private final LocalTime start;
    private final LocalTime finish;

    /**
     * @param id A unique ID
     * @param journey A journey that this bus follows
     * @param line A line that the bus belongs to
     * @param start The first stop arrival time
     * @param finish The last stop departure time
     */
    public Bus(String id, Journey journey, Line line,LocalTime start, LocalTime finish) {
        this.id = id;
        this.journey = journey;
        this.line = line;
        this.start = start;
        this.finish = finish;
    }

    public final String getId() {
        return id;
    }

    public final Coordinate getPosition() {
        return position;
    }

    public final Journey getJourney() {
        return journey;
    }

    public final Line getLine() {
        return line;
    }

    /**
     * Calculates the position of the bus based on given parameters
     * @param pos Starting position on the current trail
     * @param distance Distance between the start and end of current trail
     * @param timeDiff Amount of seconds between the start and end of current trail
     * @param currentTime The current time
     * @param posDeparture Departure time from the starting position
     * @return new position of the bus on the map
     */
    private Coordinate calculatePosition(Coordinate pos, Coordinate distance, long timeDiff, LocalTime currentTime, LocalTime posDeparture) {
        double x = pos.getX() + (distance.getX() / timeDiff) * posDeparture.until(currentTime, ChronoUnit.SECONDS);
        double y = pos.getY() + (distance.getY() / timeDiff) * posDeparture.until(currentTime, ChronoUnit.SECONDS);
        return new Coordinate(x, y);
    }

    /**
     * Updates the position of the bus circle on the map
     * @param currentTime The current time
     * @param circles A list of circles on the map
     * @param mapWindow The mapWindow pane
     * @return The newly drawn circle (purely for creating clickable actions with it)
     */
    public Circle updatePosition(LocalTime currentTime, List<Circle> circles, Pane mapWindow) {
        //check if the time is between start and end of the journey
        if (currentTime.compareTo(this.start) >= 0
                && currentTime.compareTo(this.finish) <= 0) {
            //check between which 2 stops the bus should be
            for (int i = 0; i < journey.getSequence().size()-1; i++) {
                LocalTime stop1arrival = start.plusMinutes(journey.getSequence().get(i).getArrival());
                LocalTime stop1departure = start.plusMinutes(journey.getSequence().get(i).getDeparture());
                LocalTime stop2arrival = start.plusMinutes(journey.getSequence().get(i+1).getArrival());
                LocalTime stop2departure = start.plusMinutes(journey.getSequence().get(i+1).getDeparture());
                Stop stop1 = journey.getStops().get(i);
                Stop stop2 = journey.getStops().get(i+1);


                //if the time is between these two stops
                if (currentTime.compareTo(stop1departure) >= 0 && currentTime.compareTo(stop2arrival) <= 0) {
                    //number of seconds between stops
                    int timeDiff = (journey.getSequence().get(i+1).getArrival()-journey.getSequence().get(i).getDeparture())*60;
                    double x, y;

                    //if the stops are on the same street (which is just a line)
                    if (stop1.getStreet() == stop2.getStreet()) {
                        Coordinate distance = stop1.distance(stop2.getCoordinate());
                        position = calculatePosition(stop1.getCoordinate(), distance, timeDiff, currentTime, stop1departure);
                    }
                    else {
                        Coordinate streetEnd;
                        if (stop1.getStreet().follows(stop2.getStreet())) {
                            streetEnd = stop1.getStreet().closerEndTo(stop2.getCoordinate());
                        }
                        //if the street doesn't follow, the streets must intersect
                        else {
                            streetEnd = stop1.getStreet().findIntersectionWith(stop2.getStreet());
                            try {
                                if (streetEnd == null) {
                                    throw new ArithmeticException("Stops \""+stop1.getId()+"\" and \""+stop2.getId()+"\" are on non-following streets, but no intersection was found");
                                }
                            }
                            catch (ArithmeticException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Calculating bus position: "+e.getMessage());
                                alert.showAndWait();
                                continue;
                            }
                        }
                        //distance between stop1 and the street end
                        Coordinate distance1 = stop1.distance(streetEnd);
                        Coordinate distance2 = stop2.distance(streetEnd);
                        //distance between street end and stop2
                        distance2.negate();

                        //Pythagoras theorem used to calculate straight line distance between stop1 and street end
                        double totalDistance1 = Math.sqrt(streetEnd.diffX(stop1.getCoordinate()) * streetEnd.diffX(stop1.getCoordinate())
                                + streetEnd.diffY(stop1.getCoordinate()) *  streetEnd.diffY(stop1.getCoordinate()));
                        //Pythagoras theorem used to calculate straight line distance between street end and stop2
                        double totalDistance2 = Math.sqrt(stop2.getCoordinate().diffX(streetEnd) * stop2.getCoordinate().diffX(streetEnd)
                                + stop2.getCoordinate().diffY(streetEnd) *  stop2.getCoordinate().diffY(streetEnd));

                        //calculation of the time (in seconds) the bus spends between stop1 and street end
                        long timeDiff1 = (long) ((timeDiff * totalDistance1) / (totalDistance1+totalDistance2));
                        //calculation of the time (in seconds) the bus spends between street end and stop2
                        long timeDiff2 = (long) ((timeDiff * totalDistance2) / (totalDistance1+totalDistance2));

                        //if current time is before the time that bus passes the street end (line direction change)
                        if (currentTime.compareTo(stop1departure.plusSeconds(timeDiff1)) <= 0) {
                            position = calculatePosition(stop1.getCoordinate(), distance1, timeDiff1, currentTime, stop1departure);
                        }
                        else {
                            position = calculatePosition(streetEnd, distance2, timeDiff2, currentTime, stop1departure.plusSeconds(timeDiff1));
                        }
                    }
                    busCircle = ShapeCircle.drawCircle(position, this.id);

                    ShapeCircle.display(busCircle, circles, mapWindow);

                    return busCircle;
                } // end if time within two stop times
                // if within stop1 time
                else if (currentTime.compareTo(stop1arrival) >= 0 && currentTime.compareTo(stop1departure) <= 0) {
                    position = stop1.getCoordinate();
                    busCircle = ShapeCircle.drawCircle(position, this.id);

                    ShapeCircle.display(busCircle, circles, mapWindow);
                    return busCircle;
                } // end if within stop1
                // if within stop2 time
                else if(currentTime.compareTo(stop2arrival) >= 0 && currentTime.compareTo(stop2departure) <= 0) {
                    position = stop2.getCoordinate();
                    busCircle = ShapeCircle.drawCircle(position, this.id);

                    ShapeCircle.display(busCircle, circles, mapWindow);
                    return busCircle;
                }
            } //end for journey.sequence
        } //end if time between first and last stop
        else {
            if (busCircle != null) {
                ShapeCircle.remove(circles, mapWindow, busCircle);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return getId().equals(bus.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
