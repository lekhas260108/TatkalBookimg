package com.example.tatkalbookingsimulator.model;

public class Booking {

    private final String bookingId;
    private final Passenger passenger;
    private final Train train;
    private Seat seat;
    private BookingStatus status;

    public Booking(
            String bookingId,
            Passenger passenger,
            Train train,
            Seat seat,
            BookingStatus status
    ) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID is required.");
        }
        if (passenger == null || train == null || status == null) {
            throw new IllegalArgumentException(
                    "Passenger, train, and status are required."
            );
        }
        if (status == BookingStatus.CONFIRMED && seat == null) {
            throw new IllegalArgumentException(
                    "A confirmed booking must have a seat."
            );
        }
        if (status != BookingStatus.CONFIRMED && seat != null) {
            throw new IllegalArgumentException(
                    "Only a confirmed booking can hold a seat."
            );
        }

        this.bookingId = bookingId.trim();
        this.passenger = passenger;
        this.train = train;
        this.seat = seat;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Train getTrain() {
        return train;
    }

    public synchronized Seat getSeat() {
        return seat;
    }

    public synchronized BookingStatus getStatus() {
        return status;
    }

    /**
     * Assigns a seat when promoting RAC/waiting bookings to confirmed.
     */
    public synchronized void assignSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null.");
        }
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("A cancelled booking cannot receive a seat.");
        }

        this.seat = seat;
    }

    public synchronized void setStatus(BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Booking status cannot be null.");
        }

        if (status == BookingStatus.CONFIRMED && seat == null) {
            throw new IllegalStateException(
                    "A booking must have a seat before becoming confirmed."
            );
        }

        if (status != BookingStatus.CONFIRMED) {
            seat = null;
        }

        this.status = status;
    }
}