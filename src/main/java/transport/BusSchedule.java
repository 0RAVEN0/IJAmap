package main.java.transport;

import java.time.LocalTime;

public class BusSchedule {
    private LocalTime arrival;
    private LocalTime departure;

    public BusSchedule(LocalTime start, int arrival, int departure) {
        this.arrival = start.plusMinutes(arrival);
        this.departure = start.plusMinutes(departure);
    }

    public LocalTime getArrival() {
        return arrival;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    /**
     * Adds or subtracts delay
     * @param delay delay in minutes (may be negative)
     */
    public void setDelay(int delay) {
        this.arrival = this.arrival.plusMinutes(delay);
        this.departure = this.departure.plusMinutes(delay);
    }
}
