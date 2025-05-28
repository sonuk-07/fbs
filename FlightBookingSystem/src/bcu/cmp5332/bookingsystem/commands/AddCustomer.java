package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddCustomer implements Command {

    private final int id; // Added this field
    private final String name;
    private final String phone;
    private final String email;

    // Modified constructor to accept customer ID
    public AddCustomer(int id, String name, String phone, String email) {
        this.id = id; // Initialize the new ID field
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Customer customer = new Customer(id, name, phone, email);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added: " + customer.getName());
    }
}