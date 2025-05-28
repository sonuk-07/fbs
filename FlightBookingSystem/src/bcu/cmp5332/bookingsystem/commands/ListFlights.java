package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.CommercialClassType; // Import CommercialClassType

import java.io.BufferedReader;
import java.util.List;

public class ListFlights implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        // getFlights() now automatically filters for future and non-deleted flights
        List<Flight> flights = flightBookingSystem.getFlights();
        if (flights.isEmpty()) {
            System.out.println("No active future flights in the system.");
            return;
        }
        
        System.out.println("Active Future Flights:");
        for (Flight flight : flights) {
            StringBuilder flightInfo = new StringBuilder();
            flightInfo.append(flight.getDetailsShort());

            // Append dynamic prices for each class
            for (CommercialClassType classType : flight.getAvailableClasses()) {
                try {
                    flightInfo.append(" | ").append(classType.getClassName()).append(": £").append(flight.getDynamicPrice(classType, flightBookingSystem.getSystemDate()));
                    flightInfo.append(" (Seats: ").append(flight.getRemainingSeatsForClass(classType)).append(")");
                } catch (FlightBookingSystemException e) {
                    flightInfo.append(" | ").append(classType.getClassName()).append(": Price N/A");
                }
            }
            System.out.println(flightInfo.toString());
        }
        System.out.println(flights.size() + " active future flight(s)");
    }
}
