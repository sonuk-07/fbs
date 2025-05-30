package bcu.cmp5332.bookingsystem.model;

import java.math.BigDecimal;

public class Meal {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private MealType type; // NEW FIELD: Meal Type (e.g., VEG, NON_VEG)
    private boolean isDeleted;

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

    // Helper methods for display (UPDATED)
    public String getDetailsShort() {
        return id + ": " + name + " (" + type.getDisplayName() + ") " + " (£" + price + ")";
    }

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
