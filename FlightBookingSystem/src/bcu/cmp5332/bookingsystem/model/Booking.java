package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;

public class Booking {
    
    private Customer customer;
    private Flight flight;
    private LocalDate bookingDate;
    private CommercialClassType bookedClass;

    public Booking(Customer customer, Flight flight, LocalDate bookingDate) {
        this(customer, flight, bookingDate, CommercialClassType.ECONOMY);
    }

    public Booking(Customer customer, Flight flight, LocalDate bookingDate, CommercialClassType bookedClass) {
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.bookedClass = bookedClass;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
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

    public String getDetailsShort() {
        return "Booking: " + flight.getFlightNumber() + " - " + bookedClass.getClassName();
    }

    public String getDetailsLong() {
        try {
            return "Booking Details:\n" +
                   "Customer: " + customer.getDetailsShort() + "\n" +
                   "Flight: " + flight.getDetailsShort() + "\n" +
                   "Class: " + bookedClass.getClassName() + "\n" +
                   "Price: £" + flight.getPriceForClass(bookedClass) + "\n" +
                   "Booking Date: " + bookingDate;
        } catch (Exception e) {
            return "Booking Details:\n" +
                   "Customer: " + customer.getDetailsShort() + "\n" +
                   "Flight: " + flight.getDetailsShort() + "\n" +
                   "Class: " + bookedClass.getClassName() + "\n" +
                   "Booking Date: " + bookingDate;
        }
    }
}