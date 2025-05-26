package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Flight {

    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private BigDecimal economyPrice; // Base price for economy class
    private int capacity;
    private FlightType flightType; // BUDGET or COMMERCIAL
    private Map<CommercialClassType, Integer> classCapacities; // Capacity for each class
    private Map<CommercialClassType, BigDecimal> classPrices; // Calculated prices for each class

    private final Set<Customer> passengers;

    // Constructor for Budget flights (single class)
    public Flight(int id, String flightNumber, String origin, String destination,
                  LocalDate departureDate, BigDecimal economyPrice, int capacity) {
        this(id, flightNumber, origin, destination, departureDate, economyPrice, 
             capacity, FlightType.BUDGET, createBudgetCapacities(capacity));
    }

    // Constructor for Commercial flights (multiple classes)
    public Flight(int id, String flightNumber, String origin, String destination,
                  LocalDate departureDate, BigDecimal economyPrice, int totalCapacity,
                  FlightType flightType, Map<CommercialClassType, Integer> classCapacities) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = totalCapacity;
        this.flightType = flightType;
        this.classCapacities = classCapacities != null ? classCapacities : createDefaultCapacities(totalCapacity);
        this.passengers = new HashSet<>();
        
        // Calculate prices for all classes based on economy price
        calculateClassPrices();
    }

    // Create default capacities for budget flights (economy only)
    private static Map<CommercialClassType, Integer> createBudgetCapacities(int totalCapacity) {
        Map<CommercialClassType, Integer> capacities = new HashMap<>();
        capacities.put(CommercialClassType.ECONOMY, totalCapacity);
        return capacities;
    }

    // Create default capacities for commercial flights
    private static Map<CommercialClassType, Integer> createDefaultCapacities(int totalCapacity) {
        Map<CommercialClassType, Integer> capacities = new HashMap<>();
        // Default distribution: 60% Economy, 20% Premium, 15% Business, 5% First
        capacities.put(CommercialClassType.ECONOMY, (int)(totalCapacity * 0.6));
        capacities.put(CommercialClassType.PREMIUM_ECONOMY, (int)(totalCapacity * 0.2));
        capacities.put(CommercialClassType.BUSINESS, (int)(totalCapacity * 0.15));
        capacities.put(CommercialClassType.FIRST, (int)(totalCapacity * 0.05));
        return capacities;
    }

    // Calculate prices for all classes based on economy price
    private void calculateClassPrices() {
        this.classPrices = new HashMap<>();
        
        if (flightType == FlightType.BUDGET) {
            // Budget flights only have economy class
            classPrices.put(CommercialClassType.ECONOMY, economyPrice);
        } else {
            // Commercial flights have all classes with calculated prices
            for (CommercialClassType classType : CommercialClassType.values()) {
                BigDecimal classPrice = economyPrice.multiply(classType.getMultiplier());
                classPrices.put(classType, classPrice);
            }
        }
    }

    // Get available classes for this flight
    public List<CommercialClassType> getAvailableClasses() {
        if (flightType == FlightType.BUDGET) {
            return Arrays.asList(CommercialClassType.ECONOMY);
        } else {
            return Arrays.asList(CommercialClassType.values());
        }
    }

    // Get price for a specific class
    public BigDecimal getPriceForClass(CommercialClassType classType) throws FlightBookingSystemException {
        if (!classPrices.containsKey(classType)) {
            throw new FlightBookingSystemException("Class " + classType.getClassName() + 
                " is not available for this flight type");
        }
        return classPrices.get(classType);
    }

    // Get capacity for a specific class
    public int getCapacityForClass(CommercialClassType classType) {
        return classCapacities.getOrDefault(classType, 0);
    }

    // Check if class is available for booking
    public boolean isClassAvailable(CommercialClassType classType) {
        if (flightType == FlightType.BUDGET) {
            return classType == CommercialClassType.ECONOMY;
        }
        return classCapacities.containsKey(classType) && classCapacities.get(classType) > 0;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public BigDecimal getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(BigDecimal economyPrice) {
        this.economyPrice = economyPrice;
        calculateClassPrices(); // Recalculate all prices when economy price changes
    }

    // Legacy method for backward compatibility
    public BigDecimal getPrice() {
        return economyPrice;
    }

    public void setPrice(BigDecimal price) {
        setEconomyPrice(price);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
        calculateClassPrices(); // Recalculate prices when flight type changes
    }

    // Legacy method for backward compatibility
    public String getFlightClass() {
        return flightType.toString();
    }

    public void setFlightClass(String flightClass) {
        this.flightType = FlightType.valueOf(flightClass.toUpperCase());
        calculateClassPrices();
    }

    public Map<CommercialClassType, Integer> getClassCapacities() {
        return new HashMap<>(classCapacities);
    }

    public Map<CommercialClassType, BigDecimal> getClassPrices() {
        return new HashMap<>(classPrices);
    }

    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    // --- Helper Methods ---

    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " +
                destination + " on " + departureDate.format(dtf) + " (" + flightType + ")";
    }

    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Flight ID: ").append(id)
          .append("\nFlight Number: ").append(flightNumber)
          .append("\nFrom: ").append(origin)
          .append("\nTo: ").append(destination)
          .append("\nDeparture Date: ").append(departureDate.format(dtf))
          .append("\nFlight Type: ").append(flightType)
          .append("\nTotal Capacity: ").append(capacity)
          .append("\nNumber of Passengers: ").append(passengers.size());

        // Show class information
        sb.append("\n\nClass Information:");
        for (CommercialClassType classType : getAvailableClasses()) {
            sb.append("\n - ").append(classType.getClassName())
              .append(": £").append(classPrices.get(classType))
              .append(" (Capacity: ").append(classCapacities.get(classType)).append(")");
        }

        if (!passengers.isEmpty()) {
            sb.append("\n\nPassenger List:");
            for (Customer customer : passengers) {
                sb.append("\n - ").append(customer.getDetailsShort());
            }
        }

        return sb.toString();
    }

    public void addPassenger(Customer passenger) throws FlightBookingSystemException {
        if (passengers.size() >= capacity) {
            throw new FlightBookingSystemException("Flight is at full capacity.");
        }
        passengers.add(passenger);
    }

    public void removePassenger(Customer passenger) {
        passengers.remove(passenger);
    }
}