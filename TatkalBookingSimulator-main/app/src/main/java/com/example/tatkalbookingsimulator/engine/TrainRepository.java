
package com.example.tatkalbookingsimulator.engine;

import com.example.tatkalbookingsimulator.model.Coach;
import com.example.tatkalbookingsimulator.model.Train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainRepository {

    private final List<Train> trains = new ArrayList<>();

    public TrainRepository() {
        addSampleTrains();
    }

    // Add sample trains for the offline simulator.
    private void addSampleTrains() {

        Train train1 = new Train(
                "12001",
                "Deccan Express",
                "Mumbai",
                "Pune",
                "07:00 AM",
                "10:30 AM"
        );

        train1.addCoach(new Coach("C1", "AC", 5));
        train1.addCoach(new Coach("S1", "Sleeper", 10));

        Train train2 = new Train(
                "12002",
                "Intercity Express",
                "Pune",
                "Mumbai",
                "05:00 PM",
                "08:30 PM"
        );

        train2.addCoach(new Coach("C1", "AC", 5));
        train2.addCoach(new Coach("S1", "Sleeper", 10));

        Train train3 = new Train(
                "12701",
                "Express Special",
                "Hyderabad",
                "Pune",
                "06:00 AM",
                "02:00 PM"
        );

        train3.addCoach(new Coach("C1", "AC", 5));
        train3.addCoach(new Coach("S1", "Sleeper", 10));

        trains.add(train1);
        trains.add(train2);
        trains.add(train3);
    }

    // Return all sample trains.
    public List<Train> getAllTrains() {
        return Collections.unmodifiableList(trains);
    }

    // Search trains by source and destination.
    public List<Train> searchTrains(
            String source,
            String destination
    ) {
        List<Train> results = new ArrayList<>();

        if (source == null || destination == null
                || source.trim().isEmpty()
                || destination.trim().isEmpty()) {
            return results;
        }

        for (Train train : trains) {
            if (train.matchesRoute(source, destination)) {
                results.add(train);
            }
        }

        return results;
    }

    // Find a train using its train number.
    public Train findTrainByNumber(String trainNumber) {

        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            return null;
        }

        for (Train train : trains) {
            if (train.getTrainNumber().equalsIgnoreCase(
                    trainNumber.trim())) {
                return train;
            }
        }

        return null;
    }
}
