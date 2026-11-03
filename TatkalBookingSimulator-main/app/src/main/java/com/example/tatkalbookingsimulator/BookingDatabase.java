package com.example.tatkalbookingsimulator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.Seat;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite storage for the Tatkal booking simulator.
 * This stores simulator records only; it does not connect to IRCTC.
 */
public final class BookingDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tatkal_simulator.db";
    private static final int DATABASE_VERSION = 1;
    private static volatile BookingDatabase instance;

    public static synchronized BookingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new BookingDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private BookingDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE booking_records (" +
                "booking_id TEXT PRIMARY KEY NOT NULL," +
                "passenger_id TEXT NOT NULL," +
                "passenger_name TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "gender TEXT NOT NULL," +
                "train_number TEXT NOT NULL," +
                "train_name TEXT NOT NULL," +
                "source TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "departure_time TEXT," +
                "arrival_time TEXT," +
                "journey_date TEXT," +
                "booking_status TEXT NOT NULL," +
                "seat_number INTEGER," +
                "booked_at TEXT NOT NULL," +
                "updated_at TEXT NOT NULL" +
                ")");
        db.execSQL("CREATE INDEX idx_booking_train ON booking_records(train_number)");
        db.execSQL("CREATE INDEX idx_booking_status ON booking_records(booking_status)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Keep existing records when adding future schema versions.
        // Add explicit ALTER TABLE migrations here if the schema changes.
    }

    /** Insert or update one booking; cancellation and promotions update the same row. */
    public synchronized void saveBooking(Booking booking, String journeyDate) {
        if (booking == null) return;
        long now = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put("booking_id", booking.getBookingId());
        values.put("passenger_id", booking.getPassenger().getPassengerId());
        values.put("passenger_name", booking.getPassenger().getName());
        values.put("age", booking.getPassenger().getAge());
        values.put("gender", booking.getPassenger().getGender());
        values.put("train_number", booking.getTrain().getTrainNumber());
        values.put("train_name", booking.getTrain().getTrainName());
        values.put("source", booking.getTrain().getSource());
        values.put("destination", booking.getTrain().getDestination());
        values.put("departure_time", booking.getTrain().getDepartureTime());
        values.put("arrival_time", booking.getTrain().getArrivalTime());
        values.put("journey_date", journeyDate == null ? "" : journeyDate);
        values.put("booking_status", booking.getStatus().name());
        Seat seat = booking.getSeat();
        if (seat == null) values.putNull("seat_number");
        else values.put("seat_number", seat.getSeatNumber());

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT booked_at, journey_date FROM booking_records WHERE booking_id = ?",
                new String[]{booking.getBookingId()});
        String bookedAt = Long.toString(now);
        try {
            if (cursor.moveToFirst()) {
                bookedAt = cursor.getString(0);
                String oldJourneyDate = cursor.getString(1);
                if (journeyDate == null || journeyDate.trim().isEmpty()) {
                    values.put("journey_date", oldJourneyDate == null ? "" : oldJourneyDate);
                }
            }
        } finally {
            cursor.close();
        }
        values.put("booked_at", bookedAt);
        values.put("updated_at", Long.toString(now));
        db.insertWithOnConflict("booking_records", null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public synchronized List<String> getBookingHistory() {
        List<String> rows = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT booking_id, passenger_name, train_number, train_name, " +
                        "booking_status, seat_number, journey_date, booked_at " +
                        "FROM booking_records ORDER BY CAST(updated_at AS INTEGER) DESC", null)) {
            while (c.moveToNext()) {
                String seat = c.isNull(5) ? "Not assigned" : String.valueOf(c.getInt(5));
                rows.add("PNR: " + c.getString(0)
                        + "\nPassenger: " + c.getString(1)
                        + "\nTrain: " + c.getString(2) + " - " + c.getString(3)
                        + "\nStatus: " + c.getString(4)
                        + "\nSeat: " + seat
                        + "\nJourney date: " + c.getString(6));
            }
        }
        return rows;
    }

    public synchronized int getBookingCount() {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM booking_records", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }
}
