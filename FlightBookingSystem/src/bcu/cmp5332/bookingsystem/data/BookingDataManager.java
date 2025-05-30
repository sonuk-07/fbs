package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BookingDataManager implements DataManager {

    public final String RESOURCE = "./resources/data/bookings.txt";
    private final String SEPARATOR = "::";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIdx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    lineIdx++;
                    continue;
                }

                String[] props = line.split(SEPARATOR, -1);

                // Expected fields: customerId, outboundFlightId, returnFlightId, bookingDate, bookedClass,
                // bookedPriceOutbound, bookedPriceReturn, cancellationFee, rebookFee, mealId
                // Total 10 fields. Minimum 9 fields (up to rebookFee) are mandatory for a valid booking
                if (props.length < 9) {
                    throw new FlightBookingSystemException("Malformed booking line at " + lineIdx + ": " + line + " (Too few fields)");
                }

                try {
                    int customerId = Integer.parseInt(props[0]);
                    int outboundFlightId = Integer.parseInt(props[1]);

                    Flight returnFlight = null;
                    if (!props[2].isBlank()) {
                        int returnFlightId = Integer.parseInt(props[2]);
                        returnFlight = fbs.getFlightByIDIncludingDeleted(returnFlightId);
                        if (returnFlight == null) {
                            System.err.println("Warning: Return flight ID " + returnFlightId + " not found for booking on line " + lineIdx + ". Booking partially loaded.");
                        }
                    }

                    LocalDate bookingDate = LocalDate.parse(props[3]);
                    CommercialClassType bookedClass = CommercialClassType.valueOf(props[4].toUpperCase());

                    BigDecimal bookedPriceOutbound = new BigDecimal(props[5]);
                    BigDecimal bookedPriceReturn = new BigDecimal(props[6]);

                    BigDecimal cancellationFee = new BigDecimal(props[7]);
                    BigDecimal rebookFee = new BigDecimal(props[8]);

                    // Load Meal - Safely handle if mealId is missing (older file format)
                    Meal meal = null;
                    if (props.length > 9 && !props[9].isBlank()) {
                        try {
                            int mealId = Integer.parseInt(props[9]);
                            meal = fbs.getMealByIDIncludingDeleted(mealId);
                            if (meal == null) {
                                System.err.println("Warning: Meal ID " + mealId + " not found for booking on line " + lineIdx + ". Booking loaded without meal.");
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Invalid meal ID format on line " + lineIdx + ". Booking loaded without meal.");
                        }
                    }

                    Customer customer = fbs.getCustomerByIDIncludingDeleted(customerId);
                    Flight outboundFlight = fbs.getFlightByIDIncludingDeleted(outboundFlightId);

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

                    Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, bookedClass, bookedPriceOutbound, bookedPriceReturn, meal);
                    booking.setCancellationFee(cancellationFee);
                    booking.setRebookFee(rebookFee);
                    
                    customer.addBooking(booking);
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
            for (Customer customer : fbs.getAllCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    out.print(customer.getId() + SEPARATOR);
                    out.print(booking.getOutboundFlight().getId() + SEPARATOR);

                    if (booking.getReturnFlight() != null) {
                        out.print(booking.getReturnFlight().getId());
                    }
                    out.print(SEPARATOR);

                    out.print(booking.getBookingDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + SEPARATOR);
                    out.print(booking.getBookedClass().name() + SEPARATOR);

                    out.print(booking.getBookedPriceOutbound().toPlainString() + SEPARATOR);
                    out.print(booking.getBookedPriceReturn().toPlainString() + SEPARATOR);

                    out.print(booking.getCancellationFee().toPlainString() + SEPARATOR);
                    out.print(booking.getRebookFee().toPlainString() + SEPARATOR);

                    if (booking.getMeal() != null) {
                        out.print(booking.getMeal().getId());
                    } else {
                        out.print("");
                    }
                    out.println();
                }
            }
        }
    }
}
