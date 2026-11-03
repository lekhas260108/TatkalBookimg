
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

import java.util.ArrayList;

public class SecondFragment extends Fragment {

    private final int navy = Color.rgb(15, 35, 75);
    private final int blue = Color.rgb(25, 95, 190);
    private final int green = Color.rgb(28, 130, 85);

    public SecondFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(245, 248, 253));

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(24));
        scrollView.addView(root);

        TextView eyebrow = new TextView(requireContext());
        eyebrow.setText("TATKAL  •  DEMO RESULTS");
        eyebrow.setTextColor(blue);
        eyebrow.setTextSize(12);
        eyebrow.setTypeface(null, Typeface.BOLD);
        root.addView(eyebrow);

        TextView heading = new TextView(requireContext());
        heading.setText("Available trains");
        heading.setTextColor(navy);
        heading.setTextSize(28);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        headingParams.topMargin = dp(8);
        root.addView(heading, headingParams);

        String journeyDate = "";
        ArrayList<String> trainDetails = null;

        Bundle args = getArguments();

        if (args != null) {
            journeyDate = args.getString("journeyDate", "");
            trainDetails = args.getStringArrayList("trainDetails");
        }

        TextView subtitle = new TextView(requireContext());
        subtitle.setText(
                journeyDate.isEmpty()
                        ? "Choose a train for your demo booking."
                        : "Journey date: " + journeyDate
        );
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setTextSize(14);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(6);
        subtitleParams.bottomMargin = dp(20);
        root.addView(subtitle, subtitleParams);

        if (trainDetails == null || trainDetails.isEmpty()) {
            TextView emptyMessage = new TextView(requireContext());
            emptyMessage.setText(
                    "No trains to display.\nPlease return and search a route."
            );
            emptyMessage.setTextColor(Color.DKGRAY);
            emptyMessage.setTextSize(16);
            emptyMessage.setPadding(dp(8), dp(24), dp(8), dp(24));
            root.addView(emptyMessage);
        } else {
            for (String details : trainDetails) {
                addTrainCard(root, details, journeyDate);
            }
        }

        TextView note = new TextView(requireContext());
        note.setText(
                "DEMO PROJECT\nTrain schedules and seat counts are sample data. "
                        + "This app does not make live IRCTC bookings."
        );
        note.setTextColor(Color.GRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(dp(8), dp(18), dp(8), dp(8));
        root.addView(note);

        return scrollView;
    }

    private void addTrainCard(
            LinearLayout parent,
            String details,
            String journeyDate) {

        String[] lines = details.split("\n");

        String trainName = lines.length > 0
                ? lines[0] : "Train";

        String route = lines.length > 1
                ? lines[1] : "";

        String times = lines.length > 2
                ? lines[2] : "";

        String availability = lines.length > 3
                ? lines[3] : "Availability not provided";

        // The first line is formatted as: train number - train name
        String trainNumber = "";
        if (lines.length > 0) {
            String[] trainParts = lines[0].split(" - ", 2);
            trainNumber = trainParts[0].trim();
        }

        final String selectedTrainNumber = trainNumber;

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(rounded(Color.WHITE, 16));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(16);
        parent.addView(card, cardParams);

        TextView name = new TextView(requireContext());
        name.setText(trainName);
        name.setTextColor(navy);
        name.setTextSize(17);
        name.setTypeface(null, Typeface.BOLD);
        card.addView(name);

        TextView routeText = new TextView(requireContext());
        routeText.setText(route);
        routeText.setTextColor(Color.DKGRAY);
        routeText.setTextSize(14);

        LinearLayout.LayoutParams routeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        routeParams.topMargin = dp(10);
        card.addView(routeText, routeParams);

        TextView timeText = new TextView(requireContext());
        timeText.setText(times);
        timeText.setTextColor(navy);
        timeText.setTextSize(17);
        timeText.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.topMargin = dp(10);
        card.addView(timeText, timeParams);

        TextView seats = new TextView(requireContext());
        seats.setText(availability);
        seats.setTextColor(green);
        seats.setTextSize(13);
        seats.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams seatsParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        seatsParams.topMargin = dp(8);
        card.addView(seats, seatsParams);

        Button bookButton = new Button(requireContext());
        bookButton.setText("Book Tatkal");
        bookButton.setTextColor(Color.WHITE);
        bookButton.setAllCaps(false);
        bookButton.setBackground(rounded(blue, 12));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(48));
        buttonParams.topMargin = dp(12);
        card.addView(bookButton, buttonParams);

        bookButton.setOnClickListener(v -> {
            Bundle passengerArgs = new Bundle();
            passengerArgs.putString("trainNumber", selectedTrainNumber);
            passengerArgs.putString("journeyDate", journeyDate);

            Navigation.findNavController(v).navigate(
                    R.id.action_SecondFragment_to_PassengerFragment,
                    passengerArgs
            );
        });
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
