/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: A single journey path from one end of the line to the other with all departure times
 */
package main.java;

import java.time.LocalTime;
import java.util.List;

/**
 * A single journey path from one end of the line to the other with all departure times
 */
public class Journey {
    private final String id;
    private List<Stop> stops;
    private List<LocalTime> starts;
    private List<Schedule> sequence;
}
