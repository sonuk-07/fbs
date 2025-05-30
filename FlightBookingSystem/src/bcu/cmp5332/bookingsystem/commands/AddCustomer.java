package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddCustomer implements Command {

    private final int id;
    private final String name;
    private final String phone;
    private final String email;
    private final int age;    // NEW FIELD
    private final String gender; // NEW FIELD

    // --- UPDATED CONSTRUCTOR ---
    public AddCustomer(int id, String name, String phone, String email, int age, String gender) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age = age;    // INITIALIZE NEW FIELD
        this.gender = gender; // INITIALIZE NEW FIELD
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        // --- UPDATED Customer CONSTRUCTOR CALL ---
        Customer customer = new Customer(id, name, phone, email, age, gender);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added: " + customer.getName());
    }
}
