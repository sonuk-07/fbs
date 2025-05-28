package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;

public interface Command {

    public static final String HELP_MESSAGE = "Commands:\n"
        + "\tlistflights                               print all active flights\n"
        + "\tlistallflights                            print all flights (including deleted)\n"
        + "\tlistcustomers                             print all active customers\n"
        + "\tlistallcustomers                          print all customers (including deleted)\n"
        + "\taddflight                                 add a new flight\n"
        + "\tremoveflight [flight id]                  remove a flight by ID\n"
        + "\taddcustomer                               add a new customer\n"
        + "\tremovecustomer [customer id]              remove a customer by ID\n"
        + "\tshowflight [flight id]                    show flight details\n"
        + "\tshowcustomer [customer id]                show customer details\n"
        + "\taddbooking [customer id] [flight id]      add a new booking\n"
        + "\tcancelbooking [customer id] [flight id]   cancel a booking\n"
        + "\teditbooking [booking id] [flight id]      update a booking\n"
        + "\tloadgui                                   loads the GUI version of the app\n"
        + "\thelp                                      prints this help message\n"
        + "\texit                                      exits the program";

    // --- CRITICAL CHANGE HERE ---
    // The execute method must now accept the BufferedReader
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException;

}
