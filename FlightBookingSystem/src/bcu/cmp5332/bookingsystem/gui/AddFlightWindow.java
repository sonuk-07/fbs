// bcu.cmp5332.bookingsystem.gui.AddFlightWindow.java
package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.data.FlightDataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField economyPriceText = new JTextField();
    private JTextField capacityText = new JTextField();
    private JComboBox<String> flightTypeComboBox = new JComboBox<>(new String[]{"Budget", "Commercial"});

    // For Commercial Flight specific fields
    private JLabel premiumEconomyCapacityLabel = new JLabel("Premium Economy Capacity (% of total): ");
    private JTextField premiumEconomyCapacityText = new JTextField();
    private JLabel businessCapacityLabel = new JLabel("Business Capacity (% of total): ");
    private JTextField businessCapacityText = new JTextField();
    private JLabel firstClassCapacityLabel = new JLabel("First Class Capacity (% of total): ");
    private JTextField firstClassCapacityText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Add a New Flight");
        setSize(480, 450); // Increased size to accommodate new fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Proper window closing behavior

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(11, 2, 5, 5)); // 11 rows for all fields

        topPanel.add(new JLabel("Flight No : "));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin : "));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination : "));
        topPanel.add(destinationText);
        topPanel.add(new JLabel("Departure Date (YYYY-MM-DD) : "));
        topPanel.add(depDateText);
        topPanel.add(new JLabel("Economy Price (£) : "));
        topPanel.add(economyPriceText);
        topPanel.add(new JLabel("Total Capacity : "));
        topPanel.add(capacityText);
        topPanel.add(new JLabel("Flight Type : "));
        topPanel.add(flightTypeComboBox);

        // Add commercial specific fields
        topPanel.add(premiumEconomyCapacityLabel);
        topPanel.add(premiumEconomyCapacityText);
        topPanel.add(businessCapacityLabel);
        topPanel.add(businessCapacityText);
        topPanel.add(firstClassCapacityLabel);
        topPanel.add(firstClassCapacityText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        flightTypeComboBox.addActionListener(this); // Listen for changes in flight type

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        // Initial state for commercial flight fields
        updateCommercialFieldsVisibility();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        } else if (ae.getSource() == flightTypeComboBox) {
            updateCommercialFieldsVisibility();
        }
    }

    /**
     * Updates the visibility of commercial flight specific fields based on the selected flight type.
     */
    private void updateCommercialFieldsVisibility() {
        boolean isCommercial = "Commercial".equals(flightTypeComboBox.getSelectedItem());
        premiumEconomyCapacityLabel.setVisible(isCommercial);
        premiumEconomyCapacityText.setVisible(isCommercial);
        businessCapacityLabel.setVisible(isCommercial);
        businessCapacityText.setVisible(isCommercial);
        firstClassCapacityLabel.setVisible(isCommercial);
        firstClassCapacityText.setVisible(isCommercial);
        
        // Clear the commercial fields when switching to Budget
        if (!isCommercial) {
            premiumEconomyCapacityText.setText("");
            businessCapacityText.setText("");
            firstClassCapacityText.setText("");
        }
        
        // Repaint the panel to reflect visibility changes
        this.repaint();
    }

    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText().trim();
            String origin = originText.getText().trim();
            String destination = destinationText.getText().trim();

            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() ||
                depDateText.getText().trim().isEmpty() || economyPriceText.getText().trim().isEmpty() ||
                capacityText.getText().trim().isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            LocalDate departureDate;
            try {
                departureDate = LocalDate.parse(depDateText.getText().trim());
            } catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Departure Date must be in YYYY-MM-DD format.");
            }

            BigDecimal economyPrice;
            try {
                economyPrice = new BigDecimal(economyPriceText.getText().trim());
                if (economyPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new FlightBookingSystemException("Economy Price cannot be negative.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Economy Price must be a valid number.");
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityText.getText().trim());
                if (capacity <= 0) {
                    throw new FlightBookingSystemException("Capacity must be a positive integer.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Capacity must be a valid integer.");
            }

            FlightType flightType = FlightType.valueOf(((String) flightTypeComboBox.getSelectedItem()).toUpperCase());
            Command addFlightCommand;

            if (flightType == FlightType.BUDGET) {
                addFlightCommand = new AddFlight(flightNumber, origin, destination,
                    departureDate, economyPrice, capacity);
            } else { // Commercial
                // Validate that commercial class fields are filled
                String premiumEconomyText = premiumEconomyCapacityText.getText().trim();
                String businessText = businessCapacityText.getText().trim();
                String firstClassText = firstClassCapacityText.getText().trim();
                
                if (premiumEconomyText.isEmpty() || businessText.isEmpty() || firstClassText.isEmpty()) {
                    throw new FlightBookingSystemException("All class capacity percentages must be filled for commercial flights.");
                }

                Map<CommercialClassType, Integer> classCapacities = new HashMap<>();
                int premiumEconomyPercentage, businessPercentage, firstClassPercentage;

                try {
                    premiumEconomyPercentage = Integer.parseInt(premiumEconomyText);
                    businessPercentage = Integer.parseInt(businessText);
                    firstClassPercentage = Integer.parseInt(firstClassText);

                    if (premiumEconomyPercentage < 0 || businessPercentage < 0 || firstClassPercentage < 0) {
                        throw new FlightBookingSystemException("Class percentages cannot be negative.");
                    }
                    if (premiumEconomyPercentage + businessPercentage + firstClassPercentage > 100) {
                        throw new FlightBookingSystemException("Sum of Premium Economy, Business, and First Class percentages cannot exceed 100%.");
                    }
                } catch (NumberFormatException nfe) {
                    throw new FlightBookingSystemException("Class percentages must be valid integers.");
                }

                // Calculate actual capacities based on percentages
                int premiumEconomyCapacity = (int) Math.round(capacity * (premiumEconomyPercentage / 100.0));
                int businessCapacity = (int) Math.round(capacity * (businessPercentage / 100.0));
                int firstClassCapacity = (int) Math.round(capacity * (firstClassPercentage / 100.0));
                int economyCapacity = capacity - premiumEconomyCapacity - businessCapacity - firstClassCapacity;

                if (economyCapacity < 0) { 
                    economyCapacity = 0;
                }

                classCapacities.put(CommercialClassType.ECONOMY, economyCapacity);
                classCapacities.put(CommercialClassType.PREMIUM_ECONOMY, premiumEconomyCapacity);
                classCapacities.put(CommercialClassType.BUSINESS, businessCapacity);
                classCapacities.put(CommercialClassType.FIRST, firstClassCapacity);

                addFlightCommand = new AddFlight(flightNumber, origin, destination,
                    departureDate, economyPrice, capacity, flightType, classCapacities);
            }

            // Execute the command to add the flight
            addFlightCommand.execute(mw.getFlightBookingSystem(), null);
            
            // Save the data to flights.txt file
            try {
                FlightDataManager dataManager = new FlightDataManager();
                dataManager.storeData(mw.getFlightBookingSystem());
                
                JOptionPane.showMessageDialog(this, 
                    "Flight added successfully and saved to file!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException saveEx) {
                JOptionPane.showMessageDialog(this, 
                    "Flight added successfully but failed to save to file: " + saveEx.getMessage(), 
                    "Save Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            // Refresh the display and close the window
            mw.displayFlights();
            this.setVisible(false);

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and capacity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}