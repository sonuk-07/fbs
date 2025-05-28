package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

public class Main {

    private static FlightBookingSystem fbs;

    public static void main(String[] args) throws IOException, FlightBookingSystemException {

        fbs = FlightBookingSystemData.load();
        System.out.println("Flight Booking System Loaded");
        System.out.println("System Date: " + fbs.getSystemDate());

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println(Command.HELP_MESSAGE);

        while (true) {
            System.out.print("\nEnter command: ");
            String commandLine = reader.readLine();

            try {
                Command command = CommandParser.parse(commandLine, fbs, reader);

                if (command == null) {
                    continue;
                }

                command.execute(fbs, reader);

                FlightBookingSystemData.store(fbs);

            } catch (FlightBookingSystemException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("An unexpected error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
