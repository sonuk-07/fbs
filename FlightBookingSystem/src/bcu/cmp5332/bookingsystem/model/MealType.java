package bcu.cmp5332.bookingsystem.model;


/**
 * Defines the different types of meals available, primarily for dietary preferences.
 * Each meal type has a display name for user-friendly presentation.
 */

public enum MealType {
    VEG("Vegetarian"),
    NON_VEG("Non-Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten-Free"),
    NONE("None"); // This value is correctly here

    private final String displayName;

    
    /**
     * Constructs a MealType enum entry.
     * @param displayName The user-friendly name for the meal type.
     */
    
    MealType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the meal type.
     * @return The user-friendly string name of the meal type.
     */
    
    public String getDisplayName() {
        return displayName;
    }
}
