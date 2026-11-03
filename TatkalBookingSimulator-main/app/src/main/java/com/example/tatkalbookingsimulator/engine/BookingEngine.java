package com.example.tatkalbookingsimulator.engine;

import com.example.tatkalbookingsimulator.exception.SeatNotAvailableException;
import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.BookingStatus;
import com.example.tatkalbookingsimulator.model.Coach;
import com.example.tatkalbookingsimulator.model.Passenger;
import com.example.tatkalbookingsimulator.model.Seat;
import com.example.tatkalbookingsimulator.model.Train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe in-memory booking engine.
 *
 * Demo policy:
 * 1. Allocate available seats as CONFIRMED.
 * 2. Once seats are exhausted, allow up to five RAC bookings per train.
 * 3. Further bookings enter the FIFO WAITING list.
 *
 * This is a simulator policy, not a claim about live railway allocation rules.
 */
public class BookingEngine {

    private static final int RAC_LIMIT_PER_TRAIN = 5;

    private final WaitingList waitingList = new WaitingList();
    private final List<Booking> allBookings = new ArrayList<>();
    private final List<Booking> racBookings = new ArrayList<>();
    private final AtomicInteger bookingCounter = new AtomicInteger(1000);

    /**
     * The whole allocation operation is synchronized so competing threads
     * using this engine cannot allocate the same available seat.
     */
    public synchronized Booking bookTicket(
            Passenger passenger,
            Train train
    ) throws SeatNotAvailableException {

        if (passenger == null || train == null) {
            throw new IllegalArgumentException(
                    "Passenger and train are required."
            );
        }

        Seat availableSeat = findAndBookSeat(train);
        String bookingId = "PNR" + bookingCounter.incrementAndGet();

        final Booking booking;

        if (availableSeat != null) {
            booking = new Booking(
                    bookingId,
                    passenger,
                    train,
                    availableSeat,
                    BookingStatus.CONFIRMED
            );
        } else if (countActiveRac(train) < RAC_LIMIT_PER_TRAIN) {
            booking = new Booking(
                    bookingId,
                    passenger,
                    train,
                    null,
                    BookingStatus.RAC
            );
            racBookings.add(booking);
        } else {
            booking = new Booking(
                    bookingId,
                    passenger,
                    train,
                    null,
                    BookingStatus.WAITING
            );
            waitingList.add(booking);
        }

        allBookings.add(booking);
        return booking;
    }

    /**
     * Finds and atomically reserves the first available seat.
     */
    private Seat findAndBookSeat(Train train) {
        for (Coach coach : train.getCoaches()) {
            for (Seat seat : coach.getSeats()) {
                if (seat.book()) {
                    return seat;
                }
            }
        }
        return null;
    }

    private int countActiveRac(Train train) {
        int count = 0;

        for (Booking booking : racBookings) {
            if (booking.getTrain() == train
                    && booking.getStatus() == BookingStatus.RAC) {
                count++;
            }
        }

        return count;
    }

    /**
     * Cancels a booking.
     *
     * If a confirmed booking is cancelled:
     * - release its seat;
     * - promote the oldest RAC passenger, if any;
     * - otherwise promote the oldest waiting passenger;
     * - if a waiting passenger takes the released seat directly,
     *   move the next waiting passenger to RAC if capacity permits.
     *
     * If an RAC booking is cancelled, the next waiting passenger is promoted
     * to RAC. Cancelling a waiting booking simply removes it from the queue.
     *
     * Returns the passenger promoted by the operation, or null if no one
     * was promoted.
     */
    public synchronized Booking cancelBooking(Booking booking) {
        if (booking == null || !allBookings.contains(booking)) {
            return null;
        }

        BookingStatus oldStatus = booking.getStatus();

        if (oldStatus == BookingStatus.CANCELLED) {
            return null;
        }

        Train train = booking.getTrain();

        if (oldStatus == BookingStatus.WAITING) {
            waitingList.removeBooking(booking);
            booking.setStatus(BookingStatus.CANCELLED);
            return null;
        }

        if (oldStatus == BookingStatus.RAC) {
            racBookings.remove(booking);
            booking.setStatus(BookingStatus.CANCELLED);

            Booking nextWaiting = waitingList.removeNextForTrain(train);
            if (nextWaiting != null
                    && countActiveRac(train) < RAC_LIMIT_PER_TRAIN) {
                nextWaiting.setStatus(BookingStatus.RAC);
                racBookings.add(nextWaiting);
                return nextWaiting;
            }

            return null;
        }

        if (oldStatus != BookingStatus.CONFIRMED) {
            return null;
        }

        // Capture and release the seat before changing the booking status.
        Seat releasedSeat = booking.getSeat();
        if (releasedSeat != null) {
            releasedSeat.release();
        }
        booking.setStatus(BookingStatus.CANCELLED);

        // RAC passengers have priority for the newly available seat.
        Booking promoted = removeNextRac(train);

        // If there is no RAC passenger, promote the oldest waiting passenger.
        if (promoted == null) {
            promoted = waitingList.removeNextForTrain(train);
        }

        if (promoted == null) {
            return null;
        }

        Seat newSeat = findAndBookSeat(train);

        if (newSeat == null) {
            // Defensive recovery if the seat is no longer available.
            if (promoted.getStatus() == BookingStatus.RAC) {
                racBookings.add(promoted);
            } else if (promoted.getStatus() == BookingStatus.WAITING) {
                waitingList.add(promoted);
            }
            return null;
        }

        BookingStatus promotedFrom = promoted.getStatus();
        promoted.assignSeat(newSeat);
        promoted.setStatus(BookingStatus.CONFIRMED);

        if (promotedFrom == BookingStatus.RAC) {
            racBookings.remove(promoted);
        }

        // A waiting passenger may now occupy the first RAC slot.
        if (countActiveRac(train) < RAC_LIMIT_PER_TRAIN) {
            Booking nextWaiting = waitingList.removeNextForTrain(train);

            if (nextWaiting != null) {
                nextWaiting.setStatus(BookingStatus.RAC);
                racBookings.add(nextWaiting);
            }
        }

        return promoted;
    }

