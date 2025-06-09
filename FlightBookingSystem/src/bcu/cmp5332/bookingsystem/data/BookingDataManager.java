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
                if (props.length < 12) { 
                    throw new FlightBookingSystemException("Malformed booking line at " + lineIdx + ": " + line + " (Too few fields)");
                }

                try {
                    int id = Integer.parseInt(props[0]); 
                    int customerId = Integer.parseInt(props[1]);
                    int outboundFlightId = Integer.parseInt(props[2]);

                    Flight returnFlight = null;
                    if (!props[3].isBlank() && !props[3].equals("null")) {
                        int returnFlightId = Integer.parseInt(props[3]);
                        returnFlight = fbs.getFlightByIDIncludingDeleted(returnFlightId);
                        if (returnFlight == null) {
                            System.err.println("Warning: Return flight ID " + returnFlightId + " not found for booking on line " + lineIdx + ". Booking partially loaded.");
                        }
                    }

                    LocalDate bookingDate = LocalDate.parse(props[4]);
                    CommercialClassType bookedClass = CommercialClassType.valueOf(props[5].toUpperCase());

                    BigDecimal bookedPriceOutbound = new BigDecimal(props[6]);
                    BigDecimal bookedPriceReturn = new BigDecimal(props[7]);

                    BigDecimal cancellationFee = new BigDecimal(props[8]);
                    BigDecimal rebookFee = new BigDecimal(props[9]);

                    Meal meal = null;
                    if (props.length > 10 && !props[10].isBlank() && !props[10].equals("null")) {
                        try {
                            int mealId = Integer.parseInt(props[10]);
                            meal = fbs.getMealByIDIncludingDeleted(mealId);
                            if (meal == null) {
                                System.err.println("Warning: Meal ID " + mealId + " not found for booking on line " + lineIdx + ". Booking loaded without meal.");
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Invalid meal ID format on line " + lineIdx + ". Booking loaded without meal.");
                        }
                    }

                    boolean isCancelled = Boolean.parseBoolean(props[11]);

                    Customer customer = fbs.getCustomerByIDIncludingDeleted(customerId);
                    Flight outboundFlight = fbs.getFlightByIDIncludingDeleted(outboundFlightId);

                    if (customer == null) {
                        System.err.println("Warning: Customer ID " + customerId + " not found for booking ID " + id + " on line " + lineIdx + ". Skipping booking.");
                        lineIdx++;
                        continue;
                    }
                    if (outboundFlight == null) {
                        System.err.println("Warning: Outbound flight ID " + outboundFlightId + " not found for booking ID " + id + " on line " + lineIdx + ". Skipping booking.");
                        lineIdx++;
                        continue;
                    }
                    if (!props[3].isBlank() && !props[3].equals("null") && returnFlight == null) {
                         System.err.println("Warning: Return flight ID " + props[3] + " not found for booking ID " + id + " on line " + lineIdx + ". Proceeding with one-way booking.");
                    }

                    Booking booking = new Booking(id, customer, outboundFlight, returnFlight, bookingDate, bookedClass, bookedPriceOutbound, bookedPriceReturn, meal);
                    booking.setCancellationFee(cancellationFee);
                    booking.setRebookFee(rebookFee);
                    booking.setCancelled(isCancelled);

                    if (!isCancelled) {
                        outboundFlight.addPassenger(customer, bookedClass);
                        if (returnFlight != null) {
                            returnFlight.addPassenger(customer, bookedClass);
                        }
                    }
                    
                    customer.addBooking(booking); 
                    fbs.addBookingWithoutFlightUpdate(booking); 

                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Error parsing number in booking data on line " + lineIdx + ": " + e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    throw new FlightBookingSystemException("Error parsing enum/date in booking data on line " + lineIdx + ": " + e.getMessage(), e);
                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error loading booking on line " + lineIdx + ": " + ex.getMessage(), ex);
                }
                lineIdx++;
            }
        } finally {
            int maxId = fbs.getBookings().stream()
                             .mapToInt(Booking::getId)
                             .max()
                             .orElse(0);
            fbs.setNextBookingId(maxId + 1);
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Booking booking : fbs.getBookings()) {
                out.print(booking.getId() + SEPARATOR);
                out.print(booking.getCustomer().getId() + SEPARATOR);
                out.print(booking.getOutboundFlight().getId() + SEPARATOR);

                if (booking.getReturnFlight() != null) {
                    out.print(booking.getReturnFlight().getId());
                } else {
                    out.print("null");
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
                    out.print("null");
                }
                out.print(SEPARATOR);

                out.print(booking.isCancelled());
                out.println();
            }
        }
    }
}