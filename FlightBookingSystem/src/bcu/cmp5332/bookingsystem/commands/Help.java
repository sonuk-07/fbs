package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class Help implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        System.out.println(Command.HELP_MESSAGE);
    }
}
