
package com.example.tatkalbookingsimulator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.tatkalbookingsimulator.model.Booking;
import com.example.tatkalbookingsimulator.model.BookingStatus;
import com.example.tatkalbookingsimulator.model.Passenger;
import com.example.tatkalbookingsimulator.model.Train;
import com.example.tatkalbookingsimulator.exception.SeatNotAvailableException;

public class PassengerFragment extends Fragment {

    private final int navy = Color.rgb(15, 35, 75);
    private final int blue = Color.rgb(25, 95, 190);
    private final int background = Color.rgb(245, 248, 253);

    private EditText nameInput;
    private EditText ageInput;
    private RadioGroup genderGroup;

    private String trainNumber = "";
    private String journeyDate = "";

    public PassengerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        if (args != null) {
            trainNumber = args.getString("trainNumber", "");
            journeyDate = args.getString("journeyDate", "");
        }

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(background);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(28), dp(22), dp(28));
        scrollView.addView(root);

        TextView eyebrow = new TextView(requireContext());
        eyebrow.setText("TATKAL  •  PASSENGER");
        eyebrow.setTextColor(blue);
        eyebrow.setTextSize(12);
        eyebrow.setTypeface(null, Typeface.BOLD);
        root.addView(eyebrow);

        TextView heading = new TextView(requireContext());
        heading.setText("Passenger details");
        heading.setTextColor(navy);
        heading.setTextSize(28);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        headingParams.topMargin = dp(8);
        root.addView(heading, headingParams);

        TextView trainInfo = new TextView(requireContext());
        trainInfo.setText(
                "Selected train: "
                        + (trainNumber.isEmpty()
                        ? "Not provided" : trainNumber)
                        + "\nJourney date: "
                        + (journeyDate.isEmpty()
                        ? "Not provided" : journeyDate)
        );
        trainInfo.setTextColor(Color.DKGRAY);
        trainInfo.setTextSize(15);
        trainInfo.setPadding(dp(14), dp(14), dp(14), dp(14));
        trainInfo.setBackground(rounded(Color.WHITE, 12));

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        infoParams.topMargin = dp(20);
        infoParams.bottomMargin = dp(24);
        root.addView(trainInfo, infoParams);

        addLabel(root, "Full name");

        nameInput = new EditText(requireContext());
        nameInput.setSingleLine(true);
        nameInput.setHint("Enter passenger name");
        nameInput.setTextSize(16);
        nameInput.setPadding(dp(12), dp(12), dp(12), dp(12));
        nameInput.setBackground(rounded(Color.WHITE, 10));
        root.addView(nameInput, fieldParams());

        addLabel(root, "Age");

        ageInput = new EditText(requireContext());
        ageInput.setSingleLine(true);
        ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        ageInput.setHint("Enter age");
        ageInput.setTextSize(16);
        ageInput.setPadding(dp(12), dp(12), dp(12), dp(12));
        ageInput.setBackground(rounded(Color.WHITE, 10));
        root.addView(ageInput, fieldParams());

        addLabel(root, "Gender");

        genderGroup = new RadioGroup(requireContext());
        genderGroup.setOrientation(RadioGroup.HORIZONTAL);
        genderGroup.setPadding(dp(4), dp(8), dp(4), dp(8));

        RadioButton male = new RadioButton(requireContext());
        male.setText("Male");
        male.setTextColor(navy);
        male.setId(View.generateViewId());
        genderGroup.addView(male);

        RadioButton female = new RadioButton(requireContext());
        female.setText("Female");
        female.setTextColor(navy);
        female.setId(View.generateViewId());
        genderGroup.addView(female);

        RadioButton other = new RadioButton(requireContext());
        other.setText("Other");
        other.setTextColor(navy);
        other.setId(View.generateViewId());
        genderGroup.addView(other);

        root.addView(genderGroup);

        Button continueButton = new Button(requireContext());
        continueButton.setText("Continue to Booking");
        continueButton.setAllCaps(false);
        continueButton.setTextColor(Color.WHITE);
        continueButton.setTextSize(16);
        continueButton.setBackground(rounded(blue, 12));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52));

        buttonParams.topMargin = dp(28);
        root.addView(continueButton, buttonParams);

        TextView note = new TextView(requireContext());
        note.setText(
                "Demo simulator only. No real railway reservation will be made."
        );
        note.setTextColor(Color.GRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(dp(4), dp(20), dp(4), dp(4));
        root.addView(note);

        continueButton.setOnClickListener(v -> validatePassenger());

        return scrollView;
    }

    private void validatePassenger() {

        String name = nameInput.getText().toString().trim();
        String ageText = ageInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Please enter passenger name");
            nameInput.requestFocus();
            return;
        }

        if (ageText.isEmpty()) {
            ageInput.setError("Please enter age");
            ageInput.requestFocus();
            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            ageInput.setError("Enter a valid age");
            return;
        }

        if (age < 1 || age > 120) {
            ageInput.setError("Age must be between 1 and 120");
            return;
        }

        int selectedGenderId = genderGroup.getCheckedRadioButtonId();

        if (selectedGenderId == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "No train selected. Please search again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        RadioButton selectedGender =
                genderGroup.findViewById(selectedGenderId);

        String gender = selectedGender.getText().toString();

        // Find the selected train from the shared repository.
        Train train = SimulatorData.REPOSITORY
                .findTrainByNumber(trainNumber);

        if (train == null) {
            Toast.makeText(
                    requireContext(),
                    "Selected train not found. Please search again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Create the passenger.
        Passenger passenger = new Passenger(
                "P" + System.currentTimeMillis(),
                name,
                age,
                gender
        );

        try {
            // Perform the booking using the shared booking engine.
            Booking booking = SimulatorData.BOOKING_ENGINE
                    .bookTicket(passenger, train);
            // Store the journey date alongside the booking in SQLite.
            SimulatorData.getDatabase().saveBooking(booking, journeyDate);

            Bundle ticketArgs = new Bundle();
            ticketArgs.putString("bookingId", booking.getBookingId());
            ticketArgs.putString("passengerName", passenger.getName());
            ticketArgs.putString("age", String.valueOf(passenger.getAge()));
            ticketArgs.putString("gender", passenger.getGender());
            ticketArgs.putString("trainName", train.getTrainName());
            ticketArgs.putString("trainNumber", train.getTrainNumber());
            ticketArgs.putString("journeyDate", journeyDate);
            ticketArgs.putString("status", booking.getStatus().name());
            ticketArgs.putString("seatNumber", booking.getSeat() == null
                    ? (booking.getStatus() == BookingStatus.RAC ? "RAC" : "Waiting list")
                    : String.valueOf(booking.getSeat().getSeatNumber()));
            ticketArgs.putString("route", train.getSource() + " to " + train.getDestination());
            ticketArgs.putString("departure", train.getDepartureTime());
            ticketArgs.putString("arrival", train.getArrivalTime());
            ticketArgs.putString("bookingDate", new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
            androidx.navigation.Navigation.findNavController(requireView()).navigate(
                    R.id.action_PassengerFragment_to_TicketConfirmationFragment, ticketArgs);

        } catch (SeatNotAvailableException e) {
            Toast.makeText(
                    requireContext(),
                    "Booking failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void addLabel(LinearLayout parent, String text) {

        TextView label = new TextView(requireContext());
        label.setText(text);
        label.setTextColor(navy);
        label.setTextSize(15);
        label.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        params.topMargin = dp(12);
        params.bottomMargin = dp(6);
        parent.addView(label, params);
    }

    private LinearLayout.LayoutParams fieldParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52));

        params.bottomMargin = dp(8);
        return params;
    }

    private GradientDrawable rounded(int color, int radius) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }
}
