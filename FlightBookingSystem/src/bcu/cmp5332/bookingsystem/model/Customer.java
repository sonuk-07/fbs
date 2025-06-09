package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;



/**
 * Represents a customer in the flight booking system.
 * A customer has an ID, name, phone number, email, age, gender, preferred meal type,
 * and a list of bookings. Customers can also be marked as deleted.
 */

public class Customer {

    private int id;
    private String name;
    private String phone;
    private String email;
    private int age;
    private String gender;
    private MealType preferredMealType; 
    private boolean isDeleted;
    private final List<Booking> bookings = new ArrayList<>();

    
    /**
     * Constructs a new Customer object with the specified details.
     *
     * @param id The unique ID of the customer.
     * @param name The name of the customer.
     * @param phone The phone number of the customer.
     * @param email The email address of the customer.
     * @param age The age of the customer.
     * @param gender The gender of the customer.
     * @param preferredMealType The preferred meal type of the customer.
     */
    
    // UPDATED CONSTRUCTOR
    public Customer(int id, String name, String phone, String email, int age, String gender, MealType preferredMealType) {
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.preferredMealType = preferredMealType; 
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * Sets the preferred meal type of the customer.
     *
     * @param preferredMealType The new preferred meal type.
     */
    
    public MealType getPreferredMealType() {
        return preferredMealType;
    }

    public void setPreferredMealType(MealType preferredMealType) {
        this.preferredMealType = preferredMealType;
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

    
    /**
     * Returns a short string representation of the customer's details.
     * Example: 1: Alice Wonderland (111222333, alice@example.com, 30, Female, Pref: Vegetarian)
     *
     * @return A short string containing the customer's ID, name, phone, email, age, gender, and preferred meal type.
     */

    public String getDetailsShort() {
        return id + ": " + name + " (" + phone + ", " + email + ", " + age + ", " + gender + ", Pref: " + preferredMealType.getDisplayName() + ")";
    }

    
    /**
     * Returns a long string representation of the customer's details, including their bookings.
     *
     * @return A detailed string containing the customer's ID, name, phone, email, age, gender, preferred meal type,
     * and details of their associated bookings.
     */
    
    
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer ID: ").append(id)
          .append("\nName: ").append(name)
          .append("\nPhone: ").append(phone)
          .append("\nEmail: ").append(email)
          .append("\nAge: ").append(age)
          .append("\nGender: ").append(gender)
          .append("\nPreferred Meal Type: ").append(preferredMealType.getDisplayName()) // Display preferred meal type
          .append("\nBookings: ").append(bookings.size());

        for (Booking booking : bookings) {
            if (booking.getOutboundFlight() != null) {
                sb.append("\n - Outbound Flight ID: ").append(booking.getOutboundFlight().getId())
                  .append(", Date: ").append(booking.getBookingDate())
                  .append(", Class: ").append(booking.getBookedClass().getClassName());
            }
            if (booking.getReturnFlight() != null) {
                sb.append("\n - Return Flight ID: ").append(booking.getReturnFlight().getId())
                  .append(", Date: ").append(booking.getBookingDate())
                  .append(", Class: ").append(booking.getBookedClass().getClassName());
            }
        }
        return sb.toString();
    }
    
    /**
     * Cancels a booking associated with a specific flight for this customer.
     * This method removes the booking if either the outbound or return flight matches the given flight ID.
     *
     * @param flightId The ID of the flight for which to cancel the booking.
     */

    public void cancelBookingForFlight(int flightId) {
        bookings.removeIf(booking ->
            (booking.getOutboundFlight() != null && booking.getOutboundFlight().getId() == flightId) ||
            (booking.getReturnFlight() != null && booking.getReturnFlight().getId() == flightId)
        );
    }
    
    /**
     * Checks if the customer has a booking for a given flight.
     *
     * @param flight The flight to check for a booking.
     * @return true if the customer has a booking for the specified flight (either as an outbound or return flight), false otherwise.
     */

    public boolean hasBookingForFlight(Flight flight) {
        for (Booking booking : bookings) {
            if ((booking.getOutboundFlight() != null && booking.getOutboundFlight().equals(flight)) ||
                (booking.getReturnFlight() != null && booking.getReturnFlight().equals(flight))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ID: " + id + " - " + name + " (" + email + ")";
    }
}
