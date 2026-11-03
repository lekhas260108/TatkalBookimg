
package com.example.tatkalbookingsimulator;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tatkalbookingsimulator.engine.TrainRepository;
import com.example.tatkalbookingsimulator.model.Train;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private EditText sourceInput;
    private EditText destinationInput;
    private EditText dateInput;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        int navy = Color.rgb(15, 35, 75);
        int blue = Color.rgb(25, 95, 190);
        int background = Color.rgb(245, 248, 253);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(24));
        root.setBackgroundColor(background);

        TextView brand = new TextView(requireContext());
        brand.setText("RAILWAY  •  TATKAL");
        brand.setTextColor(blue);
        brand.setTextSize(13);
        brand.setTypeface(null, Typeface.BOLD);
        root.addView(brand);

        TextView heading = new TextView(requireContext());
        heading.setText("Book your journey.");
        heading.setTextColor(navy);
        heading.setTextSize(29);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        headingParams.topMargin = dp(10);
        root.addView(heading, headingParams);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("Your next trip starts here.");
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setTextSize(15);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(5);
        subtitleParams.bottomMargin = dp(24);
        root.addView(subtitle, subtitleParams);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(rounded(Color.WHITE, 18));

        TextView cardTitle = new TextView(requireContext());
        cardTitle.setText("Find a train");
        cardTitle.setTextColor(navy);
        cardTitle.setTextSize(20);
        cardTitle.setTypeface(null, Typeface.BOLD);
        card.addView(cardTitle);

        sourceInput = createInput("From station (e.g. Mumbai)");
        destinationInput = createInput("To station (e.g. Pune)");
        dateInput = createInput("Journey date (DD/MM/YYYY)");

        addField(card, "FROM", sourceInput);
        addField(card, "TO", destinationInput);
        addField(card, "JOURNEY DATE", dateInput);

        Button searchButton = new Button(requireContext());
        searchButton.setText("Search trains");
        searchButton.setTextColor(Color.WHITE);
        searchButton.setAllCaps(false);
        searchButton.setTextSize(16);
        searchButton.setBackground(rounded(blue, 12));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(52));
        buttonParams.topMargin = dp(18);
        card.addView(searchButton, buttonParams);

        Button operationsButton = new Button(requireContext());
        operationsButton.setText("Booking Report / Cancel / Concurrency Test");
        operationsButton.setAllCaps(false);
        LinearLayout.LayoutParams operationsParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        operationsParams.topMargin = dp(10);
        card.addView(operationsButton, operationsParams);
        operationsButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_FirstFragment_to_OperationsFragment));

        searchButton.setOnClickListener(v -> {
            String from = sourceInput.getText().toString().trim();
            String to = destinationInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please enter the source, destination and date.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (from.equalsIgnoreCase(to)) {
                Toast.makeText(requireContext(),
                        "Source and destination must be different.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Search the sample train repository.
            TrainRepository repository = SimulatorData.REPOSITORY;
            List<Train> matches = repository.searchTrains(from, to);

            if (matches.isEmpty()) {
                Toast.makeText(requireContext(),
                        "No sample trains found for this route.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Pass the search results to the next screen.
            ArrayList<String> trainDetails = new ArrayList<>();

            for (Train train : matches) {
                trainDetails.add(
                        train.getTrainNumber() + " - "
                                + train.getTrainName() + "\n"
                                + train.getSource() + " to "
                                + train.getDestination() + "\n"
                                + train.getDepartureTime() + " - "
                                + train.getArrivalTime() + "\n"
                                + "Available seats: "
                                + train.getTotalAvailableSeats()
                );
            }

            Bundle bundle = new Bundle();
            bundle.putStringArrayList("trainDetails", trainDetails);
            bundle.putString("journeyDate", date);

            try {
                Navigation.findNavController(v).navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle
                );
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(),
                        "Navigation action not found. Check the navigation graph.",
                        Toast.LENGTH_LONG).show();
            }
        });

        root.addView(card);

        TextView notice = new TextView(requireContext());
        notice.setText(
                "DEMO PROJECT\nThis is an offline booking simulator, "
                        + "not a live IRCTC booking service.");
        notice.setTextColor(Color.GRAY);
        notice.setTextSize(12);
        notice.setGravity(Gravity.CENTER);
        notice.setPadding(dp(8), dp(22), dp(8), dp(8));
        root.addView(notice);

        return root;
    }

    private EditText createInput(String hint) {
        EditText input = new EditText(requireContext());
        input.setSingleLine(true);
        input.setHint(hint);
        input.setTextSize(15);
        input.setPadding(dp(12), dp(8), dp(12), dp(8));
        input.setBackground(rounded(Color.rgb(247, 249, 252), 10));
        return input;
    }

    private void addField(
            LinearLayout parent,
            String label,
            EditText input) {

        TextView title = new TextView(requireContext());
        title.setText(label);
        title.setTextColor(Color.GRAY);
        title.setTextSize(11);
        title.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = dp(18);
        parent.addView(title, labelParams);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(52));
        inputParams.topMargin = dp(6);
        parent.addView(input, inputParams);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int dp(int value) {
        return (int) (value
                * getResources().getDisplayMetrics().density + 0.5f);
    }
}
