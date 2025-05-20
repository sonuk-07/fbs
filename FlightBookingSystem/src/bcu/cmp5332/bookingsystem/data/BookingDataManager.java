package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

public class BookingDataManager implements DataManager {

    public final String RESOURCE = "./resources/data/bookings.txt";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIdx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    lineIdx++;
                    continue; // skip empty lines
                }

                String[] props = line.split(SEPARATOR, -1);

                if (props.length < 3) {
                    throw new FlightBookingSystemException("Malformed booking line at " + lineIdx + ": " + line);
                }

                try {
                    int customerId = Integer.parseInt(props[0]);
                    int flightId = Integer.parseInt(props[1]);
                    LocalDate bookingDate = LocalDate.parse(props[2].substring(0, 10)); // parse from 2025-05-20T00:00:00

                    Customer customer = fbs.getCustomerByID(customerId);
                    Flight flight = fbs.getFlightByID(flightId);

                    Booking booking = new Booking(customer, flight, bookingDate);
                    customer.addBooking(booking);
                    flight.addPassenger(customer);

                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing booking on line " + lineIdx, ex);
                }

                lineIdx++;
            }
        }
    }

    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    out.print(customer.getId() + SEPARATOR);
                    out.print(booking.getFlight().getId() + SEPARATOR);
                    out.print(booking.getBookingDate().atStartOfDay().withSecond(0).withNano(0) + SEPARATOR); // Timestamp
                    out.println(); // end of line
                }
            }
        }
    }


}
