package bcu.cmp5332.bookingsystem.gui;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent; 
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
    private JTextField depDateText = new JTextField(10);
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

    private JPanel contentPanel;

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Add New Flight");
        setSize(550, 560); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER); 
        contentPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = DesignConstants.TABLE_ROW_FONT;
        Font textFieldFont = DesignConstants.TABLE_ROW_FONT;
        Color textColor = DesignConstants.TEXT_DARK;

        int row = 0;
        // Flight Number
        gbc.gridx = 0; gbc.gridy = row;
        JLabel flightNoLabel = new JLabel("Flight Number:");
        flightNoLabel.setFont(labelFont);
        flightNoLabel.setForeground(textColor);
        contentPanel.add(flightNoLabel, gbc);
        gbc.gridx = 1;
        flightNoText.setFont(textFieldFont);
        flightNoText.setForeground(textColor);
        contentPanel.add(flightNoText, gbc);
        row++;

        // Origin
        gbc.gridx = 0; gbc.gridy = row;
        JLabel originLabel = new JLabel("Origin:");
        originLabel.setFont(labelFont);
        originLabel.setForeground(textColor);
        contentPanel.add(originLabel, gbc);
        gbc.gridx = 1;
        originText.setFont(textFieldFont);
        originText.setForeground(textColor);
        contentPanel.add(originText, gbc);
        row++;

        // Destination
        gbc.gridx = 0; gbc.gridy = row;
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setFont(labelFont);
        destinationLabel.setForeground(textColor);
        contentPanel.add(destinationLabel, gbc);
        gbc.gridx = 1;
        destinationText.setFont(textFieldFont);
        destinationText.setForeground(textColor);
        contentPanel.add(destinationText, gbc);
        row++;

        // Departure Date
        gbc.gridx = 0; gbc.gridy = row;
        JLabel depDateLabel = new JLabel("Departure Date (YYYY-MM-DD):");
        depDateLabel.setFont(labelFont);
        depDateLabel.setForeground(textColor);
        contentPanel.add(depDateLabel, gbc);
        gbc.gridx = 1;
        depDateText.setFont(textFieldFont);
        depDateText.setForeground(textColor);
        contentPanel.add(depDateText, gbc);
        row++;

        // Economy Price
        gbc.gridx = 0; gbc.gridy = row;
        JLabel economyPriceLabel = new JLabel("Economy Price (£):");
        economyPriceLabel.setFont(labelFont);
        economyPriceLabel.setForeground(textColor);
        contentPanel.add(economyPriceLabel, gbc);
        gbc.gridx = 1;
        economyPriceText.setFont(textFieldFont);
        economyPriceText.setForeground(textColor);
        contentPanel.add(economyPriceText, gbc);
        row++;

        // Total Capacity
        gbc.gridx = 0; gbc.gridy = row;
        JLabel capacityLabel = new JLabel("Total Capacity:");
        capacityLabel.setFont(labelFont);
        capacityLabel.setForeground(textColor);
        contentPanel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        capacityText.setFont(textFieldFont);
        capacityText.setForeground(textColor);
        contentPanel.add(capacityText, gbc);
        row++;

        // Flight Type
        gbc.gridx = 0; gbc.gridy = row;
        JLabel flightTypeLabel = new JLabel("Flight Type:");
        flightTypeLabel.setFont(labelFont);
        flightTypeLabel.setForeground(textColor);
        contentPanel.add(flightTypeLabel, gbc);
        gbc.gridx = 1;
        flightTypeComboBox.setFont(textFieldFont); 
        flightTypeComboBox.setForeground(textColor);
        contentPanel.add(flightTypeComboBox, gbc);
        row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel classCapacityHeader = new JLabel("--- Commercial Class Capacities ---");
        classCapacityHeader.setFont(DesignConstants.TABLE_HEADER_FONT);
        classCapacityHeader.setForeground(textColor);
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(classCapacityHeader, gbc);
        row++;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = row;
        premiumEconomyCapacityLabel.setFont(labelFont);
        premiumEconomyCapacityLabel.setForeground(textColor);
        contentPanel.add(premiumEconomyCapacityLabel, gbc);
        gbc.gridx = 1;
        premiumEconomyCapacityText.setFont(textFieldFont);
        premiumEconomyCapacityText.setForeground(textColor);
        contentPanel.add(premiumEconomyCapacityText, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        businessCapacityLabel.setFont(labelFont);
        businessCapacityLabel.setForeground(textColor);
        contentPanel.add(businessCapacityLabel, gbc);
        gbc.gridx = 1;
        businessCapacityText.setFont(textFieldFont);
        businessCapacityText.setForeground(textColor);
        contentPanel.add(businessCapacityText, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        firstClassCapacityLabel.setFont(labelFont);
        firstClassCapacityLabel.setForeground(textColor);
        contentPanel.add(firstClassCapacityLabel, gbc);
        gbc.gridx = 1;
        firstClassCapacityText.setFont(textFieldFont);
        firstClassCapacityText.setForeground(textColor);
        contentPanel.add(firstClassCapacityText, gbc);
        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP)); // Using constants for gaps
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG); // Consistent background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.BUTTON_PANEL_BOTTOM_PADDING, 0)); // Consistent bottom padding

        customizeButton(addBtn);
        customizeButton(cancelBtn);

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        flightTypeComboBox.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        updateCommercialFieldsVisibility();

        setVisible(true);
    }

    private void customizeButton(JButton button) {
        button.setBackground(DesignConstants.BUTTON_BLUE);
        button.setForeground(DesignConstants.BUTTON_TEXT_COLOR);
        button.setFont(DesignConstants.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(DesignConstants.BUTTON_PADDING_BORDER);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(DesignConstants.BUTTON_HOVER_BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DesignConstants.BUTTON_BLUE);
            }
        });
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
        
        if (!isCommercial) {
            premiumEconomyCapacityText.setText("");
            businessCapacityText.setText("");
            firstClassCapacityText.setText("");
        }
        
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

            FlightType flightType = FlightType.valueOf(((String) flightTypeComboBox.getSelectedItem()).toUpperCase());
            
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

                Map<CommercialClassType, Integer> classPercentages = new HashMap<>();
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
                
                int totalCommercialPercentage = premiumEconomyPercentage + businessPercentage + firstClassPercentage;
                int economyPercentage = 100 - totalCommercialPercentage; 
                if (economyPercentage < 0) economyPercentage = 0; 

                Map<CommercialClassType, Integer> classCapacities = new HashMap<>();
                classCapacities.put(CommercialClassType.ECONOMY, (int) Math.round(capacity * (economyPercentage / 100.0)));
                classCapacities.put(CommercialClassType.PREMIUM_ECONOMY, (int) Math.round(capacity * (premiumEconomyPercentage / 100.0)));
                classCapacities.put(CommercialClassType.BUSINESS, (int) Math.round(capacity * (businessPercentage / 100.0)));
                classCapacities.put(CommercialClassType.FIRST, (int) Math.round(capacity * (firstClassPercentage / 100.0)));

                int currentTotalAllocated = classCapacities.values().stream().mapToInt(Integer::intValue).sum();
                if (currentTotalAllocated != capacity) {
                    int diff = capacity - currentTotalAllocated;
                    classCapacities.put(CommercialClassType.ECONOMY, Math.max(0, classCapacities.get(CommercialClassType.ECONOMY) + diff)); 
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