package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.math.RoundingMode;
import java.util.*;

public class Flight {

    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private BigDecimal economyPrice;
    private int capacity;
    private FlightType flightType;
    private Map<CommercialClassType, Integer> classCapacities;
    private Map<CommercialClassType, BigDecimal> classPrices;
    private Map<CommercialClassType, Integer> occupiedSeatsByClass;

    private final Set<Customer> passengers;
    private boolean deleted = false;

    // Constructor for Budget flights (single class)
    public Flight(int id, String flightNumber, String origin, String destination,
                  LocalDate departureDate, BigDecimal economyPrice, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = capacity;
        this.flightType = FlightType.BUDGET;
        this.classCapacities = createBudgetCapacities(capacity);
        this.passengers = new HashSet<>();
        this.occupiedSeatsByClass = new HashMap<>();
        this.occupiedSeatsByClass.put(CommercialClassType.ECONOMY, 0);

        calculateClassPrices();
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
        this.classCapacities = (classCapacities != null) ? new HashMap<>(classCapacities) : createDefaultCapacities(totalCapacity);
        this.passengers = new HashSet<>();
        this.occupiedSeatsByClass = new HashMap<>();
        for (CommercialClassType classType : CommercialClassType.values()) {
            this.occupiedSeatsByClass.put(classType, 0);
        }

        calculateClassPrices();
    }

    private static Map<CommercialClassType, Integer> createBudgetCapacities(int totalCapacity) {
        Map<CommercialClassType, Integer> capacities = new HashMap<>();
        capacities.put(CommercialClassType.ECONOMY, totalCapacity);
        return capacities;
    }

    private static Map<CommercialClassType, Integer> createDefaultCapacities(int totalCapacity) {
        Map<CommercialClassType, Integer> capacities = new HashMap<>();
        capacities.put(CommercialClassType.ECONOMY, (int) (totalCapacity * 0.6));
        capacities.put(CommercialClassType.PREMIUM_ECONOMY, (int) (totalCapacity * 0.2));
        capacities.put(CommercialClassType.BUSINESS, (int) (totalCapacity * 0.15));
        capacities.put(CommercialClassType.FIRST, (int) (totalCapacity * 0.05));
        int sum = capacities.values().stream().mapToInt(Integer::intValue).sum();
        if (sum != totalCapacity) {
            capacities.put(CommercialClassType.ECONOMY, capacities.get(CommercialClassType.ECONOMY) + (totalCapacity - sum));
        }
        return capacities;
    }

    private void calculateClassPrices() {
        this.classPrices = new HashMap<>();
        if (flightType == FlightType.BUDGET) {
            classPrices.put(CommercialClassType.ECONOMY, economyPrice);
        } else {
            for (CommercialClassType classType : CommercialClassType.values()) {
                // FIX: Convert double to BigDecimal before multiplication
                BigDecimal multiplier = BigDecimal.valueOf(classType.getMultiplier());
                BigDecimal classPrice = economyPrice.multiply(multiplier);
                classPrices.put(classType, classPrice);
            }
        }
    }

    public List<CommercialClassType> getAvailableClasses() {
        if (flightType == FlightType.BUDGET) {
            return Arrays.asList(CommercialClassType.ECONOMY);
        } else {
            return Arrays.asList(CommercialClassType.values());
        }
    }

    public BigDecimal getPriceForClass(CommercialClassType classType) throws FlightBookingSystemException {
        if (!classPrices.containsKey(classType)) {
            throw new FlightBookingSystemException("Class " + classType.getClassName() +
                " is not available for this flight type");
        }
        return classPrices.get(classType);
    }

    public int getCapacityForClass(CommercialClassType classType) {
        return classCapacities.getOrDefault(classType, 0);
    }

    public int getRemainingSeatsForClass(CommercialClassType classType) {
        int totalCapacity = getCapacityForClass(classType);
        int occupied = occupiedSeatsByClass.getOrDefault(classType, 0);
        return totalCapacity - occupied;
    }

    public boolean isClassAvailable(CommercialClassType classType) {
        if (flightType == FlightType.BUDGET) {
            return classType == CommercialClassType.ECONOMY && getRemainingSeatsForClass(CommercialClassType.ECONOMY) > 0;
        }
        return classCapacities.containsKey(classType) && getRemainingSeatsForClass(classType) > 0;
    }

    public boolean hasAnySeatsLeft() {
        return passengers.size() < capacity;
    }

