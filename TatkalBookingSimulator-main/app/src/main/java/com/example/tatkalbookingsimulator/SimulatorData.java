package com.example.tatkalbookingsimulator;

import com.example.tatkalbookingsimulator.engine.BookingEngine;
import com.example.tatkalbookingsimulator.engine.TrainRepository;
import android.content.Context;

public final class SimulatorData {

    public static final TrainRepository REPOSITORY =
            new TrainRepository();

    public static final BookingEngine BOOKING_ENGINE =
            new BookingEngine();

    private static volatile BookingDatabase database;

    public static synchronized void initializeDatabase(Context context) {
        if (database == null) {
            database = BookingDatabase.getInstance(context.getApplicationContext());
        }
    }

    public static BookingDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database not initialized. Call SimulatorData.initializeDatabase() in MainActivity.");
        }
        return database;
    }

    private SimulatorData() {
        // Prevent creating extra instances.
    }
}
