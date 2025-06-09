package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;

import java.io.BufferedReader;
import java.util.List;

public class ListMeals implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        List<Meal> allMeals = flightBookingSystem.getAllMeals(); 

        if (allMeals.isEmpty()) {
            System.out.println("No meals in the system.");
            return;
        }

        System.out.println("--- All Meals (Active and Unavailable) ---");
        for (Meal meal : allMeals) {
            String status = meal.isDeleted() ? " (Unavailable)" : " (Active)"; 
            System.out.println(meal.getDetailsShort() + status);
        }
        System.out.println(allMeals.size() + " meal(s) listed.");
    }
}