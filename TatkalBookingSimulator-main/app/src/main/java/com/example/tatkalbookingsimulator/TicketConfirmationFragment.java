package com.example.tatkalbookingsimulator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class TicketConfirmationFragment extends Fragment {

    private final int navy = Color.rgb(15, 35, 75);
    private final int blue = Color.rgb(25, 95, 190);
    private final int green = Color.rgb(28, 130, 85);
    private final int background = Color.rgb(245, 248, 253);

    public TicketConfirmationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        String bookingId = getArg(args, "bookingId", "Not available");
        String passengerName = getArg(args, "passengerName", "Not provided");
        String age = getArg(args, "age", "Not provided");
        String gender = getArg(args, "gender", "Not provided");
        String trainName = getArg(args, "trainName", "Sample train");
        String trainNumber = getArg(args, "trainNumber", "Not available");
        String journeyDate = getArg(args, "journeyDate", "Not provided");
        String status = getArg(args, "status", "CONFIRMED");
        String seatNumber = getArg(args, "seatNumber", "Waiting list");
        String route = getArg(args, "route", "Route not available");
        String departure = getArg(args, "departure", "");
       String bookingDate = getArg(args, "bookingDate", "Not provided");
        String arrival = getArg(args, "arrival", "");

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(background);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(28));
        scrollView.addView(root);

        TextView eyebrow = new TextView(requireContext());
        eyebrow.setText("TATKAL  •  E-TICKET");
        eyebrow.setTextColor(blue);
        eyebrow.setTextSize(12);
        eyebrow.setTypeface(null, Typeface.BOLD);
        root.addView(eyebrow);

        TextView heading = new TextView(requireContext());
        heading.setText("Booking result");
        heading.setTextColor(navy);
        heading.setTextSize(27);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams = matchWrap();
        headingParams.topMargin = dp(8);
        root.addView(heading, headingParams);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("Your demo ticket details are shown below.");
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setTextSize(14);

        LinearLayout.LayoutParams subtitleParams = matchWrap();
        subtitleParams.topMargin = dp(6);
        subtitleParams.bottomMargin = dp(20);
        root.addView(subtitle, subtitleParams);

        LinearLayout ticket = new LinearLayout(requireContext());
        ticket.setOrientation(LinearLayout.VERTICAL);
        ticket.setPadding(dp(18), dp(18), dp(18), dp(18));
        ticket.setBackground(rounded(Color.WHITE, 16));

        LinearLayout.LayoutParams ticketParams = matchWrap();
        root.addView(ticket, ticketParams);

        TextView statusTitle = new TextView(requireContext());
        statusTitle.setText("BOOKING STATUS");
        statusTitle.setTextColor(Color.GRAY);
        statusTitle.setTextSize(12);
        statusTitle.setTypeface(null, Typeface.BOLD);
        ticket.addView(statusTitle);

        TextView statusValue = new TextView(requireContext());
        statusValue.setText(status);
        statusValue.setTextColor(
                "CONFIRMED".equalsIgnoreCase(status) ? green : blue
        );
        statusValue.setTextSize(21);
        statusValue.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams statusParams = matchWrap();
        statusParams.topMargin = dp(4);
        statusParams.bottomMargin = dp(16);
        ticket.addView(statusValue, statusParams);

        addDivider(ticket);

        addDetail(ticket, "PNR / Booking ID", bookingId);
        addDetail(ticket, "Passenger", passengerName);
        addDetail(ticket, "Age and gender", age + " • " + gender);

        addDivider(ticket);

        addDetail(ticket, "Train", trainName);
        addDetail(ticket, "Train number", trainNumber);
        addDetail(ticket, "Route", route);

        String timeText = departure.isEmpty() && arrival.isEmpty()
                ? "Not provided"
                : departure + " - " + arrival;
        addDetail(ticket, "Departure - arrival", timeText);
        addDetail(ticket, "Journey date", journeyDate);
       addDetail(ticket, "Booked on", bookingDate);
        addDetail(ticket, "Seat", seatNumber);

        TextView disclaimer = new TextView(requireContext());
        disclaimer.setText(
                "DEMO TICKET ONLY\n" +
                        "This is a sample booking confirmation from your simulator. " +
                        "It is not a real IRCTC ticket and cannot be used for travel."
        );
        disclaimer.setTextColor(Color.GRAY);
        disclaimer.setTextSize(12);
        disclaimer.setGravity(Gravity.CENTER);
        disclaimer.setPadding(dp(8), dp(20), dp(8), dp(12));

        LinearLayout.LayoutParams disclaimerParams = matchWrap();
        disclaimerParams.topMargin = dp(8);
        root.addView(disclaimer, disclaimerParams);

        Button homeButton = new Button(requireContext());
        homeButton.setText("Back to Home");
        homeButton.setAllCaps(false);
        homeButton.setTextColor(Color.WHITE);
        homeButton.setBackground(rounded(blue, 12));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        buttonParams.topMargin = dp(12);
        root.addView(homeButton, buttonParams);

        homeButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_TicketConfirmationFragment_to_FirstFragment);
        });

        return scrollView;
    }

    private String getArg(Bundle args, String key, String fallback) {
        if (args == null) {
            return fallback;
        }
        String value = args.getString(key);
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private void addDetail(LinearLayout parent, String labelText, String valueText) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(10), 0, dp(10));

        TextView label = new TextView(requireContext());
        label.setText(labelText);
        label.setTextColor(Color.GRAY);
        label.setTextSize(12);
        row.addView(label);

        TextView value = new TextView(requireContext());
        value.setText(valueText);
        value.setTextColor(navy);
        value.setTextSize(16);
        value.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams valueParams = matchWrap();
        valueParams.topMargin = dp(3);
        row.addView(value, valueParams);

        parent.addView(row);
    }

    private void addDivider(LinearLayout parent) {
        View divider = new View(requireContext());
        divider.setBackgroundColor(Color.rgb(225, 230, 238));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        params.topMargin = dp(4);
        params.bottomMargin = dp(4);
        parent.addView(divider, params);
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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