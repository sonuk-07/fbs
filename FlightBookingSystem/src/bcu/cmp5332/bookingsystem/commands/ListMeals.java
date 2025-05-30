package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;

import java.io.BufferedReader;
import java.util.List;

public class ListMeals implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        List<Meal> meals = flightBookingSystem.getMeals(); // Gets active meals
        if (meals.isEmpty()) {
            System.out.println("No available meals in the system.");
            return;
        }

        System.out.println("--- Available Meals ---");
        for (Meal meal : meals) {
            System.out.println(meal.getDetailsShort());
        }
        System.out.println(meals.size() + " available meal(s)");
    }
}
