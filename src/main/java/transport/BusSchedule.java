/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: A schedule for one stop for one particular bus journey
 */
package main.java.transport;

import java.time.LocalTime;

/**
 * A schedule for one stop for one particular bus journey
 */
public class BusSchedule {
    private LocalTime arrival;
    private LocalTime departure;

    private long delay;

    private final LocalTime originalArrival;
    private final LocalTime originalDeparture;

    /**
     * @param start Start time of the bus journey
     * @param arrival arrival time at the stop (taken from journey sequence, thus int)
     * @param departure departure time at the stop (taken from journey sequence, thus int)
     */
    public BusSchedule(LocalTime start, int arrival, int departure) {
        this.arrival = start.plusMinutes(arrival);
        this.departure = start.plusMinutes(departure);
        this.originalArrival = start.plusMinutes(arrival);
        this.originalDeparture = start.plusMinutes(departure);
    }

    public LocalTime getArrival() {
        return arrival;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    public LocalTime getOriginalArrival() {
        return originalArrival;
    }

    public LocalTime getOriginalDeparture() {
        return originalDeparture;
    }

    public long getDelay() {
        return delay;
    }

    /**
     * Sets the delay
     * @param delay Delay in minutes
     */
    public void setDelay(long delay) {
        this.arrival = this.originalArrival.plusMinutes(delay);
        this.departure = this.originalDeparture.plusMinutes(delay);
        this.delay = delay;
    }
}
