package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
// import java.time.LocalDateTime; // No longer needed for date parsing if file is YYYY-MM-DD
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FlightDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/flights.txt";
    private final String SEPARATOR = "\\|";
    private final String OUTPUT_SEPARATOR = "|";
    private final String MAP_ENTRY_SEPARATOR = ",";
    private final String KEY_VALUE_SEPARATOR = ":";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    // It's good practice to trim all input strings from file
                    int id = Integer.parseInt(properties[0].trim());
                    String flightNumber = properties[1].trim();
                    String origin = properties[2].trim();
                    String destination = properties[3].trim();

                    // THIS IS THE CRUCIAL PART FOR THIS ERROR
                    // Even if printout looks clean, there might be invisible chars
                    LocalDate departureDate = LocalDate.parse(properties[4].trim());

                    BigDecimal economyPrice = new BigDecimal(properties[5].trim());
                    int totalCapacity = Integer.parseInt(properties[6].trim());
                    FlightType flightType = FlightType.valueOf(properties[7].trim());

                    Map<CommercialClassType, Integer> classCapacities = null;
                    // Ensure that there are enough properties for classCapacities string
                    if (flightType == FlightType.COMMERCIAL && properties.length > 8 && !properties[8].trim().isEmpty()) {
                        classCapacities = new HashMap<>();
                        String capacitiesString = properties[8].trim(); // Trim the whole string too
                        String[] entries = capacitiesString.split(MAP_ENTRY_SEPARATOR);
                        for (String entry : entries) {
                            String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
                            if (keyValue.length == 2) {
                                CommercialClassType classType = CommercialClassType.valueOf(keyValue[0].trim()); // Trim key
                                int capacity = Integer.parseInt(keyValue[1].trim()); // Trim value
                                classCapacities.put(classType, capacity);
                            }
                        }
                    }

                    Flight flight = new Flight(id, flightNumber, origin, destination,
                                               departureDate, economyPrice, totalCapacity,
                                               flightType, classCapacities);
                    fbs.addFlight(flight);
                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing flight on line " + line_idx + ": " + ex.getMessage());
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Flight flight : fbs.getFlights()) {
                out.print(flight.getId() + OUTPUT_SEPARATOR);
                out.print(flight.getFlightNumber() + OUTPUT_SEPARATOR);
                out.print(flight.getOrigin() + OUTPUT_SEPARATOR);
                out.print(flight.getDestination() + OUTPUT_SEPARATOR);
                out.print(flight.getDepartureDate() + OUTPUT_SEPARATOR);
                out.print(flight.getEconomyPrice() + OUTPUT_SEPARATOR);
                out.print(flight.getCapacity() + OUTPUT_SEPARATOR);
                out.print(flight.getFlightType() + OUTPUT_SEPARATOR);

                if (flight.getFlightType() == FlightType.COMMERCIAL) {
                    Map<CommercialClassType, Integer> capacities = flight.getClassCapacities();
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;
                    for (Map.Entry<CommercialClassType, Integer> entry : capacities.entrySet()) {
                        if (!first) {
                            sb.append(MAP_ENTRY_SEPARATOR);
                        }
                        sb.append(entry.getKey().name()).append(KEY_VALUE_SEPARATOR).append(entry.getValue());
                        first = false;
                    }
                    out.println(sb.toString());
                } else {
                    out.println();
                }
            }
        }
    }
}