package com.example.tatkalbookingsimulator.engine;

import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.BookingStatus;
import com.example.tatkalbookingsimulator.model.Passenger;
import com.example.tatkalbookingsimulator.model.Train;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookingEngineTest {
    @Test public void fullTrainAllocatesConfirmedThenRacThenWaiting() throws Exception {
        Train train = new TrainRepository().findTrainByNumber("12001");
        BookingEngine engine = new BookingEngine();
        Booking last = null;
        for (int i=0;i<21;i++) last = engine.bookTicket(new Passenger("P"+i,"Passenger "+i,22,"Other"), train);
        assertNotNull(last);
        assertEquals(BookingStatus.WAITING, last.getStatus());
    }

    @Test public void cancellationPromotesRacPassengerToConfirmed() throws Exception {
        Train train = new TrainRepository().findTrainByNumber("12001");
        BookingEngine engine = new BookingEngine();
        Booking first = null;
        for (int i=0;i<16;i++) {
            Booking b = engine.bookTicket(new Passenger("P"+i,"Passenger "+i,22,"Other"), train);
            if (i == 0) first = b;
        }
        assertEquals(BookingStatus.RAC, engine.getAllBookings().get(15).getStatus());
        Booking promoted = engine.cancelBooking(first);
        assertNotNull(promoted);
        assertEquals(BookingStatus.CONFIRMED, promoted.getStatus());
        assertNotNull(promoted.getSeat());
    }
}
