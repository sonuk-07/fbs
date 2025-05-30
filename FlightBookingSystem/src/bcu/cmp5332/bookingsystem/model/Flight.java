package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.math.RoundingMode;
import java.util.*;


/**
 * Represents a flight in the booking system, including its details,
 * capacity, pricing, and passenger information.
 * Flights can be of type BUDGET (single class) or COMMERCIAL (multiple classes).
 */

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
	
    /**
     * Constructor for creating a new Budget flight.
     * Budget flights only have an Economy class.
     *
     * @param id The unique ID of the flight.
     * @param flightNumber The flight's alphanumeric identifier.
     * @param origin The departure airport/city.
     * @param destination The arrival airport/city.
     * @param departureDate The scheduled date of departure.
     * @param economyPrice The base price for the Economy class.
     * @param capacity The total passenger capacity for the flight (all Economy).
     */
	

	// Constructor for Budget flights (single class)
	public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate,
			BigDecimal economyPrice, int capacity) {
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

	   /**
     * Constructor for creating a new Commercial flight.
     * Commercial flights support multiple commercial classes (Economy, Premium Economy, Business, First).
     *
     * @param id The unique ID of the flight.
     * @param flightNumber The flight's alphanumeric identifier.
     * @param origin The departure airport/city.
     * @param destination The arrival airport/city.
     * @param departureDate The scheduled date of departure.
     * @param economyPrice The base price for the Economy class, used to derive other class prices.
     * @param totalCapacity The total passenger capacity across all classes.
     * @param flightType The type of flight (must be COMMERCIAL for this constructor's intended use).
     * @param classCapacities A map defining the capacity for each {@link CommercialClassType}.
     * If null, default capacities are calculated based on totalCapacity.
     */
	
	// Constructor for Commercial flights (multiple classes)
	public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate,
			BigDecimal economyPrice, int totalCapacity, FlightType flightType,
			Map<CommercialClassType, Integer> classCapacities) {
		this.id = id;
		this.flightNumber = flightNumber;
		this.origin = origin;
		this.destination = destination;
		this.departureDate = departureDate;
		this.economyPrice = economyPrice;
		this.capacity = totalCapacity;
		this.flightType = flightType;
		this.classCapacities = (classCapacities != null) ? new HashMap<>(classCapacities)
				: createDefaultCapacities(totalCapacity);
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
			capacities.put(CommercialClassType.ECONOMY,
					capacities.get(CommercialClassType.ECONOMY) + (totalCapacity - sum));
		}
		return capacities;
	}
	
    /**
     * Helper method to calculate and store the base prices for each commercial class.
     * For Budget flights, only Economy price is set. For Commercial flights,
     * prices for all classes are derived from the economy price using their respective multipliers.
     */

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
	
    /**
     * Gets the base price for a specific commercial class on this flight.
     * @param classType The {@link CommercialClassType} to get the price for.
     * @return The {@link BigDecimal} base price for the given class.
     * @throws FlightBookingSystemException If the requested class is not available for this flight type.
     */
	

	public BigDecimal getPriceForClass(CommercialClassType classType) throws FlightBookingSystemException {
		if (!classPrices.containsKey(classType)) {
			throw new FlightBookingSystemException(
					"Class " + classType.getClassName() + " is not available for this flight type");
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

    /**
     * Checks if a specific commercial class is available for booking (i.e., has remaining seats).
     * @param classType The {@link CommercialClassType} to check.
     * @return true if the class is available and has at least one seat, false otherwise.
     */
	
	public boolean isClassAvailable(CommercialClassType classType) {
		if (flightType == FlightType.BUDGET) {
			return classType == CommercialClassType.ECONOMY
					&& getRemainingSeatsForClass(CommercialClassType.ECONOMY) > 0;
		}
		return classCapacities.containsKey(classType) && getRemainingSeatsForClass(classType) > 0;
	}

	public boolean hasAnySeatsLeft() {
		return passengers.size() < capacity;
	}
	
    /**
     * Calculates the dynamic price for a given commercial class based on the system date
     * and current occupancy. Price can increase closer to departure or with higher occupancy.
     *
     * @param classType The {@link CommercialClassType} for which to calculate the price.
     * @param systemDate The current date of the system, used to determine days until departure.
     * @return The dynamically calculated {@link BigDecimal} price for the class, rounded to 2 decimal places.
     * @throws FlightBookingSystemException If the requested class is not available for this flight type.
     */
	

	public BigDecimal getDynamicPrice(CommercialClassType classType, LocalDate systemDate)
			throws FlightBookingSystemException {
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
	
    /**
     * Checks if the flight has already departed or departs on the current system date.
     * @param systemDate The current date of the system.
     * @return true if the departure date is on or before the system date, false otherwise.
     */
	

	public boolean hasDeparted(LocalDate systemDate) {
		return departureDate.isBefore(systemDate) || departureDate.isEqual(systemDate);
	}

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
		calculateClassPrices();
	}

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
		calculateClassPrices();
	}

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

	public int getOccupiedSeatsByClass(CommercialClassType classType) {
		return occupiedSeatsByClass.getOrDefault(classType, 0);
	}

	public void setOccupiedSeatsByClass(Map<CommercialClassType, Integer> occupiedSeatsByClass) {
		this.occupiedSeatsByClass = occupiedSeatsByClass;
	}

	public Map<CommercialClassType, Integer> getOccupiedSeatsMap() {
		return new HashMap<>(occupiedSeatsByClass);
	}
	
    /**
     * Returns a short string representation of the flight details.
     * This includes ID, flight number, origin, destination, departure date, and flight type.
     * @return A {@link String} containing a concise summary of the flight.
     */
	

	public String getDetailsShort() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " + destination + " on "
				+ departureDate.format(dtf) + " (" + flightType + ")";
	}

    /**
     * Returns a long string representation of the flight details.
     * This provides a comprehensive overview including all flight attributes,
     * class-specific capacities, base and dynamic prices, remaining seats,
     * and a list of current passengers.
     * @param systemDate The current system date, used for dynamic price calculation.
     * @return A {@link String} containing detailed information about the flight.
     * @throws FlightBookingSystemException If there's an issue getting the dynamic price for a class.
     */
	
	public String getDetailsLong(LocalDate systemDate) throws FlightBookingSystemException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		StringBuilder sb = new StringBuilder();
		sb.append("Flight ID: ").append(id).append("\nFlight Number: ").append(flightNumber).append("\nFrom: ")
				.append(origin).append("\nTo: ").append(destination).append("\nDeparture Date: ")
				.append(departureDate.format(dtf)).append("\nFlight Type: ").append(flightType)
				.append("\nTotal Capacity: ").append(capacity).append("\nNumber of Passengers: ")
				.append(passengers.size());

		sb.append("\n\nClass Information:");
		for (CommercialClassType classType : getAvailableClasses()) {
			sb.append("\n - ").append(classType.getClassName()).append(": Base £").append(classPrices.get(classType))
					.append(" | Current Price: £").append(getDynamicPrice(classType, systemDate))
					.append(" (Remaining Seats: ").append(getRemainingSeatsForClass(classType)).append(" / Total: ")
					.append(classCapacities.get(classType)).append(")");
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

    /**
     * Adds a passenger to the flight in a specific commercial class.
     * This method increments the occupied seats count for the given class.
     *
     * @param passenger The {@link Customer} to be added as a passenger.
     * @param classType The {@link CommercialClassType} the passenger is booking in.
     * @throws FlightBookingSystemException If the chosen class is full or the customer is already a passenger.
     */
	
	public void addPassenger(Customer passenger, CommercialClassType classType) throws FlightBookingSystemException {
		if (!isClassAvailable(classType)) {
			throw new FlightBookingSystemException(
					"Flight " + flightNumber + " " + classType.getClassName() + " class is at full capacity.");
		}
		if (passengers.contains(passenger)) {
			throw new FlightBookingSystemException("Customer is already a passenger on this flight.");
		}

		passengers.add(passenger);
		occupiedSeatsByClass.put(classType, occupiedSeatsByClass.getOrDefault(classType, 0) + 1);
	}
	
    /**
     * Removes a passenger from the flight.
     * This method decrements the occupied seats count for the class the passenger was in.
     *
     * @param passenger The {@link Customer} to be removed.
     * @param classType The {@link CommercialClassType} the passenger was originally booked in.
     * (Needed to correctly decrement occupied seats by class.)
     */
	

	public void removePassenger(Customer passenger, CommercialClassType classType) {
		if (passengers.remove(passenger)) {
			occupiedSeatsByClass.put(classType, occupiedSeatsByClass.getOrDefault(classType, 0) - 1);
		}
	}

	
	  /**
     * Checks if the flight is marked as deleted. Deleted flights are typically hidden from active lists.
     * @return true if the flight is deleted, false otherwise.
     */
	
	public boolean isDeleted() {
		return deleted;
	}
	

    /**
     * Sets the deletion status of the flight.
     * @param deleted true to mark the flight as deleted, false to mark it as active.
     */
	

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
