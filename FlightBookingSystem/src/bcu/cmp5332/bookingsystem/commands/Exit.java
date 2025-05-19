package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class Exit implements Command {
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        try {
            System.out.println("Saving data and exiting...");
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (Exception e) {
            throw new FlightBookingSystemException("Failed to save data before exiting.");
        }
        System.exit(0);
    }
}
