package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class FlightDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/flights.txt";
    private final String SEPARATOR = "::";
    private final String MAP_ENTRY_SEPARATOR = ",";
    private final String KEY_VALUE_SEPARATOR = ":";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) {
                    line_idx++;
                    continue;
                }

                String[] properties = line.split(SEPARATOR, -1);
                
                int id;
                String flightNumber;
                String origin;
                String destination;
                LocalDate departureDate;
                BigDecimal economyPrice;
                int totalCapacity;
                FlightType flightType;
                Map<CommercialClassType, Integer> classCapacities = null;
                boolean deleted = false;
                Map<CommercialClassType, Integer> occupiedSeatsByClass = new HashMap<>();

                try {
                    id = Integer.parseInt(properties[0].trim());
                    flightNumber = properties[1].trim();
                    origin = properties[2].trim();
                    destination = properties[3].trim();
                    
                    try {
                        departureDate = LocalDate.parse(properties[4].trim());
                    } catch (DateTimeParseException e) {
                        throw new FlightBookingSystemException("Invalid departure date format for flight ID " + id + " on line " + line_idx + ": '" + properties[4].trim() + "' - " + e.getMessage(), e);
                    }
                    
                    economyPrice = new BigDecimal(properties[5].trim());
                    totalCapacity = Integer.parseInt(properties[6].trim());
                    flightType = FlightType.valueOf(properties[7].trim());

                    if (flightType == FlightType.COMMERCIAL) {
                        if (properties.length > 8 && !properties[8].trim().isEmpty()) {
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
                        } else {
                            classCapacities = null;
                        }
                    }

                    if (properties.length > 9) {
                        deleted = Boolean.parseBoolean(properties[9].trim());
                    }

                    if (properties.length > 10 && !properties[10].trim().isEmpty()) {
                        occupiedSeatsByClass = new HashMap<>();
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
                    
                    Flight flight;
                    if (flightType == FlightType.BUDGET) {
                        flight = new Flight(id, flightNumber, origin, destination,
                                            departureDate, economyPrice, totalCapacity);
                    } else {
                        flight = new Flight(id, flightNumber, origin, destination,
                                            departureDate, economyPrice, totalCapacity,
                                            flightType, classCapacities);
                    }

                    flight.setDeleted(deleted);
                    if (!occupiedSeatsByClass.isEmpty()) {
                        flight.setOccupiedSeatsByClass(occupiedSeatsByClass);
                    }

                    fbs.addFlight(flight);

                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Error parsing numeric value for flight on line " + line_idx + ": " + e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    throw new FlightBookingSystemException("Error parsing enum value for flight on line " + line_idx + ": " + e.getMessage(), e);
                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Unknown error parsing flight on line " + line_idx + ": " + ex.getMessage(), ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Flight flight : fbs.getAllFlights()) {
                out.print(flight.getId());
                out.print(SEPARATOR + flight.getFlightNumber());
                out.print(SEPARATOR + flight.getOrigin());
                out.print(SEPARATOR + flight.getDestination());
                
                if (flight.getDepartureDate() != null) {
                    out.print(SEPARATOR + flight.getDepartureDate());
                } else {
                    out.print(SEPARATOR + "null");
                }
                
                out.print(SEPARATOR + flight.getEconomyPrice());
                out.print(SEPARATOR + flight.getCapacity());
                out.print(SEPARATOR + flight.getFlightType());

                StringBuilder classCapacitiesSb = new StringBuilder();
                if (flight.getFlightType() == FlightType.COMMERCIAL) {
                    Map<CommercialClassType, Integer> capacities = flight.getClassCapacities();
                    List<Map.Entry<CommercialClassType, Integer>> sortedCapacities = new ArrayList<>(capacities.entrySet());
                    sortedCapacities.sort(Comparator.comparing(entry -> entry.getKey().ordinal()));
                    
                    boolean first = true;
                    for (Map.Entry<CommercialClassType, Integer> entry : sortedCapacities) {
                        if (!first) {
                            classCapacitiesSb.append(MAP_ENTRY_SEPARATOR);
                        }
                        classCapacitiesSb.append(entry.getKey().name()).append(KEY_VALUE_SEPARATOR).append(entry.getValue());
                        first = false;
                    }
                }
                out.print(SEPARATOR + classCapacitiesSb.toString());

                out.print(SEPARATOR + flight.isDeleted());

                StringBuilder occupiedSeatsSb = new StringBuilder();
                Map<CommercialClassType, Integer> occupiedSeats = flight.getOccupiedSeatsMap();
                List<Map.Entry<CommercialClassType, Integer>> sortedOccupiedSeats = new ArrayList<>(occupiedSeats.entrySet());
                sortedOccupiedSeats.sort(Comparator.comparing(entry -> entry.getKey().ordinal()));

                boolean firstOccupied = true;
                for (Map.Entry<CommercialClassType, Integer> entry : sortedOccupiedSeats) {
                    if (!firstOccupied) {
                        occupiedSeatsSb.append(MAP_ENTRY_SEPARATOR);
                    }
                    occupiedSeatsSb.append(entry.getKey().name()).append(KEY_VALUE_SEPARATOR).append(entry.getValue());
                    firstOccupied = false;
                }
                out.println(SEPARATOR + occupiedSeatsSb.toString());
            }
        }
    }
}