    public BigDecimal getDynamicPrice(CommercialClassType classType, LocalDate systemDate) throws FlightBookingSystemException {
        BigDecimal basePrice = getPriceForClass(classType);
        long daysLeft = ChronoUnit.DAYS.between(systemDate, departureDate);

        BigDecimal dynamicPrice = basePrice;
        if (daysLeft <= 7) {
            dynamicPrice = dynamicPrice.multiply(new BigDecimal("1.5"));
        } else if (daysLeft <= 30) {
            dynamicPrice = dynamicPrice.multiply(new BigDecimal("1.2"));
        }

        int remainingSeats = getRemainingSeatsForClass(classType);
        int classTotalCapacity = getCapacityForClass(classType);
        if (classTotalCapacity > 0) {
            double occupancyRate = (double) (classTotalCapacity - remainingSeats) / classTotalCapacity;
            if (occupancyRate > 0.8) {
                dynamicPrice = dynamicPrice.multiply(new BigDecimal("1.3"));
            } else if (occupancyRate > 0.6) {
                dynamicPrice = dynamicPrice.multiply(new BigDecimal("1.1"));
            }
        }
        return dynamicPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean hasDeparted(LocalDate systemDate) {
        return departureDate.isBefore(systemDate) || departureDate.isEqual(systemDate);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public BigDecimal getEconomyPrice() { return economyPrice; }
    public void setEconomyPrice(BigDecimal economyPrice) {
        this.economyPrice = economyPrice;
        calculateClassPrices();
    }
    public BigDecimal getPrice() { return economyPrice; }
    public void setPrice(BigDecimal price) { setEconomyPrice(price); }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
        calculateClassPrices();
    }
    public String getFlightClass() { return flightType.toString(); }
    public void setFlightClass(String flightClass) {
        this.flightType = FlightType.valueOf(flightClass.toUpperCase());
        calculateClassPrices();
    }
    public Map<CommercialClassType, Integer> getClassCapacities() { return new HashMap<>(classCapacities); }
    public Map<CommercialClassType, BigDecimal> getClassPrices() { return new HashMap<>(classPrices); }
    public List<Customer> getPassengers() { return new ArrayList<>(passengers); }
    public int getOccupiedSeatsByClass(CommercialClassType classType) { return occupiedSeatsByClass.getOrDefault(classType, 0); }
    public void setOccupiedSeatsByClass(Map<CommercialClassType, Integer> occupiedSeatsByClass) { this.occupiedSeatsByClass = occupiedSeatsByClass; }
    public Map<CommercialClassType, Integer> getOccupiedSeatsMap() { return new HashMap<>(occupiedSeatsByClass); }


    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " +
               destination + " on " + departureDate.format(dtf) + " (" + flightType + ")";
    }

    public String getDetailsLong(LocalDate systemDate) throws FlightBookingSystemException {
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

        sb.append("\n\nClass Information:");
        for (CommercialClassType classType : getAvailableClasses()) {
            sb.append("\n - ").append(classType.getClassName())
              .append(": Base £").append(classPrices.get(classType))
              .append(" | Current Price: £").append(getDynamicPrice(classType, systemDate))
              .append(" (Remaining Seats: ").append(getRemainingSeatsForClass(classType)).append(" / Total: ").append(classCapacities.get(classType)).append(")");
        }

        sb.append("\n\nNo specific meals assigned to this flight.");

        if (!passengers.isEmpty()) {
            sb.append("\n\nPassenger List:");
            for (Customer customer : passengers) {
                sb.append("\n - ").append(customer.getDetailsShort());
            }
        }

        return sb.toString();
    }

    public void addPassenger(Customer passenger, CommercialClassType classType) throws FlightBookingSystemException {
        if (!isClassAvailable(classType)) {
            throw new FlightBookingSystemException("Flight " + flightNumber + " " + classType.getClassName() + " class is at full capacity.");
        }
        if (passengers.contains(passenger)) {
             throw new FlightBookingSystemException("Customer is already a passenger on this flight.");
        }

        passengers.add(passenger);
        occupiedSeatsByClass.put(classType, occupiedSeatsByClass.getOrDefault(classType, 0) + 1);
    }

    public void removePassenger(Customer passenger, CommercialClassType classType) {
        if (passengers.remove(passenger)) {
            occupiedSeatsByClass.put(classType, occupiedSeatsByClass.getOrDefault(classType, 0) - 1);
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
