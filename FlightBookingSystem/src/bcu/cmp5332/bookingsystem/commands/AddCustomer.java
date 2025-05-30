package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.MealType; // NEW: Import MealType

public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final int age;
    private final String gender;
    private final MealType preferredMealType; // NEW FIELD

    // UPDATED CONSTRUCTOR
    public AddCustomer(String name, String phone, String email, int age, String gender, MealType preferredMealType) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.preferredMealType = preferredMealType; // Initialize new field
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        int newCustomerId = flightBookingSystem.generateNextCustomerId();
        // UPDATED Customer constructor call
        Customer customer = new Customer(newCustomerId, name, phone, email, age, gender, preferredMealType);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added: " + customer.getName());
    }
}
