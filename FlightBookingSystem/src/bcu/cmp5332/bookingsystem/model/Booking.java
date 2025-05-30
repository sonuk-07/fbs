package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Booking {

    private Customer customer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private LocalDate bookingDate;
    private CommercialClassType bookedClass;
    private BigDecimal bookedPriceOutbound;
    private BigDecimal bookedPriceReturn;
    private BigDecimal cancellationFee;
    private BigDecimal rebookFee;
    private Meal meal;

    public Booking(Customer customer, Flight outboundFlight, Flight returnFlight,
                   LocalDate bookingDate, CommercialClassType bookedClass,
                   BigDecimal bookedPriceOutbound, BigDecimal bookedPriceReturn,
                   Meal meal) {
        this.customer = customer;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.bookingDate = bookingDate;
        this.bookedClass = bookedClass;
        this.bookedPriceOutbound = bookedPriceOutbound;
        this.bookedPriceReturn = bookedPriceReturn;
        this.cancellationFee = BigDecimal.ZERO;
        this.rebookFee = BigDecimal.ZERO;
        this.meal = meal;
    }

    public Booking(Customer customer, Flight flight, LocalDate bookingDate, CommercialClassType bookedClass) {
        this(customer, flight, null, bookingDate, bookedClass, BigDecimal.ZERO, BigDecimal.ZERO, null);
    }

    public Customer getCustomer() { return customer; }
    public Flight getOutboundFlight() { return outboundFlight; }
    public Flight getReturnFlight() { return returnFlight; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public CommercialClassType getBookedClass() { return bookedClass; }
    public void setBookedClass(CommercialClassType bookedClass) { this.bookedClass = bookedClass; }
    public BigDecimal getBookedPriceOutbound() { return bookedPriceOutbound; }
    public BigDecimal getBookedPriceReturn() { return bookedPriceReturn; }
    public void setBookedPriceOutbound(BigDecimal bookedPriceOutbound) { this.bookedPriceOutbound = bookedPriceOutbound; }
    public void setBookedPriceReturn(BigDecimal bookedPriceReturn) { this.bookedPriceReturn = bookedPriceReturn; }
    public BigDecimal getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(BigDecimal cancellationFee) { this.cancellationFee = cancellationFee; }
    public BigDecimal getRebookFee() { return rebookFee; }
    public void setRebookFee(BigDecimal rebookFee) { this.rebookFee = rebookFee; }
    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }

    // UPDATED getDetailsShort()
    public String getDetailsShort() {
        String mealInfo = (meal != null) ? ", Meal: " + meal.getName() + " (" + meal.getType().getDisplayName() + ")" : "";
        return "Booking: Outbound " + outboundFlight.getFlightNumber() +
               ", Return " + (returnFlight != null ? returnFlight.getFlightNumber() : "N/A") +
               " - Class: " + bookedClass.getClassName() +
               mealInfo +
               " - Total Price: £" + getTotalBookingPrice();
    }

    // UPDATED getDetailsLong()
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

        if (meal != null) {
            sb.append("\nMeal: ").append(meal.getName())
              .append(" (Type: ").append(meal.getType().getDisplayName()) // Display meal type
              .append(", Price: £").append(meal.getPrice()).append(")");
        } else {
            sb.append("\nMeal: None");
        }

        sb.append("\nBooking Date: ").append(bookingDate.format(dtf));
        sb.append("\nTotal Booking Price (Flights + Meal): £").append(getTotalBookingPrice());

        if (cancellationFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\nCancellation Fee Applied: £").append(cancellationFee);
        }
        if (rebookFee.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\nRebook Fee Applied: £").append(rebookFee);
        }

        return sb.toString();
    }

    public BigDecimal getTotalBookingPrice() {
        BigDecimal total = bookedPriceOutbound.add(bookedPriceReturn);
        if (meal != null) {
            total = total.add(meal.getPrice());
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
