package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;

public class DoNothingCommand implements Command {

    private final String message;

    public DoNothingCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        System.out.println(message);
    }
}
