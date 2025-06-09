package bcu.cmp5332.bookingsystem.test;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.MealType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CustomerTest {

    private Customer testCustomer;
    private Flight flight1;
    private Flight flight2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        // Initialize common objects for tests
        testCustomer = new Customer(1, "Alice Wonderland", "07123456789", "alice@example.com", 28, "Female", MealType.VEG);

        flight1 = new Flight(101, "FL001", "London", "Paris", LocalDate.of(2025, 8, 1),
                new BigDecimal("100.00"), 100);
        flight2 = new Flight(102, "FL002", "Paris", "London", LocalDate.of(2025, 8, 5),
                new BigDecimal("120.00"), 80);

        // Create bookings associated with the customer
        booking1 = new Booking(1, testCustomer, flight1, null, LocalDate.of(2025, 7, 1),
                CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, null);
        booking2 = new Booking(2, testCustomer, flight1, flight2, LocalDate.of(2025, 7, 10),
                CommercialClassType.BUSINESS, new BigDecimal("250.00"), new BigDecimal("300.00"), null);
    }

    @Test
    void testCustomerConstructor() {
        assertNotNull(testCustomer);
        assertEquals(1, testCustomer.getId());
        assertEquals("Alice Wonderland", testCustomer.getName());
        assertEquals("07123456789", testCustomer.getPhone());
        assertEquals("alice@example.com", testCustomer.getEmail());
        assertEquals(28, testCustomer.getAge());
        assertEquals("Female", testCustomer.getGender());
        assertEquals(MealType.VEG, testCustomer.getPreferredMealType());
        assertFalse(testCustomer.isDeleted());
        assertTrue(testCustomer.getBookings().isEmpty()); // Should be empty initially
    }

    @Test
    void testGettersAndSetters() {
        testCustomer.setId(2);
        assertEquals(2, testCustomer.getId());

        testCustomer.setName("Bob Johnson");
        assertEquals("Bob Johnson", testCustomer.getName());

        testCustomer.setPhone("07987654321");
        assertEquals("07987654321", testCustomer.getPhone());

        testCustomer.setEmail("bob@example.com");
        assertEquals("bob@example.com", testCustomer.getEmail());

        testCustomer.setAge(35);
        assertEquals(35, testCustomer.getAge());

        testCustomer.setGender("Male");
        assertEquals("Male", testCustomer.getGender());

        testCustomer.setPreferredMealType(MealType.GLUTEN_FREE);
        assertEquals(MealType.GLUTEN_FREE, testCustomer.getPreferredMealType());

        assertFalse(testCustomer.isDeleted());
        testCustomer.setDeleted(true);
        assertTrue(testCustomer.isDeleted());
    }

    @Test
    void testAddBooking() {
        testCustomer.addBooking(booking1);
        List<Booking> bookings = testCustomer.getBookings();
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(booking1));

        testCustomer.addBooking(booking2);
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void testCancelBookingForFlightOutbound() {
        testCustomer.addBooking(booking1); // Flight FL001 (outbound)
        testCustomer.addBooking(booking2); // Flight FL001 (outbound), Flight FL002 (return)

        assertEquals(2, testCustomer.getBookings().size());

        // Cancel booking related to FL001 (outbound)
        testCustomer.cancelBookingForFlight(flight1.getId());

        // Both booking1 and booking2 have flight1 as outbound, so both will be removed.
        assertEquals(0, testCustomer.getBookings().size());
        assertFalse(testCustomer.getBookings().contains(booking1));
        assertFalse(testCustomer.getBookings().contains(booking2)); // booking2 should also be removed
    }

    @Test
    void testCancelBookingForFlightReturn() {
        testCustomer.addBooking(booking1); // FL001 outbound
        testCustomer.addBooking(booking2); // FL001 outbound, FL002 return

        assertEquals(2, testCustomer.getBookings().size());

        // Cancel booking related to FL002 (return)
        testCustomer.cancelBookingForFlight(flight2.getId());

        assertEquals(1, testCustomer.getBookings().size());
        // booking1 should remain as it's not related to FL002
        assertTrue(testCustomer.getBookings().contains(booking1));
        // booking2 should be removed as FL002 is its return
        assertFalse(testCustomer.getBookings().contains(booking2));
    }

    @Test
    void testCancelBookingForFlightNonExistent() {
        testCustomer.addBooking(booking1);
        assertEquals(1, testCustomer.getBookings().size());

        // Attempt to cancel for a flight not in any booking
        testCustomer.cancelBookingForFlight(999);
        assertEquals(1, testCustomer.getBookings().size()); // No change
        assertTrue(testCustomer.getBookings().contains(booking1));
    }

    @Test
    void testHasBookingForFlightOutbound() {
        testCustomer.addBooking(booking1);
        assertTrue(testCustomer.hasBookingForFlight(flight1));
    }

    @Test
    void testHasBookingForFlightReturn() {
        testCustomer.addBooking(booking2);
        assertTrue(testCustomer.hasBookingForFlight(flight2));
    }

    @Test
    void testHasBookingForFlightNone() {
        assertFalse(testCustomer.hasBookingForFlight(flight1)); // No bookings yet
        testCustomer.addBooking(booking2);
        // Create a third flight not associated with any booking
        Flight flight3 = new Flight(103, "FL003", "Berlin", "Rome", LocalDate.of(2025, 9, 1),
                new BigDecimal("150.00"), 70);
        assertFalse(testCustomer.hasBookingForFlight(flight3));
    }

    @Test
    void testGetDetailsShort() {
        String expected = "1: Alice Wonderland (07123456789, alice@example.com, 28, Female, Pref: Vegetarian)";
        assertEquals(expected, testCustomer.getDetailsShort());
    }

    @Test
    void testGetDetailsLongNoBookings() {
        String expected = "Customer ID: 1\n" +
                          "Name: Alice Wonderland\n" +
                          "Phone: 07123456789\n" +
                          "Email: alice@example.com\n" +
                          "Age: 28\n" +
                          "Gender: Female\n" +
                          "Preferred Meal Type: Vegetarian\n" +
                          "Bookings: 0";
        assertEquals(expected, testCustomer.getDetailsLong());
    }

    @Test
    void testGetDetailsLongWithBookings() {
        testCustomer.addBooking(booking1);
        testCustomer.addBooking(booking2);

        String expected = "Customer ID: 1\n" +
                          "Name: Alice Wonderland\n" +
                          "Phone: 07123456789\n" +
                          "Email: alice@example.com\n" +
                          "Age: 28\n" +
                          "Gender: Female\n" +
                          "Preferred Meal Type: Vegetarian\n" +
                          "Bookings: 2\n" +
                          " - Outbound Flight ID: 101, Date: 2025-07-01, Class: Economy\n" +
                          " - Outbound Flight ID: 101, Date: 2025-07-10, Class: Business\n" +
                          " - Return Flight ID: 102, Date: 2025-07-10, Class: Business";
        assertEquals(expected, testCustomer.getDetailsLong());
    }

    @Test
    void testToString() {
        String expected = "ID: 1 - Alice Wonderland (alice@example.com)";
        assertEquals(expected, testCustomer.toString());
    }
}
