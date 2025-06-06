package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.MealType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class FlightTest {
    
    private Flight budgetFlight;
    private Flight commercialFlight;
    private Customer testCustomer;
    private LocalDate futureDate;
    private LocalDate systemDate;
    
    @BeforeEach
    void setUp() {
        // Set up test dates
        systemDate = LocalDate.now();
        futureDate = systemDate.plusDays(30);
        
        // Create a budget flight
        budgetFlight = new Flight(
            1, 
            "BF001", 
            "London", 
            "Paris", 
            futureDate, 
            new BigDecimal("100.00"), 
            150
        );
        
        // Create class capacities for commercial flight
        Map<CommercialClassType, Integer> classCapacities = new HashMap<>();
        classCapacities.put(CommercialClassType.ECONOMY, 120);
        classCapacities.put(CommercialClassType.PREMIUM_ECONOMY, 40);
        classCapacities.put(CommercialClassType.BUSINESS, 30);
        classCapacities.put(CommercialClassType.FIRST, 10);
        
        // Create a commercial flight
        commercialFlight = new Flight(
            2,
            "CF002",
            "Manchester",
            "New York",
            futureDate,
            new BigDecimal("200.00"),
            200,
            FlightType.COMMERCIAL,
            classCapacities
        );
        
        // Create a test customer (assuming Customer class exists)
        testCustomer = new Customer(1, "John Doe", "123456789", "john@example.com", 30, "Male", MealType.VEG);
    }
    
    @Test
    void testBudgetFlightConstructor() {
        assertEquals(1, budgetFlight.getId());
        assertEquals("BF001", budgetFlight.getFlightNumber());
        assertEquals("London", budgetFlight.getOrigin());
        assertEquals("Paris", budgetFlight.getDestination());
        assertEquals(futureDate, budgetFlight.getDepartureDate());
        assertEquals(new BigDecimal("100.00"), budgetFlight.getEconomyPrice());
        assertEquals(150, budgetFlight.getCapacity());
        assertEquals(FlightType.BUDGET, budgetFlight.getFlightType());
        assertFalse(budgetFlight.isDeleted());
    }
    
    @Test
    void testCommercialFlightConstructor() {
        assertEquals(2, commercialFlight.getId());
        assertEquals("CF002", commercialFlight.getFlightNumber());
        assertEquals("Manchester", commercialFlight.getOrigin());
        assertEquals("New York", commercialFlight.getDestination());
        assertEquals(futureDate, commercialFlight.getDepartureDate());
        assertEquals(new BigDecimal("200.00"), commercialFlight.getEconomyPrice());
        assertEquals(200, commercialFlight.getCapacity());
        assertEquals(FlightType.COMMERCIAL, commercialFlight.getFlightType());
    }
    
    @Test
    void testBudgetFlightAvailableClasses() {
        assertEquals(1, budgetFlight.getAvailableClasses().size());
        assertTrue(budgetFlight.getAvailableClasses().contains(CommercialClassType.ECONOMY));
    }
    
    @Test
    void testCommercialFlightAvailableClasses() {
        assertEquals(4, commercialFlight.getAvailableClasses().size());
        assertTrue(commercialFlight.getAvailableClasses().contains(CommercialClassType.ECONOMY));
        assertTrue(commercialFlight.getAvailableClasses().contains(CommercialClassType.PREMIUM_ECONOMY));
        assertTrue(commercialFlight.getAvailableClasses().contains(CommercialClassType.BUSINESS));
        assertTrue(commercialFlight.getAvailableClasses().contains(CommercialClassType.FIRST));
    }
    
    @Test
    void testGetPriceForClass() throws FlightBookingSystemException {
        // Test budget flight - only economy available
        assertEquals(0, new BigDecimal("100.00").compareTo(budgetFlight.getPriceForClass(CommercialClassType.ECONOMY)));
        
        // Test commercial flight - all classes available
        assertEquals(0, new BigDecimal("200.00").compareTo(commercialFlight.getPriceForClass(CommercialClassType.ECONOMY)));
        assertTrue(commercialFlight.getPriceForClass(CommercialClassType.BUSINESS).compareTo(new BigDecimal("200.00")) > 0);
    }
    
    @Test
    void testGetPriceForInvalidClass() {
        // Budget flight should not have business class
        assertThrows(FlightBookingSystemException.class, () -> {
            budgetFlight.getPriceForClass(CommercialClassType.BUSINESS);
        });
    }
    
    @Test
    void testCapacityForClass() {
        // Budget flight
        assertEquals(150, budgetFlight.getCapacityForClass(CommercialClassType.ECONOMY));
        assertEquals(0, budgetFlight.getCapacityForClass(CommercialClassType.BUSINESS));
        
        // Commercial flight
        assertEquals(120, commercialFlight.getCapacityForClass(CommercialClassType.ECONOMY));
        assertEquals(40, commercialFlight.getCapacityForClass(CommercialClassType.PREMIUM_ECONOMY));
        assertEquals(30, commercialFlight.getCapacityForClass(CommercialClassType.BUSINESS));
        assertEquals(10, commercialFlight.getCapacityForClass(CommercialClassType.FIRST));
    }
    
    @Test
    void testRemainingSeatsForClass() {
        // Initially, all seats should be available
        assertEquals(150, budgetFlight.getRemainingSeatsForClass(CommercialClassType.ECONOMY));
        assertEquals(120, commercialFlight.getRemainingSeatsForClass(CommercialClassType.ECONOMY));
        assertEquals(30, commercialFlight.getRemainingSeatsForClass(CommercialClassType.BUSINESS));
    }
    
    @Test
    void testIsClassAvailable() {
        // Budget flight
        assertTrue(budgetFlight.isClassAvailable(CommercialClassType.ECONOMY));
        assertFalse(budgetFlight.isClassAvailable(CommercialClassType.BUSINESS));
        
        // Commercial flight
        assertTrue(commercialFlight.isClassAvailable(CommercialClassType.ECONOMY));
        assertTrue(commercialFlight.isClassAvailable(CommercialClassType.BUSINESS));
        assertTrue(commercialFlight.isClassAvailable(CommercialClassType.FIRST));
    }
    
    @Test
    void testHasAnySeatsLeft() {
        assertTrue(budgetFlight.hasAnySeatsLeft());
        assertTrue(commercialFlight.hasAnySeatsLeft());
    }
    
    @Test
    void testAddPassenger() throws FlightBookingSystemException {
        // Add passenger to budget flight
        budgetFlight.addPassenger(testCustomer, CommercialClassType.ECONOMY);
        assertEquals(1, budgetFlight.getPassengers().size());
        assertEquals(149, budgetFlight.getRemainingSeatsForClass(CommercialClassType.ECONOMY));
        assertEquals(1, budgetFlight.getOccupiedSeatsByClass(CommercialClassType.ECONOMY));
        
        // Try to add same passenger again - should throw exception
        assertThrows(FlightBookingSystemException.class, () -> {
            budgetFlight.addPassenger(testCustomer, CommercialClassType.ECONOMY);
        });
    }
    
    @Test
    void testRemovePassenger() throws FlightBookingSystemException {
        // First add a passenger
        budgetFlight.addPassenger(testCustomer, CommercialClassType.ECONOMY);
        assertEquals(1, budgetFlight.getPassengers().size());
        
        // Then remove the passenger
        budgetFlight.removePassenger(testCustomer, CommercialClassType.ECONOMY);
        assertEquals(0, budgetFlight.getPassengers().size());
        assertEquals(150, budgetFlight.getRemainingSeatsForClass(CommercialClassType.ECONOMY));
        assertEquals(0, budgetFlight.getOccupiedSeatsByClass(CommercialClassType.ECONOMY));
    }
    
    @Test
    void testHasDeparted() {
        // Flight with future date should not have departed
        assertFalse(budgetFlight.hasDeparted(systemDate));
        
        // Test with past date
        Flight pastFlight = new Flight(
            3, "PF003", "London", "Berlin", 
            systemDate.minusDays(1), 
            new BigDecimal("150.00"), 
            100
        );
        assertTrue(pastFlight.hasDeparted(systemDate));
        
        // Test with same date (departure day)
        Flight todayFlight = new Flight(
            4, "TF004", "London", "Madrid", 
            systemDate, 
            new BigDecimal("120.00"), 
            120
        );
        assertTrue(todayFlight.hasDeparted(systemDate));
    }
    
    @Test
    void testGetDynamicPrice() throws FlightBookingSystemException {
        LocalDate nearDepartureDate = systemDate.plusDays(5); // Within 7 days
        Flight urgentFlight = new Flight(
            5, "UF005", "London", "Rome", 
            nearDepartureDate, 
            new BigDecimal("100.00"), 
            100
        );
        
        // Price should be higher for flights departing within 7 days
        BigDecimal dynamicPrice = urgentFlight.getDynamicPrice(CommercialClassType.ECONOMY, systemDate);
        assertTrue(dynamicPrice.compareTo(new BigDecimal("100.00")) > 0);
    }
    
    @Test
    void testSettersAndGetters() {
        budgetFlight.setId(999);
        assertEquals(999, budgetFlight.getId());
        
        budgetFlight.setFlightNumber("NEW001");
        assertEquals("NEW001", budgetFlight.getFlightNumber());
        
        budgetFlight.setOrigin("Birmingham");
        assertEquals("Birmingham", budgetFlight.getOrigin());
        
        budgetFlight.setDestination("Dublin");
        assertEquals("Dublin", budgetFlight.getDestination());
        
        LocalDate newDate = systemDate.plusDays(45);
        budgetFlight.setDepartureDate(newDate);
        assertEquals(newDate, budgetFlight.getDepartureDate());
        
        budgetFlight.setEconomyPrice(new BigDecimal("250.00"));
        assertEquals(new BigDecimal("250.00"), budgetFlight.getEconomyPrice());
        
        budgetFlight.setCapacity(200);
        assertEquals(200, budgetFlight.getCapacity());
    }
    
    @Test
    void testDeletedStatus() {
        assertFalse(budgetFlight.isDeleted());
        
        budgetFlight.setDeleted(true);
        assertTrue(budgetFlight.isDeleted());
        
        budgetFlight.setDeleted(false);
        assertFalse(budgetFlight.isDeleted());
    }
    
    @Test
    void testGetDetailsShort() {
        String details = budgetFlight.getDetailsShort();
        assertTrue(details.contains("Flight #1"));
        assertTrue(details.contains("BF001"));
        assertTrue(details.contains("London"));
        assertTrue(details.contains("Paris"));
        assertTrue(details.contains("BUDGET"));
    }
    
    @Test
    void testGetDetailsLong() throws FlightBookingSystemException {
        String details = budgetFlight.getDetailsLong(systemDate);
        assertTrue(details.contains("Flight ID: 1"));
        assertTrue(details.contains("Flight Number: BF001"));
        assertTrue(details.contains("From: London"));
        assertTrue(details.contains("To: Paris"));
        assertTrue(details.contains("Flight Type: BUDGET"));
        assertTrue(details.contains("Total Capacity: 150"));
        assertTrue(details.contains("Class Information:"));
    }
}