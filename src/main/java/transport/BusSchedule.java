package main.java.transport;

import java.time.LocalTime;

public class BusSchedule {
    private LocalTime arrival;
    private LocalTime departure;

    private final LocalTime originalArrival;
    private final LocalTime originalDeparture;

    private static final int busyDelay = 5;
    private static final int collapseDelay = 15;

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

    /**
     * Resets the schedule to the original times
     */
    public void reset() {
        this.arrival = originalArrival;
        this.departure = originalDeparture;
    }

    /**
     * Adds 5 minute delay
     */
    public void setDelayBusy() {
        this.arrival = this.originalArrival.plusMinutes(busyDelay);
        this.departure = this.originalDeparture.plusMinutes(busyDelay);
    }

    /**
     * Adds 15 minute delay
     */
    public void setDelayCollapse() {
        this.arrival = this.originalArrival.plusMinutes(collapseDelay);
        this.departure = this.originalDeparture.plusMinutes(collapseDelay);
    }
}
