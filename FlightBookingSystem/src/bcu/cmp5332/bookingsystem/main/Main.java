package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException, FlightBookingSystemException {
        
        FlightBookingSystem fbs = FlightBookingSystemData.load();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Flight Booking System");
        System.out.println("Enter 'help' to see a list of available commands.");

        while (true) {
            System.out.print("> ");
            String line = br.readLine();

            try {
                Command command = CommandParser.parse(line);
                command.execute(fbs);
                FlightBookingSystemData.store(fbs);

            } catch (FlightBookingSystemException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O Error: " + ex.getMessage());
            }
        }
    }
}
