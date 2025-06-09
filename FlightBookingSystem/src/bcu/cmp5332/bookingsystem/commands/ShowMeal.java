package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;

import java.io.BufferedReader;

public class ShowMeal implements Command {
    private final int mealId;

    public ShowMeal(int mealId) {
        this.mealId = mealId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Meal meal = flightBookingSystem.getMealByID(mealId);
        if (meal == null) {
            throw new FlightBookingSystemException("Meal ID not found: " + mealId);
        }
        System.out.println(meal.getDetailsLong());
    }
}
