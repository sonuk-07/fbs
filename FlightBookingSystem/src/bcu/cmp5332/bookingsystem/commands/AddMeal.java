package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType; // NEW: Import MealType

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class AddMeal implements Command {

    private final String name;
    private final String description;
    private final BigDecimal price;
    private final MealType type; // NEW FIELD

    // UPDATED CONSTRUCTOR
    public AddMeal(String name, String description, BigDecimal price, MealType type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type; // Initialize new field
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        int nextId = flightBookingSystem.generateNextMealId();
        // UPDATED Meal CONSTRUCTOR CALL
        Meal meal = new Meal(nextId, name, description, price, type);
        flightBookingSystem.addMeal(meal);
        System.out.println("Meal #" + meal.getId() + " added: " + meal.getName() + " (" + meal.getType().getDisplayName() + ") " + " (£" + meal.getPrice() + ")");
    }
}
