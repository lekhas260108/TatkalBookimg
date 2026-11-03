package com.example.tatkalbookingsimulator.model;

public class Ticket {

    private final String pnrNumber;
    private final Booking booking;
    private final String bookingDate;

    public Ticket(String pnrNumber, Booking booking, String bookingDate) {
        if (pnrNumber == null || pnrNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("PNR number is required.");
        }
        if (booking == null) {
            throw new IllegalArgumentException("Booking is required.");
        }

        this.pnrNumber = pnrNumber.trim();
        this.booking = booking;
        this.bookingDate = bookingDate == null ? "" : bookingDate.trim();
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String generateTicketSummary() {
        Passenger passenger = booking.getPassenger();
        Train train = booking.getTrain();
        Seat seat = booking.getSeat();

        String seatText = seat == null
                ? "Not assigned"
                : String.valueOf(seat.getSeatNumber());

        return "===== TATKAL DEMO E-TICKET =====\n"
                + "PNR: " + pnrNumber + "\n"
                + "Passenger: " + passenger.getName() + "\n"
                + "Age: " + passenger.getAge() + "\n"
                + "Gender: " + passenger.getGender() + "\n"
                + "Train: " + train.getTrainNumber()
                + " - " + train.getTrainName() + "\n"
                + "Route: " + train.getSource()
                + " to " + train.getDestination() + "\n"
                + "Departure: " + train.getDepartureTime() + "\n"
                + "Arrival: " + train.getArrivalTime() + "\n"
                + "Status: " + booking.getStatus() + "\n"
                + "Seat: " + seatText + "\n"
                + "Booking date: " + bookingDate + "\n"
                + "================================\n"
                + "DEMO ONLY - NOT VALID FOR TRAVEL";
    }
}