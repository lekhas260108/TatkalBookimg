package com.example.tatkalbookingsimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Coach {

    private final String coachNumber;
    private final String coachType;
    private final List<Seat> seats;

    public Coach(String coachNumber, String coachType, int totalSeats) {
        if (coachNumber == null || coachNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Coach number is required.");
        }
        if (coachType == null || coachType.trim().isEmpty()) {
            throw new IllegalArgumentException("Coach type is required.");
        }
        if (totalSeats < 1) {
            throw new IllegalArgumentException("A coach must have at least one seat.");
        }

        this.coachNumber = coachNumber.trim();
        this.coachType = coachType.trim();
        this.seats = new ArrayList<>();

        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public String getCoachType() {
        return coachType;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public synchronized Seat getAvailableSeat() {
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                return seat;
            }
        }
        return null;
    }

    public synchronized int getAvailableSeatCount() {
        int count = 0;

        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                count++;
            }
        }

        return count;
    }
}