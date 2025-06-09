package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a booking made by a customer in the flight booking system. A
 * booking can include an outbound flight and an optional return flight, a
 * specific commercial class, an optional meal, and associated prices and fees.
 */

public class Booking {

	private int id; // ADDED: Unique identifier for the booking
	private Customer customer;
	private Flight outboundFlight;
	private Flight returnFlight;
	private LocalDate bookingDate;
	private CommercialClassType bookedClass;
	private BigDecimal bookedPriceOutbound;
	private BigDecimal bookedPriceReturn;
	private BigDecimal cancellationFee;
	private BigDecimal rebookFee;
	private Meal meal;
	private boolean isCancelled; // ADDED: Flag to indicate if the booking is cancelled

	/**
	 * Constructs a new Booking object with comprehensive details. This is the
	 * primary constructor for creating a new booking, supporting both one-way and
	 * round-trip bookings, along with meal selection.
	 *
	 * @param id The unique identifier for this booking.
	 * @param customer            The {@link Customer} who made this booking.
	 * @param outboundFlight      The {@link Flight} for the outbound journey.
	 *                            Cannot be null.
	 * @param returnFlight        The {@link Flight} for the return journey. Can be
	 *                            null for one-way bookings.
	 * @param bookingDate         The {@link LocalDate} when the booking was made.
	 * @param bookedClass         The {@link CommercialClassType} chosen for this
	 *                            booking.
	 * @param bookedPriceOutbound The price of the outbound flight at the time of
	 *                            booking.
	 * @param bookedPriceReturn   The price of the return flight at the time of
	 *                            booking. Is {@link BigDecimal#ZERO} if no return
	 *                            flight.
	 * @param meal                The {@link Meal} selected for this booking. Can be
	 *                            null if no meal is chosen.
	 */

	public Booking(int id, Customer customer, Flight outboundFlight, Flight returnFlight, LocalDate bookingDate,
			CommercialClassType bookedClass, BigDecimal bookedPriceOutbound, BigDecimal bookedPriceReturn, Meal meal) {
		this.id = id; // Set the ID
		this.customer = customer;
		this.outboundFlight = outboundFlight;
		this.returnFlight = returnFlight;
		this.bookingDate = bookingDate;
		this.bookedClass = bookedClass;
		this.bookedPriceOutbound = bookedPriceOutbound;
		this.bookedPriceReturn = bookedPriceReturn;
		this.cancellationFee = BigDecimal.ZERO;
		this.rebookFee = BigDecimal.ZERO;
		this.meal = meal;
		this.isCancelled = false; // Initialize as not cancelled
	}

	/**
	 * Constructs a new Booking object for a simple one-way flight without specific
	 * initial prices or meal. This constructor is a convenience for scenarios where
	 * only the customer, single flight, date, and class are known initially. Prices
	 * and meal can be set later if needed.
	 *
	 * @param id The unique identifier for this booking.
	 * @param customer    The {@link Customer} who made this booking.
	 * @param flight      The {@link Flight} for the journey (assumed to be
	 *                    outbound).
	 * @param bookingDate The {@link LocalDate} when the booking was made.
	 * @param bookedClass The {@link CommercialClassType} chosen for this booking.
	 */

	public Booking(int id, Customer customer, Flight flight, LocalDate bookingDate, CommercialClassType bookedClass) {
		this(id, customer, flight, null, bookingDate, bookedClass, BigDecimal.ZERO, BigDecimal.ZERO, null);
	}

	/**
	 * Gets the unique ID of this booking.
	 * @return The booking ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the customer associated with this booking.
	 * 
	 * @return The {@link Customer} object.
	 */

	public Customer getCustomer() {
		return customer;
	}

	public Flight getOutboundFlight() {
		return outboundFlight;
	}

