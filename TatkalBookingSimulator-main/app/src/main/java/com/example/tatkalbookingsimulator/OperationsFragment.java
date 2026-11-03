package com.example.tatkalbookingsimulator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.BookingStatus;
import com.example.tatkalbookingsimulator.model.Train;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Booking report, cancellation and multi-thread race-condition demonstration. */
public class OperationsFragment extends Fragment {
    private LinearLayout report;
    private EditText bookingIdInput, trainNumberInput, threadCountInput;
    private TextView testResult;
    private final int navy = Color.rgb(15,35,75);
    private final int blue = Color.rgb(25,95,190);

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        ScrollView scroll = new ScrollView(requireContext());
        LinearLayout root = new LinearLayout(requireContext()); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18),dp(22),dp(18),dp(28)); root.setBackgroundColor(Color.rgb(245,248,253)); scroll.addView(root);
        TextView title = label("Booking operations", 27, true); root.addView(title);
        TextView sub = label("View the booking report, cancel a booking, or launch competing threads.",14,false); root.addView(sub);

        root.addView(label("CANCEL BOOKING",13,true));
        bookingIdInput = input("PNR / Booking ID (example: PNR1001)", InputType.TYPE_CLASS_TEXT); root.addView(bookingIdInput);
        Button cancel = button("Cancel booking and promote next passenger"); root.addView(cancel);
        cancel.setOnClickListener(v -> {
            String id = bookingIdInput.getText().toString().trim();
            Booking booking = SimulatorData.BOOKING_ENGINE.findBooking(id);
            if (booking == null) { toast("Booking ID not found."); return; }
            if (booking.getStatus() == BookingStatus.CANCELLED) { toast("This booking is already cancelled."); return; }
            Booking promoted = SimulatorData.BOOKING_ENGINE.cancelBooking(booking);
            toast(promoted == null ? "Booking cancelled." : "Cancelled. " + promoted.getPassenger().getName() + " promoted to " + promoted.getStatus() + ".");
            refreshReport();
        });

        root.addView(label("MULTITHREADING / RACE CONDITION TEST",13,true));
        trainNumberInput = input("Train number (12001 / 12002 / 12701)", InputType.TYPE_CLASS_TEXT); root.addView(trainNumberInput);
        threadCountInput = input("Number of concurrent threads (e.g. 25)", InputType.TYPE_CLASS_NUMBER); threadCountInput.setText("25"); root.addView(threadCountInput);
        Button run = button("Run concurrent booking test"); root.addView(run);
        testResult = label("The test result will appear here.",14,false); testResult.setPadding(0,dp(8),0,dp(12)); root.addView(testResult);
        run.setOnClickListener(v -> runConcurrency());

        root.addView(label("BOOKING REPORT",18,true));
        Button refresh = button("Refresh booking report"); root.addView(refresh); refresh.setOnClickListener(v -> refreshReport());
        Button export = button("Export report to text file"); root.addView(export);
        export.setOnClickListener(v -> exportReport());
        report = new LinearLayout(requireContext()); report.setOrientation(LinearLayout.VERTICAL); root.addView(report);
        Button home = button("Back to train search"); root.addView(home); home.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_OperationsFragment_to_FirstFragment));
        refreshReport();
        return scroll;
    }

    private void runConcurrency() {
        String number = trainNumberInput.getText().toString().trim();
        Train train = SimulatorData.REPOSITORY.findTrainByNumber(number);
        if (train == null) { trainNumberInput.setError("Enter a valid sample train number"); return; }
        int count;
        try { count = Integer.parseInt(threadCountInput.getText().toString().trim()); }
        catch (Exception e) { threadCountInput.setError("Enter 1 to 200"); return; }
        if (count < 1 || count > 200) { threadCountInput.setError("Use 1 to 200 threads"); return; }
        testResult.setText("Running " + count + " competing threads…");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = SimulatorData.BOOKING_ENGINE.runConcurrencyDemo(train, count);
            if (isAdded()) requireActivity().runOnUiThread(() -> { testResult.setText(result); refreshReport(); });
            executor.shutdown();
        });
    }


    private void exportReport() {
        try {
            StringBuilder text = new StringBuilder("IRCTC TATKAL BOOKING SIMULATOR REPORT\n\n");
            for (Booking b : SimulatorData.BOOKING_ENGINE.getAllBookings()) {
                text.append("PNR: ").append(b.getBookingId()).append("\n")
                        .append("Passenger: ").append(b.getPassenger().getName()).append("\n")
                        .append("Train: ").append(b.getTrain().getTrainNumber()).append(" - ").append(b.getTrain().getTrainName()).append("\n")
                        .append("Route: ").append(b.getTrain().getSource()).append(" to ").append(b.getTrain().getDestination()).append("\n")
                        .append("Status: ").append(b.getStatus()).append("\n")
                        .append("Seat: ").append(b.getSeat() == null ? "Not assigned" : b.getSeat().getSeatNumber()).append("\n\n");
            }
            try (java.io.FileOutputStream out = requireContext().openFileOutput("tatkal_booking_report.txt", android.content.Context.MODE_PRIVATE)) {
                out.write(text.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            toast("Report saved to app internal storage: tatkal_booking_report.txt");
        } catch (java.io.IOException e) { toast("Could not export report: " + e.getMessage()); }
    }

    private void refreshReport() {
        if (report == null) return;
        report.removeAllViews();

        List<Booking> bookings = SimulatorData.BOOKING_ENGINE.getAllBookings();
        report.addView(label("In-memory session report", 16, true));
        if (bookings.isEmpty()) {
            report.addView(label("No active-session bookings. Saved records may still exist below.",14,false));
        } else {
            for (int i = bookings.size() - 1; i >= 0; i--) {
                Booking b = bookings.get(i);
                String seat = b.getSeat() == null ? "Not assigned" : String.valueOf(b.getSeat().getSeatNumber());
                TextView item = label(b.getBookingId() + "  •  " + b.getStatus()
                        + "\\n" + b.getPassenger().getName() + " (" + b.getPassenger().getAge() + ")"
                        + "\\n" + b.getTrain().getTrainNumber() + " " + b.getTrain().getTrainName()
                        + "\\nSeat: " + seat, 14, false);
                item.setPadding(dp(12),dp(12),dp(12),dp(12));
                item.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
                lp.bottomMargin = dp(8);
                report.addView(item,lp);
            }
        }

        report.addView(label("Persistent SQLite history (" +
                SimulatorData.getDatabase().getBookingCount() + " records)", 16, true));
        List<String> savedRows = SimulatorData.getDatabase().getBookingHistory();
        if (savedRows.isEmpty()) {
            report.addView(label("No records saved in SQLite yet.",14,false));
        } else {
            for (String row : savedRows) {
                TextView item = label(row, 14, false);
                item.setPadding(dp(12),dp(12),dp(12),dp(12));
                item.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
                lp.bottomMargin = dp(8);
                report.addView(item,lp);
            }
        }
    }
    private TextView label(String text,int size,boolean bold) { TextView t = new TextView(requireContext()); t.setText(text); t.setTextColor(navy); t.setTextSize(size); if(bold)t.setTypeface(null,Typeface.BOLD); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.bottomMargin=dp(10); t.setLayoutParams(p); return t; }
    private EditText input(String hint,int type) { EditText e = new EditText(requireContext()); e.setSingleLine(true); e.setHint(hint); e.setInputType(type); e.setTextSize(15); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52)); p.bottomMargin=dp(8); e.setLayoutParams(p); return e; }
    private Button button(String text) { Button b=new Button(requireContext()); b.setText(text); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.bottomMargin=dp(16); b.setLayoutParams(p); return b; }
    private void toast(String s) { Toast.makeText(requireContext(),s,Toast.LENGTH_LONG).show(); }
    private int dp(int n) { return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
}
