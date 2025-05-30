package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;

import java.io.BufferedReader;
import java.io.IOException;

public class RemoveMeal implements Command {
    private final int mealId;

    public RemoveMeal(int mealId) {
        this.mealId = mealId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Meal meal = flightBookingSystem.getMealByIDIncludingDeleted(mealId);
        if (meal == null) {
            throw new FlightBookingSystemException("Meal with ID " + mealId + " not found.");
        }

        if (meal.isDeleted()) {
            System.out.println("Meal " + meal.getName() + " (ID: " + meal.getId() + ") is already removed.");
            return;
        }

        try {
            System.out.print("Are you sure you want to remove meal " + meal.getName() + " (ID: " + meal.getId() + ")? (yes/no): ");
            String confirmation = reader.readLine().trim().toLowerCase();
            if (!"yes".equals(confirmation)) {
                System.out.println("Meal removal cancelled.");
                return;
            }
        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input during confirmation: " + e.getMessage());
        }

        boolean removed = flightBookingSystem.removeMealById(mealId);
        if (removed) {
            System.out.println("Meal " + meal.getName() + " (ID: " + meal.getId() + ") has been removed.");
        } else {
            throw new FlightBookingSystemException("Failed to remove meal " + mealId + ".");
        }
    }
}
