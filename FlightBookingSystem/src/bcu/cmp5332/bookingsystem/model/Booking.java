package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // For formatting output

public class Booking {

    private Customer customer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private LocalDate bookingDate;
    private CommercialClassType bookedClass;
    private BigDecimal bookedPriceOutbound; // NEW: Store the dynamic price for outbound flight
    private BigDecimal bookedPriceReturn;   // NEW: Store the dynamic price for return flight
    private BigDecimal cancellationFee;     // NEW: Store the cancellation fee
    private BigDecimal rebookFee;           // NEW: Store the rebook fee


    // Main constructor for bookings
    public Booking(Customer customer, Flight outboundFlight, Flight returnFlight,
                   LocalDate bookingDate, CommercialClassType bookedClass,
                   BigDecimal bookedPriceOutbound, BigDecimal bookedPriceReturn) { // Added price parameters
        this.customer = customer;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.bookingDate = bookingDate;
        this.bookedClass = bookedClass;
        this.bookedPriceOutbound = bookedPriceOutbound;
        this.bookedPriceReturn = bookedPriceReturn;
        this.cancellationFee = BigDecimal.ZERO; // Initialize to zero
        this.rebookFee = BigDecimal.ZERO;       // Initialize to zero
    }

    // Existing constructor (consider removing if the above is always used now, or adapt it)
    public Booking(Customer customer, Flight flight, LocalDate bookingDate, CommercialClassType bookedClass) {
        this.customer = customer;
        this.outboundFlight = flight;
        this.returnFlight = null; // Assuming this constructor means no return flight
        this.bookingDate = bookingDate;
        this.bookedClass = bookedClass;
        this.bookedPriceOutbound = BigDecimal.ZERO; // Default or calculate if this constructor is still used for new bookings
        this.bookedPriceReturn = BigDecimal.ZERO;
        this.cancellationFee = BigDecimal.ZERO;
        this.rebookFee = BigDecimal.ZERO;
    }


    public Customer getCustomer() {
        return customer;
    }

    public Flight getOutboundFlight() {
        return outboundFlight;
    }

    public Flight getReturnFlight() {
        return returnFlight;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public CommercialClassType getBookedClass() {
        return bookedClass;
    }

    public void setBookedClass(CommercialClassType bookedClass) {
        this.bookedClass = bookedClass;
    }

    // NEW Getters for booked prices
    public BigDecimal getBookedPriceOutbound() {
        return bookedPriceOutbound;
    }

    public BigDecimal getBookedPriceReturn() {
        return bookedPriceReturn;
    }

    // NEW Setters for booked prices (useful for loading data)
    public void setBookedPriceOutbound(BigDecimal bookedPriceOutbound) {
        this.bookedPriceOutbound = bookedPriceOutbound;
    }

    public void setBookedPriceReturn(BigDecimal bookedPriceReturn) {
        this.bookedPriceReturn = bookedPriceReturn;
    }

    // NEW Getters and Setters for fees
    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(BigDecimal cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public BigDecimal getRebookFee() {
        return rebookFee;
    }

    public void setRebookFee(BigDecimal rebookFee) {
        this.rebookFee = rebookFee;
    }


    public String getDetailsShort() {
        // You might want to include prices and fees here too, or in getDetailsLong
        return "Booking: Outbound " + outboundFlight.getFlightNumber() +
               ", Return " + (returnFlight != null ? returnFlight.getFlightNumber() : "N/A") +
               " - Class: " + bookedClass.getClassName() +
               " - Total Price: £" + getTotalBookingPrice(); // New helper method
    }

    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(customer.getDetailsShort())
          .append("\nOutbound Flight: ").append(outboundFlight.getDetailsShort())
          .append("\n  Booked Class: ").append(bookedClass.getClassName())
          .append("\n  Booked Price: £").append(bookedPriceOutbound);

        if (returnFlight != null) {
            sb.append("\nReturn Flight: ").append(returnFlight.getDetailsShort())
              .append("\n  Booked Price: £").append(bookedPriceReturn);
        }

        sb.append("\nBooking Date: ").append(bookingDate.format(dtf));
        sb.append("\nTotal Booking Price: £").append(getTotalBookingPrice());

        if (cancellationFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\nCancellation Fee Applied: £").append(cancellationFee);
        }
        if (rebookFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\nRebook Fee Applied: £").append(rebookFee);
        }

        return sb.toString();
    }

    // NEW: Helper method to calculate total booking price
    public BigDecimal getTotalBookingPrice() {
        return bookedPriceOutbound.add(bookedPriceReturn).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
