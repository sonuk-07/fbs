package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    
    private int id;
    private String name;
    private String phone;
    private String email;
    private boolean isDeleted;
    private final List<Booking> bookings = new ArrayList<>();
    
	public Customer(int id, String name, String phone, String email) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.isDeleted = false;
		
	}
    
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
	public List<Booking> getBookings() {
		return bookings;
	}
	
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    public String getDetailsShort() {
    	return id + ": " + name + "(" + phone + "," + email + ")";
    }
    
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer ID: ").append(id)
          .append("\nName: ").append(name)
          .append("\nPhone: ").append(phone)
          .append("\nEmail: ").append(email)
          .append("\nBookings: ").append(bookings.size());

        for (Booking booking : bookings) {
            // Handle outbound flight info if exists
            if (booking.getOutboundFlight() != null) {
                sb.append("\n - Outbound Flight ID: ").append(booking.getOutboundFlight().getId())
                  .append(", Date: ").append(booking.getBookingDate())
                  .append(", Class: ").append(booking.getBookedClass().getClassName());
            }
            // Handle return flight info if exists
            if (booking.getReturnFlight() != null) {
                sb.append("\n - Return Flight ID: ").append(booking.getReturnFlight().getId())
                  .append(", Date: ").append(booking.getBookingDate())
                  .append(", Class: ").append(booking.getBookedClass().getClassName());
            }
        }

        return sb.toString();
    }

    public void cancelBookingForFlight(int flightId) {
        bookings.removeIf(booking -> 
            (booking.getOutboundFlight() != null && booking.getOutboundFlight().getId() == flightId) ||
            (booking.getReturnFlight() != null && booking.getReturnFlight().getId() == flightId)
        );
    }

    
    public boolean hasBookingForFlight(Flight flight) {
        for (Booking booking : bookings) {
            if ((booking.getOutboundFlight() != null && booking.getOutboundFlight().equals(flight)) ||
                (booking.getReturnFlight() != null && booking.getReturnFlight().equals(flight))) {
                return true;
            }
        }
        return false;
    }


}
