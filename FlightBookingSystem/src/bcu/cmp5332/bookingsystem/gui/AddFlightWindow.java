package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; // Changed from FlightDataManager for consistency

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

    private JTextField flightNoText = new JTextField(15);
    private JTextField originText = new JTextField(15);
    private JTextField destinationText = new JTextField(15);
    private JTextField depDateText = new JTextField(10); // Shorter for date
    private JTextField economyPriceText = new JTextField(10);
    private JTextField capacityText = new JTextField(10);
    private JComboBox<String> flightTypeComboBox = new JComboBox<>(new String[]{"Budget", "Commercial"});

    // For Commercial Flight specific fields
    private JLabel premiumEconomyCapacityLabel = new JLabel("Premium Economy (% of total):");
    private JTextField premiumEconomyCapacityText = new JTextField(5);
    private JLabel businessCapacityLabel = new JLabel("Business (% of total):");
    private JTextField businessCapacityText = new JTextField(5);
    private JLabel firstClassCapacityLabel = new JLabel("First Class (% of total):");
    private JTextField firstClassCapacityText = new JTextField(5);

    private JButton addBtn = new JButton("Add Flight");
    private JButton cancelBtn = new JButton("Cancel");

    private JPanel contentPanel; // <--- FIX: Declare contentPanel as a member variable

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Add New Flight");
        setSize(550, 520); // Increased size for more fields and padding
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPanel = new JPanel(new GridBagLayout()); // <--- Initialize here
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        // Flight Number
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(flightNoText, gbc);
        row++;

        // Origin
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(originText, gbc);
        row++;

        // Destination
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(destinationText, gbc);
        row++;

        // Departure Date
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        contentPanel.add(depDateText, gbc);
        row++;

        // Economy Price
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Economy Price (£):"), gbc);
        gbc.gridx = 1;
        contentPanel.add(economyPriceText, gbc);
        row++;

        // Total Capacity
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Total Capacity:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(capacityText, gbc);
        row++;

        // Flight Type
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(new JLabel("Flight Type:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(flightTypeComboBox, gbc);
        row++;

        // Commercial specific fields - these will be conditionally visible
        gbc.gridwidth = 2; // Span across two columns for better alignment
        gbc.anchor = GridBagConstraints.CENTER; // Center labels for these sections
        gbc.insets = new Insets(15, 5, 5, 5); // More top padding for section
        JLabel classCapacityHeader = new JLabel("--- Commercial Class Capacities ---");
        classCapacityHeader.setFont(classCapacityHeader.getFont().deriveFont(Font.BOLD, 12f));
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(classCapacityHeader, gbc);
        row++;
        gbc.insets = new Insets(8, 5, 8, 5); // Reset insets for fields
        gbc.gridwidth = 1; // Reset to 1 column width
        gbc.anchor = GridBagConstraints.WEST; // Align to left for fields

        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(premiumEconomyCapacityLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(premiumEconomyCapacityText, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(businessCapacityLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(businessCapacityText, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(firstClassCapacityLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(firstClassCapacityText, gbc);
        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        flightTypeComboBox.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        updateCommercialFieldsVisibility(); // Initial state for commercial flight fields

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
            this.dispose();
        } else if (ae.getSource() == flightTypeComboBox) {
            updateCommercialFieldsVisibility();
        }
    }

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
        
        // Ensure the layout manager revalidates the panel
        // For GridBagLayout, revalidate() and repaint() on the content panel are usually sufficient
        contentPanel.revalidate(); 
        contentPanel.repaint();
    }

    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText().trim();
            String origin = originText.getText().trim();
            String destination = destinationText.getText().trim();

            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() ||
                depDateText.getText().trim().isEmpty() || economyPriceText.getText().trim().isEmpty() ||
                capacityText.getText().trim().isEmpty()) {
                throw new FlightBookingSystemException("All main flight fields must be filled out.");
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
                    throw new FlightBookingSystemException("Total Capacity must be a positive integer.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Total Capacity must be a valid integer.");
            }

            FlightType flightType = FlightType.valueOf(((String) flightTypeComboBox.getSelectedItem()).toUpperCase().replace(" ", "_")); // Handle "Premium Economy" -> PREMIUM_ECONOMY
            
            if (flightType == FlightType.BUDGET) {
                int newFlightId = mw.getFlightBookingSystem().generateNextFlightId();
                bcu.cmp5332.bookingsystem.model.Flight newFlight = new bcu.cmp5332.bookingsystem.model.Flight(
                    newFlightId, flightNumber, origin, destination, departureDate, economyPrice, capacity);
                mw.getFlightBookingSystem().addFlight(newFlight);

            } else { // Commercial
                String premiumEconomyText = premiumEconomyCapacityText.getText().trim();
                String businessText = businessCapacityText.getText().trim();
                String firstClassText = firstClassCapacityText.getText().trim();
                
                if (premiumEconomyText.isEmpty() || businessText.isEmpty() || firstClassText.isEmpty()) {
                    throw new FlightBookingSystemException("All class capacity percentages must be filled for commercial flights.");
                }

                Map<CommercialClassType, Integer> classPercentages = new HashMap<>(); // Store percentages
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
                int totalCommercialPercentage = premiumEconomyPercentage + businessPercentage + firstClassPercentage;
                int economyPercentage = 100 - totalCommercialPercentage;
                if (economyPercentage < 0) economyPercentage = 0;

                Map<CommercialClassType, Integer> classCapacities = new HashMap<>();
                classCapacities.put(CommercialClassType.ECONOMY, (int) Math.round(capacity * (economyPercentage / 100.0)));
                classCapacities.put(CommercialClassType.PREMIUM_ECONOMY, (int) Math.round(capacity * (premiumEconomyPercentage / 100.0)));
                classCapacities.put(CommercialClassType.BUSINESS, (int) Math.round(capacity * (businessPercentage / 100.0)));
                classCapacities.put(CommercialClassType.FIRST, (int) Math.round(capacity * (firstClassPercentage / 100.0)));

                int currentTotalAllocated = classCapacities.values().stream().mapToInt(Integer::intValue).sum();
                if (currentTotalAllocated > capacity) {
                    int diff = currentTotalAllocated - capacity;
                    classCapacities.put(CommercialClassType.ECONOMY, Math.max(0, classCapacities.get(CommercialClassType.ECONOMY) - diff)); // Ensure non-negative
                } else if (currentTotalAllocated < capacity) {
                    int diff = capacity - currentTotalAllocated;
                    classCapacities.put(CommercialClassType.ECONOMY, classCapacities.get(CommercialClassType.ECONOMY) + diff);
                }
                
                int newFlightId = mw.getFlightBookingSystem().generateNextFlightId();
                bcu.cmp5332.bookingsystem.model.Flight newFlight = new bcu.cmp5332.bookingsystem.model.Flight(
                    newFlightId, flightNumber, origin, destination, departureDate, economyPrice, capacity, flightType, classCapacities);
                mw.getFlightBookingSystem().addFlight(newFlight);
            }

            FlightBookingSystemData.store(mw.getFlightBookingSystem());
            
            JOptionPane.showMessageDialog(this, 
                "Flight " + flightNumber + " added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            mw.displayFlights();
            this.dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and capacity/percentages.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}