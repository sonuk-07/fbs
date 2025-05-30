package bcu.cmp5332.bookingsystem.model;

public enum CommercialClassType {
    ECONOMY("Economy"),
    PREMIUM_ECONOMY("Premium Economy"),
    BUSINESS("Business"),
    FIRST("First");
    // Do NOT add a 'NONE' value here, as it's not a valid booking class.

    private final String className;
    private final double multiplier;

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

    public String getClassName() {
        return className;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
