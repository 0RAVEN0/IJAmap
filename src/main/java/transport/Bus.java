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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An object representing a bus
 */
public class Bus {
    private final String id;
    private Coordinate position = null;
    private final Journey journey;
    private final Line line;
    private Circle busCircle = null;
    private final List<BusSchedule> busSchedules = new ArrayList<>();
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
        for (Schedule schedule : journey.getSequence()) {
            busSchedules.add(new BusSchedule(start, schedule.getArrival(), schedule.getDeparture()));
        }
        this.start = start;
        this.finish = finish;
    }

    public final String getId() {
        return id;
    }

    public final Coordinate getPosition() {
        return position;
    }

    public final void setPosition(Coordinate position) {
        this.position = position;
    }

    public final Journey getJourney() {
        return journey;
    }

    public final Line getLine() {
        return line;
    }

    public List<BusSchedule> getBusSchedules() {
        return busSchedules;
    }

    /**
     * Calculates the position of the bus based on given parameters
     * @param pos1 Starting position on the current trail
     * @param pos2 End position on the current trail
     * @param pos2arrival Time of the arrival to the end position
     * @param currentTime The current time
     * @return new position of the bus on the map
     */
    private Coordinate calculatePosition(Coordinate pos1, Coordinate pos2, LocalTime pos2arrival, LocalTime currentTime, int busyness) {
        double x, y;
        double distance, speed;
        long time;
        Coordinate difference = pos1.difference(pos2);

        if (difference.getX() == 0) {
            distance = Math.abs(difference.getY());
        }
        else if (difference.getY() == 0) {
            distance = Math.abs(difference.getX());
        }
        else {
            distance = Math.sqrt(difference.getX() * difference.getX() + difference.getY() * difference.getY());
        }

        time = currentTime.until(pos2arrival, ChronoUnit.SECONDS);
        if (time == 0) {
            time = 1;
        }

        speed = distance / time;
        speed /= busyness;

        if (distance <= speed) {
            speed = distance;
        }

        if (pos1.getX() == pos2.getX()) {
            x = pos1.getX();
            y = pos1.getY() + (difference.getX() / (distance/speed));
        }
        else if (pos1.getY() == pos2.getY()) {
            x = pos1.getX() + (difference.getX() / (distance/speed));
            y = pos1.getY();
        }
        else {
            x = pos1.getX() + (difference.getX() / (distance/speed));
            y = pos1.getY() + (difference.getY() / (distance/speed));
        }

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
                LocalTime stop1arrival = busSchedules.get(i).getArrival();
                LocalTime stop1departure = busSchedules.get(i).getDeparture();
                LocalTime stop2arrival = busSchedules.get(i+1).getArrival();
                LocalTime stop2departure = busSchedules.get(i+1).getDeparture();
                Stop stop1 = journey.getStops().get(i);
                Stop stop2 = journey.getStops().get(i+1);


                //if the time is between these two stops
                if (currentTime.compareTo(stop1departure) > 0 && currentTime.compareTo(stop2arrival) < 0) {
                    //number of seconds between stops
                    long timeDiff = stop1departure.until(stop2arrival, ChronoUnit.SECONDS);
                    if (position == null) {
                        calculateFirstPosition(currentTime, stop1departure, i);
                    }
                    int busyness;

                    //if the stops are on the same street (which is just a line)
                    if (stop1.getStreet() == stop2.getStreet()) {
                        busyness = stop1.getStreet().getBusyness();
                        position = calculatePosition(position, stop2.getCoordinate(), stop2arrival, currentTime, busyness);
                        //position = calculatePosition(stop1.getCoordinate(), distance, timeDiff, currentTime, stop1departure);
                    }
                    else {
                        Coordinate streetEnd;
                        LocalTime streetEndArrival;
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

                        //Pythagoras theorem used to calculate straight line distance between stop1 and street end
                        double totalDistance1 = Math.sqrt(streetEnd.diffX(stop1.getCoordinate()) * streetEnd.diffX(stop1.getCoordinate())
                                + streetEnd.diffY(stop1.getCoordinate()) *  streetEnd.diffY(stop1.getCoordinate()));
                        //Pythagoras theorem used to calculate straight line distance between street end and stop2
                        double totalDistance2 = Math.sqrt(stop2.getCoordinate().diffX(streetEnd) * stop2.getCoordinate().diffX(streetEnd)
                                + stop2.getCoordinate().diffY(streetEnd) *  stop2.getCoordinate().diffY(streetEnd));

                        //calculation of the time (in seconds) the bus spends between stop1 and street end
                        long timeDiff1 = (long) ((timeDiff * totalDistance1) / (totalDistance1+totalDistance2));

                        streetEndArrival = stop1departure.plusSeconds(timeDiff1);

                        if (currentTime.compareTo(streetEndArrival) < 0) {
                            busyness = stop1.getStreet().getBusyness();
                            position = calculatePosition(position, streetEnd, streetEndArrival, currentTime, busyness);
                        }
                        else {
                            busyness = stop2.getStreet().getBusyness();
                            position = calculatePosition(position, stop2.getCoordinate(), stop2arrival, currentTime, busyness);
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

    private void calculateFirstPosition(LocalTime currentTime, LocalTime stop1departure, int i) {
        Stop stop1 = journey.getStops().get(i);
        Stop stop2 = journey.getStops().get(i + 1);
        //number of seconds between stops
        int timeDiff = (journey.getSequence().get(i + 1).getArrival() - journey.getSequence().get(i).getDeparture()) * 60;
        Coordinate position;
        double x, y;

        //if the stops are on the same street (which is just a line)
        if (stop1.getStreet() == stop2.getStreet()) {
            Coordinate distance = stop1.distance(stop2.getCoordinate());
            x = (stop1.getCoordinate().getX()
                    + (distance.getX() / timeDiff) * (stop1departure.until(currentTime, ChronoUnit.SECONDS)));
            y = (stop1.getCoordinate().getY()
                    + (distance.getY() / timeDiff) * (stop1departure.until(currentTime, ChronoUnit.SECONDS)));
            position = new Coordinate(x, y);
        } else {
            Coordinate streetEnd;
            if (stop1.getStreet().follows(stop2.getStreet())) {
                streetEnd = stop1.getStreet().closerEndTo(stop2.getCoordinate());
            }
            //if the street doesn't follow, the streets must intersect
            else {
                streetEnd = stop1.getStreet().findIntersectionWith(stop2.getStreet());
                try {
                    if (streetEnd == null) {
                        throw new ArithmeticException("Stops \"" + stop1.getId() + "\" and \"" + stop2.getId() + "\" are on non-following streets, but no intersection was found");
                    }
                } catch (ArithmeticException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Calculating bus position: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            //distance between stop1 and the street end
            Coordinate distance1 = stop1.distance(streetEnd);
            Coordinate distance2 = stop2.distance(streetEnd);
            //distance between street end and stop2
            distance2.negate();

            //Pythagoras theorem used to calculate straight line distance between stop1 and street end
            double totalDistance1 = Math.sqrt(streetEnd.diffX(stop1.getCoordinate()) * streetEnd.diffX(stop1.getCoordinate())
                    + streetEnd.diffY(stop1.getCoordinate()) * streetEnd.diffY(stop1.getCoordinate()));
            //Pythagoras theorem used to calculate straight line distance between street end and stop2
            double totalDistance2 = Math.sqrt(stop2.getCoordinate().diffX(streetEnd) * stop2.getCoordinate().diffX(streetEnd)
                    + stop2.getCoordinate().diffY(streetEnd) * stop2.getCoordinate().diffY(streetEnd));

            //calculation of the time (in seconds) the bus spends between stop1 and street end
            long timeDiff1 = (long) ((timeDiff * totalDistance1) / (totalDistance1 + totalDistance2));
            //calculation of the time (in seconds) the bus spends between street end and stop2
            long timeDiff2 = (long) ((timeDiff * totalDistance2) / (totalDistance1 + totalDistance2));

            //if current time is before the time that bus passes the street end (line direction change)
            if (currentTime.compareTo(stop1departure.plusSeconds(timeDiff1)) <= 0) {
                x = stop1.getCoordinate().getX()
                        + (distance1.getX() / timeDiff1) * (stop1departure.until(currentTime, ChronoUnit.SECONDS));
                y = stop1.getCoordinate().getY()
                        + (distance1.getY() / timeDiff1) * (stop1departure.until(currentTime, ChronoUnit.SECONDS));
            } else {
                x = streetEnd.getX() + (distance2.getX() / timeDiff2)
                        * (stop1departure.plusSeconds(timeDiff1).until(currentTime, ChronoUnit.SECONDS));
                y = streetEnd.getY() + (distance2.getY() / timeDiff2)
                        * (stop1departure.plusSeconds(timeDiff1).until(currentTime, ChronoUnit.SECONDS));
            }
            this.position = new Coordinate(x, y);
        }
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
