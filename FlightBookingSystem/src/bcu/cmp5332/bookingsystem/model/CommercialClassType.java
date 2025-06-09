package bcu.cmp5332.bookingsystem.model;

/**
 * Defines the different commercial class types available for flights.
 * Each class type has a display name and a price multiplier relative to the base economy price.
 */

public enum CommercialClassType {
    ECONOMY("Economy"),
    PREMIUM_ECONOMY("Premium Economy"),
    BUSINESS("Business"),
    FIRST("First");

    private final String className;
    private final double multiplier;

    /**
     * Constructs a CommercialClassType enum entry.
     * The multiplier is determined by the class name to calculate the base price.
     * @param className The display name of the commercial class (e.g., "Economy").
     */
    
    CommercialClassType(String className) {
        this.className = className;
        switch (className) {
            case "Economy":
                this.multiplier = 1.0;
                break;
            case "Premium Economy":
                this.multiplier = 1.5;
                break;
            case "Business":
                this.multiplier = 2.5;
                break;
            case "First":
                this.multiplier = 4.0;
                break;
            default:
                this.multiplier = 1.0;
        }
    }
    
    /**
     * Gets the display name of the commercial class.
     * @return The string name of the class.
     */
    

    public String getClassName() {
        return className;
    }


    /**
     * Gets the price multiplier for this commercial class.
     * This multiplier is applied to the base economy price to determine the class-specific base price.
     * @return The double multiplier for the class price.
     */
    
    public double getMultiplier() {
        return multiplier;
    }
}
