package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal;

/**
 * Represents a meal option available for selection on flights.
 * Each meal has an ID, name, description, price, and a specific {@link MealType}.
 */

public class Meal {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private MealType type; // NEW FIELD: Meal Type (e.g., VEG, NON_VEG)
    private boolean isDeleted;

    
    /**
     * Constructs a new Meal object.
     *
     * @param id The unique ID of the meal.
     * @param name The name of the meal (e.g., "Chicken Biryani").
     * @param description A brief description of the meal.
     * @param price The cost of the meal.
     * @param type The {@link MealType} (e.g., VEG, NON_VEG) of the meal.
     */
    
    
    // UPDATED CONSTRUCTOR
    public Meal(int id, String name, String description, BigDecimal price, MealType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type; // Initialize new field
        this.isDeleted = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // NEW GETTER for MealType
    public MealType getType() {
        return type;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // NEW SETTER for MealType
    public void setType(MealType type) {
        this.type = type;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    
    /**
     * Returns a short string representation of the meal details.
     * This includes ID, name, type, and price.
     * @return A {@link String} containing a concise summary of the meal.
     */
    
    
    // Helper methods for display (UPDATED)
    public String getDetailsShort() {
        return id + ": " + name + " (" + type.getDisplayName() + ") " + "(£" + price + ")";
    }
    

    
    /**
     * Returns a long string representation of the meal details.
     * This provides a comprehensive overview including all meal attributes
     * and its deletion status.
     * @return A {@link String} containing detailed information about the meal.
     */
    
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal ID: ").append(id)
          .append("\nName: ").append(name)
          .append("\nDescription: ").append(description)
          .append("\nPrice: £").append(price)
          .append("\nType: ").append(type.getDisplayName()); // Display meal type
        if (isDeleted) {
            sb.append(" (DELETED)");
        }
        return sb.toString();
    }
}
