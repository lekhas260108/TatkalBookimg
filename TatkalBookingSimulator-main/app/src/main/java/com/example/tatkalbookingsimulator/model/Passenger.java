
package com.example.tatkalbookingsimulator.model;

public class Passenger {

    private final String passengerId;
    private final String name;
    private final int age;
    private final String gender;

    public Passenger(String passengerId, String name,
                     int age, String gender) {

        if (passengerId == null || passengerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger ID is required.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name is required.");
        }

        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120.");
        }

        this.passengerId = passengerId.trim();
        this.name = name.trim();
        this.age = age;
        this.gender = gender == null ? "Not specified" : gender.trim();
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
