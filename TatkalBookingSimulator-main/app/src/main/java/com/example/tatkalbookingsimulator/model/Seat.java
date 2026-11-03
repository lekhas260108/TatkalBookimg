package com.example.tatkalbookingsimulator.model;

public class Seat {

    private final int seatNumber;
    private boolean available = true;

    public Seat(int seatNumber) {
        if (seatNumber < 1) {
            throw new IllegalArgumentException("Seat number must be positive.");
        }
        this.seatNumber = seatNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public synchronized boolean isAvailable() {
        return available;
    }

    /**
     * Atomically reserves this seat.
     * Only one competing thread can successfully reserve it.
     */
    public synchronized boolean book() {
        if (!available) {
            return false;
        }

        available = false;
        return true;
    }

    /**
     * Releases the seat after cancellation.
     */
    public synchronized void release() {
        available = true;
    }
}