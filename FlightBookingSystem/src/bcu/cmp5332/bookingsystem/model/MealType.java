package bcu.cmp5332.bookingsystem.model;

public enum MealType {
    VEG("Vegetarian"),
    NON_VEG("Non-Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten-Free"),
    NONE("None"); // This value is correctly here

    private final String displayName;

    MealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
