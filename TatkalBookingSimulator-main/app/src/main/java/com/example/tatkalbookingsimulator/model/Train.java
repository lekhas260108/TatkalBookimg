package com.example.tatkalbookingsimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Train {

    private final String trainNumber;
    private final String trainName;
    private final String source;
    private final String destination;
    private final String departureTime;
    private final String arrivalTime;
    private final List<Coach> coaches = new ArrayList<>();

    public Train(
            String trainNumber,
            String trainName,
            String source,
            String destination,
            String departureTime,
            String arrivalTime
    ) {
        this.trainNumber = requireText(trainNumber, "Train number");
        this.trainName = requireText(trainName, "Train name");
        this.source = requireText(source, "Source");
        this.destination = requireText(destination, "Destination");
        this.departureTime = departureTime == null ? "" : departureTime.trim();
        this.arrivalTime = arrivalTime == null ? "" : arrivalTime.trim();
    }

    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public synchronized void addCoach(Coach coach) {
        if (coach == null) {
            throw new IllegalArgumentException("Coach cannot be null.");
        }
        coaches.add(coach);
    }

    public synchronized List<Coach> getCoaches() {
        return Collections.unmodifiableList(new ArrayList<>(coaches));
    }

    public synchronized int getTotalAvailableSeats() {
        int total = 0;

        for (Coach coach : coaches) {
            total += coach.getAvailableSeatCount();
        }

        return total;
    }

    public boolean matchesRoute(String from, String to) {
        if (from == null || to == null) {
            return false;
        }

        return source.equalsIgnoreCase(from.trim())
                && destination.equalsIgnoreCase(to.trim());
    }
}