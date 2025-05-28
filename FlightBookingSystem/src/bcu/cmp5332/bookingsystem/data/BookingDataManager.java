package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import java.io.*;
import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BookingDataManager implements DataManager {

    public final String RESOURCE = "./resources/data/bookings.txt";
    private final String SEPARATOR = "::"; // Ensure SEPARATOR is defined

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

                if (props.length < 9) { // Adjusted min length
                    throw new FlightBookingSystemException("Malformed booking line at " + lineIdx + ": " + line);
                }

                try {
                    int customerId = Integer.parseInt(props[0]);
                    int outboundFlightId = Integer.parseInt(props[1]);

                    Flight returnFlight = null;
                    if (!props[2].isBlank()) {
                        int returnFlightId = Integer.parseInt(props[2]);
                        // Use getFlightByIDIncludingDeleted here because a booking might exist
                        // for a flight that has since departed or been soft-deleted.
                        returnFlight = fbs.getFlightByIDIncludingDeleted(returnFlightId);
                        if (returnFlight == null) {
                            System.err.println("Warning: Return flight ID " + returnFlightId + " not found for booking on line " + lineIdx + ". Booking partially loaded.");
                        }
                    }

                    LocalDate bookingDate = LocalDate.parse(props[3].substring(0, 10)); // Ensure correct date format parsing
                    CommercialClassType bookedClass = CommercialClassType.valueOf(props[4].toUpperCase());

                    // NEW: Load booked prices
                    BigDecimal bookedPriceOutbound = new BigDecimal(props[5]);
                    BigDecimal bookedPriceReturn = new BigDecimal(props[6]);

                    // NEW: Load fees
                    BigDecimal cancellationFee = new BigDecimal(props[7]);
                    BigDecimal rebookFee = new BigDecimal(props[8]);

                    Customer customer = fbs.getCustomerByIDIncludingDeleted(customerId); // Use getCustomerByIDIncludingDeleted
                    Flight outboundFlight = fbs.getFlightByIDIncludingDeleted(outboundFlightId); // Use getFlightByIDIncludingDeleted

                    if (customer == null) {
                         System.err.println("Warning: Customer ID " + customerId + " not found for booking on line " + lineIdx + ". Skipping booking.");
                         lineIdx++;
                         continue;
                    }
                    if (outboundFlight == null) {
                        System.err.println("Warning: Outbound flight ID " + outboundFlightId + " not found for booking on line " + lineIdx + ". Skipping booking.");
                        lineIdx++;
                        continue;
                    }

                    Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, bookedClass, bookedPriceOutbound, bookedPriceReturn);
                    booking.setCancellationFee(cancellationFee);
                    booking.setRebookFee(rebookFee);
                    
                    customer.addBooking(booking); // Add booking to customer's list
                    outboundFlight.addPassenger(customer, bookedClass);
                    if (returnFlight != null) {
                        returnFlight.addPassenger(customer, bookedClass);
                    }

                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing booking on line " + lineIdx + ": " + ex.getMessage(), ex);
                }
                lineIdx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            // Iterate over ALL customers to ensure all bookings are saved
            for (Customer customer : fbs.getAllCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    out.print(customer.getId() + SEPARATOR);
                    out.print(booking.getOutboundFlight().getId() + SEPARATOR);

                    // Store return flight ID or empty string if none
                    if (booking.getReturnFlight() != null) {
                        out.print(booking.getReturnFlight().getId());
                    }
                    out.print(SEPARATOR); // Always print separator

                    // Store booking date (ensure consistent format)
                    out.print(booking.getBookingDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + SEPARATOR);

                    // Store booked class
                    out.print(booking.getBookedClass().name() + SEPARATOR); // Use .name() for enum string

                    // NEW: Store booked prices
                    out.print(booking.getBookedPriceOutbound().toPlainString() + SEPARATOR); // Use toPlainString() for exact decimal representation
                    out.print(booking.getBookedPriceReturn().toPlainString() + SEPARATOR);

                    // NEW: Store fees
                    out.print(booking.getCancellationFee().toPlainString() + SEPARATOR);
                    out.print(booking.getRebookFee().toPlainString());

                    out.println();
                }
            }
        }
    }
}
