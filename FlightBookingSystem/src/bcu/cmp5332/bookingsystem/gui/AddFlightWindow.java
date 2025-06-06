// bcu.cmp5332.bookingsystem.gui.AddFlightWindow.java
package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(480, 400); // Increased size to accommodate new fields

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(11, 2, 5, 5)); // Increased grid rows for new fields

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
        boolean isCommercial = flightTypeComboBox.getSelectedItem().equals("Commercial");
        premiumEconomyCapacityLabel.setVisible(isCommercial);
        premiumEconomyCapacityText.setVisible(isCommercial);
        businessCapacityLabel.setVisible(isCommercial);
        businessCapacityText.setVisible(isCommercial);
        firstClassCapacityLabel.setVisible(isCommercial);
        firstClassCapacityText.setVisible(isCommercial);
    }

    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();

            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() ||
                depDateText.getText().isEmpty() || economyPriceText.getText().isEmpty() ||
                capacityText.getText().isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            LocalDate departureDate;
            try {
                departureDate = LocalDate.parse(depDateText.getText());
            } catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Departure Date must be in YYYY-MM-DD format.");
            }

            BigDecimal economyPrice;
            try {
                economyPrice = new BigDecimal(economyPriceText.getText());
                if (economyPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new FlightBookingSystemException("Economy Price cannot be negative.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Economy Price must be a valid number.");
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityText.getText());
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
                Map<CommercialClassType, Integer> classCapacities = new HashMap<>();
                int premiumEconomyPercentage, businessPercentage, firstClassPercentage;

                try {
                    premiumEconomyPercentage = Integer.parseInt(premiumEconomyCapacityText.getText());
                    businessPercentage = Integer.parseInt(businessCapacityText.getText());
                    firstClassPercentage = Integer.parseInt(firstClassCapacityText.getText());

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
                int premiumEconomyCapacity = (int) (capacity * (premiumEconomyPercentage / 100.0));
                int businessCapacity = (int) (capacity * (businessPercentage / 100.0));
                int firstClassCapacity = (int) (capacity * (firstClassPercentage / 100.0));
                int economyCapacity = capacity - premiumEconomyCapacity - businessCapacity - firstClassCapacity;

                if (economyCapacity < 0) { // Should not happen with validation above, but as a safeguard
                     economyCapacity = 0;
                }

                classCapacities.put(CommercialClassType.ECONOMY, economyCapacity);
                classCapacities.put(CommercialClassType.PREMIUM_ECONOMY, premiumEconomyCapacity);
                classCapacities.put(CommercialClassType.BUSINESS, businessCapacity);
                classCapacities.put(CommercialClassType.FIRST, firstClassCapacity);

                addFlightCommand = new AddFlight(flightNumber, origin, destination,
                    departureDate, economyPrice, capacity, flightType, classCapacities);
            }

            addFlightCommand.execute(mw.getFlightBookingSystem(), null);
            mw.displayFlights();
            this.setVisible(false);

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and capacity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}