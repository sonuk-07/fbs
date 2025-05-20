package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    
    private int id;
    private String name;
    private String phone;
    private final List<Booking> bookings = new ArrayList<>();
    
	public Customer(int id, String name, String phone) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
	}
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Booking> getBookings() {
		return bookings;
	}
	
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    public String getDetailsShort() {
    	return id + ": " + name + "(" + phone + ")";
    }
    
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer ID: ").append(id)
          .append("\nName: ").append(name)
          .append("\nPhone: ").append(phone)
          .append("\nBookings: ").append(bookings.size());

        for (Booking booking : bookings) {
            sb.append("\n - Flight ID: ").append(booking.getFlight().getId())
              .append(", Date: ").append(booking.getBookingDate());
        }

        return sb.toString();
    }

    public void cancelBookingForFlight(int flightId) {
    	bookings.removeIf(booking->booking.getFlight().getId()==flightId);
    }
    
    public boolean hasBookingForFlight(int flightId) {
        return bookings.stream().anyMatch(b -> b.getFlight().getId() == flightId);
    }

}
