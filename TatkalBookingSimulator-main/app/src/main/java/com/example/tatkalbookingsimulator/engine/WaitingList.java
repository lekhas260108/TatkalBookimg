package com.example.tatkalbookingsimulator.engine;

import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.BookingStatus;
import com.example.tatkalbookingsimulator.model.Train;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class WaitingList {

    private final Queue<Booking> queue = new LinkedList<>();

    public synchronized void add(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException(
                    "Only WAITING bookings can be added to the waiting list."
            );
        }
        if (!queue.contains(booking)) {
            queue.offer(booking);
        }
    }

    public synchronized Booking removeNext() {
        return queue.poll();
    }

    public synchronized Booking peekNext() {
        return queue.peek();
    }

    public synchronized boolean removeBooking(Booking booking) {
        return queue.remove(booking);
    }

    public synchronized Booking removeNextForTrain(Train train) {
        if (train == null) {
            return null;
        }

        Iterator<Booking> iterator = queue.iterator();

        while (iterator.hasNext()) {
            Booking booking = iterator.next();

            if (booking.getTrain() == train
                    && booking.getStatus() == BookingStatus.WAITING) {
                iterator.remove();
                return booking;
            }
        }

        return null;
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}