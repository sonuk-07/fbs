package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem; // Import FlightBookingSystem
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerTest {

    private Customer customer;
    private Flight flight1;
    private Flight flight2;
    private Meal meal;
    private LocalDate systemDate;
    private FlightBookingSystem fbs; // Add FlightBookingSystem instance

    @BeforeEach
    void setUp() {
        fbs = new FlightBookingSystem(); // Initialize FlightBookingSystem
        systemDate = fbs.getSystemDate();

        customer = new Customer(1, "Alice Smith", "07123456789", "alice@email.com", 25, "Female", MealType.VEG);
        // Mock flights for testing bookings; actual prices are not critical for Customer class tests
        flight1 = new Flight(101, "BA100", "London", "Paris", systemDate.plusDays(10), new BigDecimal("100.00"), 50);
        flight2 = new Flight(102, "BA101", "Paris", "London", systemDate.plusDays(15), new BigDecimal("100.00"), 50);
        meal = new Meal(1, "Standard Meal", "A standard meal", new BigDecimal("10.00"), MealType.NON_VEG);
    }

    @Test
    void testCustomerConstructorAndGetters() {
        assertEquals(1, customer.getId());
        assertEquals("Alice Smith", customer.getName());
        assertEquals("07123456789", customer.getPhone());
        assertEquals("alice@email.com", customer.getEmail());
        assertEquals(25, customer.getAge());
        assertEquals("Female", customer.getGender());
        assertEquals(MealType.VEG, customer.getPreferredMealType());
        assertFalse(customer.isDeleted());
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    void testCustomerSetters() {
        customer.setName("Alicia Jones");
        assertEquals("Alicia Jones", customer.getName());
        customer.setPhone("07987654321");
        assertEquals("07987654321", customer.getPhone());
        customer.setEmail("alicia@example.com");
        assertEquals("alicia@example.com", customer.getEmail());
        customer.setAge(26);
        assertEquals(26, customer.getAge());
        customer.setGender("Other");
        assertEquals("Other", customer.getGender());
        customer.setPreferredMealType(MealType.VEGAN);
        assertEquals(MealType.VEGAN, customer.getPreferredMealType());
        customer.setDeleted(true);
        assertTrue(customer.isDeleted());
    }

    @Test
    void testAddBooking() {
        // Use fbs.generateNextBookingId() for unique IDs
        Booking booking1 = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);
        List<Booking> bookings = customer.getBookings();
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(booking1));

        Booking booking2 = new Booking(fbs.generateNextBookingId(), customer, flight2, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, null);
        customer.addBooking(booking2);
        assertEquals(2, customer.getBookings().size());
        assertTrue(customer.getBookings().contains(booking2));
    }

    @Test
    void testCancelBookingForFlight_OneWay() {
        // Use fbs.generateNextBookingId()
        Booking booking = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertEquals(1, customer.getBookings().size());
        assertFalse(booking.isCancelled()); // Ensure it's not cancelled initially

        customer.cancelBookingForFlight(flight1.getId());
        // After cancellation, the booking should be marked as cancelled, not necessarily removed from the list.
        // Assuming cancelBookingForFlight now sets the `isCancelled` flag.
        assertTrue(booking.isCancelled()); // Check if the flag is set
        // The hasBookingForFlight and getBookings() methods should reflect the new logic
        // where cancelled bookings might be filtered out or handled differently.
        // If your cancelBookingForFlight still removes the booking from the list, then isEmpty() is correct.
        // If it just marks it cancelled, then the list size might remain 1, and hasBookingForFlight needs adjustment.
        // For this test, I'll assume it sets isCancelled and the list remains, but hasBookingForFlight filters.
        assertFalse(customer.hasBookingForFlight(flight1)); // It should appear as if no active booking for flight1
        assertEquals(1, customer.getBookings().size()); // List size remains 1, but booking is cancelled
    }

    @Test
    void testCancelBookingForFlight_RoundTrip_Outbound() {
        Booking booking = new Booking(fbs.generateNextBookingId(), customer, flight1, flight2, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertTrue(customer.hasBookingForFlight(flight2));
        assertEquals(1, customer.getBookings().size());
        assertFalse(booking.isCancelled());

        // Cancelling the outbound flight should mark the entire booking as cancelled
        customer.cancelBookingForFlight(flight1.getId());
        assertTrue(booking.isCancelled()); // Check if the flag is set
        assertFalse(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2)); // Both flights should appear cancelled for this booking
        assertEquals(1, customer.getBookings().size()); // List size remains 1
    }

    @Test
    void testCancelBookingForFlight_RoundTrip_Return() {
        Booking booking = new Booking(fbs.generateNextBookingId(), customer, flight1, flight2, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertTrue(customer.hasBookingForFlight(flight2));
        assertEquals(1, customer.getBookings().size());
        assertFalse(booking.isCancelled());

        // Cancelling the return flight should also mark the entire booking as cancelled
        customer.cancelBookingForFlight(flight2.getId());
        assertTrue(booking.isCancelled()); // Check if the flag is set
        assertFalse(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2));
        assertEquals(1, customer.getBookings().size());
    }

    @Test
    void testCancelBookingForFlight_NonExistentFlight() {
        Booking booking = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());
        assertFalse(booking.isCancelled());

        customer.cancelBookingForFlight(999); // Non-existent flight ID
        assertEquals(1, customer.getBookings().size()); // Booking should still be there
        assertFalse(booking.isCancelled()); // And not cancelled
    }

    @Test
    void testHasBookingForFlight() {
        assertFalse(customer.hasBookingForFlight(flight1));

        Booking booking1 = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2)); // No booking for flight2 yet

        Booking booking2 = new Booking(fbs.generateNextBookingId(), customer, flight2, flight1, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), null);
        customer.addBooking(booking2);
        // Customer now has two active bookings: booking1 for flight1 outbound, and booking2 with flight2 outbound and flight1 return.
        // So customer.hasBookingForFlight(flight1) should remain true.
        assertTrue(customer.hasBookingForFlight(flight2));
        assertTrue(customer.hasBookingForFlight(flight1)); // Still true because booking1 is active and has flight1
    }

    @Test
    void testGetDetailsShort() {
        String expected = "1: Alice Smith (07123456789, alice@email.com, 25, Female, Pref: Vegetarian)";
        assertEquals(expected, customer.getDetailsShort());
    }

    @Test
    void testGetDetailsLong_NoBookings() {
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Customer ID: 1"));
        assertTrue(details.contains("Name: Alice Smith"));
        assertTrue(details.contains("Phone: 07123456789"));
        assertTrue(details.contains("Email: alice@email.com"));
        assertTrue(details.contains("Age: 25"));
        assertTrue(details.contains("Gender: Female"));
        assertTrue(details.contains("Preferred Meal Type: Vegetarian"));
        assertTrue(details.contains("Bookings: 0"));
        assertFalse(details.contains("- Outbound Flight ID:"));
    }

    @Test
    void testGetDetailsLong_WithBookings() {
        Booking booking1 = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);

        String details = customer.getDetailsLong();
        assertTrue(details.contains("Bookings: 1"));
        assertTrue(details.contains("- Booking ID: " + booking1.getId() + ", Outbound Flight ID: " + flight1.getId()));
        assertTrue(details.contains(", Date: " + systemDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        assertTrue(details.contains(", Class: Economy"));
        assertTrue(details.contains(", Status: Active")); // Check for status
        assertFalse(details.contains("Meal:")); // The Customer's getDetailsLong() does not include specific meal details
        assertFalse(details.contains("Total Price:")); // or total price from the booking.
    }

    @Test
    void testGetDetailsLong_WithMultipleBookings() {
        Booking booking1 = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);

        Booking booking2 = new Booking(fbs.generateNextBookingId(), customer, flight2, null, systemDate.minusDays(5), CommercialClassType.ECONOMY, new BigDecimal("120.00"), BigDecimal.ZERO, null);
        customer.addBooking(booking2);

        String details = customer.getDetailsLong();
        assertTrue(details.contains("Bookings: 2"));
        assertTrue(details.contains("- Booking ID: " + booking1.getId() + ", Outbound Flight ID: " + flight1.getId()));
        assertTrue(details.contains("- Booking ID: " + booking2.getId() + ", Outbound Flight ID: " + flight2.getId()));
    }

    @Test
    void testGetDetailsLong_WithCancelledBooking() {
        Booking booking1 = new Booking(fbs.generateNextBookingId(), customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);
        booking1.setCancelled(true); // Mark as cancelled

        String details = customer.getDetailsLong();
        assertTrue(details.contains("Bookings: 1"));
        assertTrue(details.contains("- Booking ID: " + booking1.getId() + ", Outbound Flight ID: " + flight1.getId()));
        assertTrue(details.contains(", Status: Cancelled")); // Check for status
    }
}