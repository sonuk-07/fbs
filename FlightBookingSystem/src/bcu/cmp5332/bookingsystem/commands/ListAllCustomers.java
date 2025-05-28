package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.util.List;

public class ListAllCustomers implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        List<Customer> allCustomers = flightBookingSystem.getAllCustomers(); // Use the new method
        if (allCustomers.isEmpty()) {
            System.out.println("No customers in the system (including deleted ones).");
            return;
        }

        System.out.println("All Customers (including deleted):");
        for (Customer customer : allCustomers) {
            String status = customer.isDeleted() ? " (DELETED)" : "";
            System.out.println(customer.getDetailsShort() + status);
        }
        System.out.println(allCustomers.size() + " customer(s)");
    }
}