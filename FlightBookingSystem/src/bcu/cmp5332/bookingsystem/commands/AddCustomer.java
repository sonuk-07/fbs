package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddCustomer implements Command {

    private final String name;
    private final String phone;

    public AddCustomer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        int maxId = 0;

        for (Customer c : flightBookingSystem.getCustomers()) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }

        Customer customer = new Customer(++maxId, name, phone);
        flightBookingSystem.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added: " + name);
    }

}