    /**
     * Removes and returns the oldest active RAC booking for a train.
     * Uses an iterator to avoid modifying the list inside an enhanced loop.
     */
    private Booking removeNextRac(Train train) {
        for (int i = 0; i < racBookings.size(); i++) {
            Booking booking = racBookings.get(i);

            if (booking.getTrain() == train
                    && booking.getStatus() == BookingStatus.RAC) {
                racBookings.remove(i);
                return booking;
            }
        }

        return null;
    }

    public synchronized Booking findBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }

        String searchId = bookingId.trim();

        for (Booking booking : allBookings) {
            if (booking.getBookingId().equalsIgnoreCase(searchId)) {
                return booking;
            }
        }

        return null;
    }

    public synchronized List<Booking> getAllBookings() {
        return Collections.unmodifiableList(
                new ArrayList<>(allBookings)
        );
    }

    public synchronized int getWaitingListSize() {
        return waitingList.size();
    }

    public synchronized int getRacBookingCount() {
        int count = 0;

        for (Booking booking : racBookings) {
            if (booking.getStatus() == BookingStatus.RAC) {
                count++;
            }
        }

        return count;
    }

    public synchronized int getConfirmedBookingCount() {
        int count = 0;

        for (Booking booking : allBookings) {
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                count++;
            }
        }

        return count;
    }

    /**
     * Starts real competing threads that share this same engine and train.
     * Each thread waits at a start gate to increase contention.
     */
    public String runConcurrencyDemo(final Train train, int attempts) {
        if (train == null || attempts < 1) {
            return "Choose a train and a positive number of threads.";
        }

        final CountDownLatch ready = new CountDownLatch(attempts);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch finished = new CountDownLatch(attempts);

        final AtomicInteger confirmed = new AtomicInteger();
        final AtomicInteger rac = new AtomicInteger();
        final AtomicInteger waiting = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();
        final AtomicReference<String> firstError = new AtomicReference<>();

        for (int i = 0; i < attempts; i++) {
            final int index = i;

            Thread thread = new Thread(() -> {
                try {
                    Passenger passenger = new Passenger(
                            "THREAD-" + System.nanoTime() + "-" + index,
                            "Concurrent User " + index,
                            25,
                            "Other"
                    );

                    ready.countDown();
                    start.await();

                    Booking booking = bookTicket(passenger, train);

                    if (booking.getStatus() == BookingStatus.CONFIRMED) {
                        confirmed.incrementAndGet();
                    } else if (booking.getStatus() == BookingStatus.RAC) {
                        rac.incrementAndGet();
                    } else if (booking.getStatus() == BookingStatus.WAITING) {
                        waiting.incrementAndGet();
                    }
                } catch (Exception exception) {
                    failed.incrementAndGet();
                    firstError.compareAndSet(
                            null,
                            exception.getClass().getSimpleName()
                                    + ": " + exception.getMessage()
                    );
                } finally {
                    // Also count down if a thread fails before reaching the gate.
                    finished.countDown();
                }
            }, "TatkalBooking-" + index);

            thread.start();
        }

        try {
            ready.await();
            start.countDown();
            finished.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return "Concurrency test interrupted. Please run it again.";
        }

        return "CONCURRENT BOOKING TEST\n"
                + "Threads requested: " + attempts + "\n"
                + "New confirmed: " + confirmed.get() + "\n"
                + "New RAC: " + rac.get() + "\n"
                + "New waiting-list: " + waiting.get() + "\n"
                + "Failed attempts: " + failed.get()
                + (firstError.get() == null
                ? ""
                : "\nFirst error: " + firstError.get())
                + "\n\nThe engine serializes booking decisions, and each seat "
                + "also protects its own allocation.";
    }
}