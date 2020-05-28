/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Single arrival and departure time in the schedule, represented by amount of minutes since departure from starting stop
 */
package main.java.transport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Single arrival and departure time in the schedule, represented by amount of minutes since departure from starting stop
 */
public class Schedule {
    private final int arrival;
    private final int departure;

    /**
     * Constructor for schedule
     * @param arrival Time of arrival to the station in minutes since departure from starting stop
     * @param departure Time of departure from the station in minutes since departure from starting stop
     */
    @JsonCreator
    public Schedule(@JsonProperty("arrival") int arrival, @JsonProperty("departure") int departure) {
        this.arrival = arrival;
        this.departure = departure;
    }

    public int getArrival() {
        return arrival;
    }

    public int getDeparture() {
        return departure;
    }
}
