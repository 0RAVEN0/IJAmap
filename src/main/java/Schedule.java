package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Schedule {
    private final int arrival;
    private final int departure;

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
