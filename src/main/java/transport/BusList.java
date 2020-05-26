package main.java.transport;

import java.util.ArrayList;
import java.util.List;

public class BusList {
    private final List<Bus> busList = new ArrayList<>();

    public List<Bus> getBusList() {
        return busList;
    }

    public boolean add(Bus bus) {
        if (!busList.contains(bus)) {
            busList.add(bus);
            return true;
        }
        return false;
    }

    public Bus findById(String id) {
        for (Bus bus : busList) {
            if (bus.getId().equals(id)) {
                return bus;
            }
        }
        return null;
    }

    public Bus findByBus(Bus bus) {
        return busList.get(busList.indexOf(bus));
    }

    public void clear() {
        busList.clear();
    }
}
