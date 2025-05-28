package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FlightDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/flights.txt";
    private final String SEPARATOR = "::";
    private final String MAP_ENTRY_SEPARATOR = ","; // For class capacities and occupied seats
    private final String KEY_VALUE_SEPARATOR = ":"; // For class capacities and occupied seats

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0].trim());
                    String flightNumber = properties[1].trim();
                    String origin = properties[2].trim();
                    String destination = properties[3].trim();
                    LocalDate departureDate = LocalDate.parse(properties[4].trim());
                    BigDecimal economyPrice = new BigDecimal(properties[5].trim());
                    int totalCapacity = Integer.parseInt(properties[6].trim());
                    FlightType flightType = FlightType.valueOf(properties[7].trim());

                    Map<CommercialClassType, Integer> classCapacities = null;
                    if (flightType == FlightType.COMMERCIAL && properties.length > 8 && !properties[8].trim().isEmpty()) {
                        classCapacities = new HashMap<>();
                        String capacitiesString = properties[8].trim();
                        String[] entries = capacitiesString.split(MAP_ENTRY_SEPARATOR);
                        for (String entry : entries) {
                            String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
                            if (keyValue.length == 2) {
                                CommercialClassType classType = CommercialClassType.valueOf(keyValue[0].trim());
                                int capacity = Integer.parseInt(keyValue[1].trim());
                                classCapacities.put(classType, capacity);
                            }
                        }
                    }

                    boolean deleted = false;
                    if (properties.length > 9) { // Check for 'deleted' property
                        deleted = Boolean.parseBoolean(properties[9].trim());
                    }

                    // NEW: Load occupied seats by class
                    Map<CommercialClassType, Integer> occupiedSeatsByClass = new HashMap<>();
                    if (properties.length > 10 && !properties[10].trim().isEmpty()) {
                        String occupiedSeatsString = properties[10].trim();
                        String[] entries = occupiedSeatsString.split(MAP_ENTRY_SEPARATOR);
                        for (String entry : entries) {
                            String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
                            if (keyValue.length == 2) {
                                CommercialClassType classType = CommercialClassType.valueOf(keyValue[0].trim());
                                int count = Integer.parseInt(keyValue[1].trim());
                                occupiedSeatsByClass.put(classType, count);
                            }
                        }
                    }


                    Flight flight = new Flight(id, flightNumber, origin, destination,
                                               departureDate, economyPrice, totalCapacity,
                                               flightType, classCapacities);
                    flight.setDeleted(deleted);
                    flight.setOccupiedSeatsByClass(occupiedSeatsByClass); // Set occupied seats

                    fbs.addFlight(flight);

                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing flight on line " + line_idx + ": " + ex.getMessage(), ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Flight flight : fbs.getAllFlights()) { // Iterate over all flights (including deleted)
                out.print(flight.getId() + SEPARATOR);
                out.print(flight.getFlightNumber() + SEPARATOR);
                out.print(flight.getOrigin() + SEPARATOR);
                out.print(flight.getDestination() + SEPARATOR);
                out.print(flight.getDepartureDate() + SEPARATOR);
                out.print(flight.getEconomyPrice() + SEPARATOR);
                out.print(flight.getCapacity() + SEPARATOR);
                out.print(flight.getFlightType() + SEPARATOR);

                // Store class capacities
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
                    out.print(sb.toString());
                }
                out.print(SEPARATOR); // Always output separator for class capacities, even if empty

                // Store deleted status
                out.print(flight.isDeleted() + SEPARATOR);

                // NEW: Store occupied seats by class
                Map<CommercialClassType, Integer> occupiedSeats = flight.getOccupiedSeatsMap();
                StringBuilder sbOccupied = new StringBuilder();
                boolean firstOccupied = true;
                for (Map.Entry<CommercialClassType, Integer> entry : occupiedSeats.entrySet()) {
                    if (!firstOccupied) {
                        sbOccupied.append(MAP_ENTRY_SEPARATOR);
                    }
                    sbOccupied.append(entry.getKey().name()).append(KEY_VALUE_SEPARATOR).append(entry.getValue());
                    firstOccupied = false;
                }
                out.print(sbOccupied.toString());

                out.println();
            }
        }
    }
}