	public Flight getReturnFlight() {
		return returnFlight;
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

	public BigDecimal getBookedPriceOutbound() {
		return bookedPriceOutbound;
	}

	public BigDecimal getBookedPriceReturn() {
		return bookedPriceReturn;
	}

	public void setBookedPriceOutbound(BigDecimal bookedPriceOutbound) {
		this.bookedPriceOutbound = bookedPriceOutbound;
	}

	public void setBookedPriceReturn(BigDecimal bookedPriceReturn) {
		this.bookedPriceReturn = bookedPriceReturn;
	}

	public BigDecimal getCancellationFee() {
		return cancellationFee;
	}

	public void setCancellationFee(BigDecimal cancellationFee) {
		this.cancellationFee = cancellationFee;
	}

	public BigDecimal getRebookFee() {
		return rebookFee;
	}

	public void setRebookFee(BigDecimal rebookFee) {
		this.rebookFee = rebookFee;
	}

	public Meal getMeal() {
		return meal;
	}

	public void setMeal(Meal meal) {
		this.meal = meal;
	}

	/**
	 * Checks if this booking has been cancelled.
	 * @return true if the booking is cancelled, false otherwise.
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Sets the cancellation status of this booking.
	 * @param cancelled true to mark as cancelled, false otherwise.
	 */
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

 	/**
 	 * Returns a short string representation of the booking details.
 	 * This includes flight numbers, booked class, meal information, and total price.
 	 * @return A {@link String} containing a concise summary of the booking.
 	 */

	// UPDATED getDetailsShort()
	public String getDetailsShort() {
		String mealInfo = (meal != null) ? ", Meal: " + meal.getName() + " (" + meal.getType().getDisplayName() + ")"
				: "";
		String status = isCancelled ? " (CANCELLED)" : ""; // Add status
		return "Booking ID: " + id + status + " - Outbound " + outboundFlight.getFlightNumber() + ", Return "
				+ (returnFlight != null ? returnFlight.getFlightNumber() : "N/A") + " - Class: "
				+ bookedClass.getClassName() + mealInfo + " - Total Price: £" + getTotalBookingPrice();
	}

 	/**
 	 * Returns a long string representation of the booking details.
 	 * This provides a comprehensive overview including customer details,
 	 * full flight details for outbound and return (if applicable),
 	 * booked class, prices, meal details, booking date, and any applied fees.
 	 * @return A {@link String} containing detailed information about the booking.
 	 */


	// UPDATED getDetailsLong()
	public String getDetailsLong() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		StringBuilder sb = new StringBuilder();
		sb.append("Booking ID: ").append(id).append("\n"); // Add booking ID
		sb.append("Status: ").append(isCancelled ? "CANCELLED" : "ACTIVE").append("\n"); // Add status
		sb.append("Customer: ").append(customer.getDetailsShort()).append("\nOutbound Flight: ")
				.append(outboundFlight.getDetailsShort()).append("\n  Booked Class: ")
				.append(bookedClass.getClassName()).append("\n  Booked Price: £").append(bookedPriceOutbound);

		if (returnFlight != null) {
			sb.append("\nReturn Flight: ").append(returnFlight.getDetailsShort()).append("\n  Booked Price: £")
					.append(bookedPriceReturn);
		}

		if (meal != null) {
			sb.append("\nMeal: ").append(meal.getName()).append(" (Type: ").append(meal.getType().getDisplayName()) // Display
																													// meal
																													// type
					.append(", Price: £").append(meal.getPrice()).append(")");
		} else {
			sb.append("\nMeal: None");
		}

		sb.append("\nBooking Date: ").append(bookingDate.format(dtf));
		sb.append("\nTotal Booking Price (Flights + Meal): £").append(getTotalBookingPrice());

		if (cancellationFee.compareTo(BigDecimal.ZERO) > 0) {
			sb.append("\nCancellation Fee Applied: £").append(cancellationFee);
		}
		if (rebookFee.compareTo(BigDecimal.ZERO) > 0) {
			sb.append("\nRebook Fee Applied: £").append(rebookFee);
		}

		return sb.toString();
	}

	public BigDecimal getTotalBookingPrice() {
		BigDecimal total = bookedPriceOutbound.add(bookedPriceReturn);
		if (meal != null) {
			total = total.add(meal.getPrice());
		}
		return total.setScale(2, java.math.RoundingMode.HALF_UP);
	}
}