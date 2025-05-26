package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightType; // Import FlightType

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JComboBox; // For selecting flight type
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField economyPriceText = new JTextField(); // Renamed for clarity
    private JTextField capacityText = new JTextField();
    // Replaced classText with JComboBox for FlightType
    private JComboBox<FlightType> flightTypeComboBox;


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

        setSize(350, 250); // Increased size to accommodate new field
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(8, 2)); // 8 rows for 8 fields

        topPanel.add(new JLabel("Flight No : "));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin : "));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination : "));
        topPanel.add(destinationText);
        topPanel.add(new JLabel("Departure Date (YYYY-MM-DD) : "));
        topPanel.add(depDateText);
        topPanel.add(new JLabel("Economy Price : ")); // Changed label
        topPanel.add(economyPriceText); // Renamed text field
        topPanel.add(new JLabel("Total Capacity : ")); // Changed label
        topPanel.add(capacityText);

        // Add Flight Type selection using JComboBox
        topPanel.add(new JLabel("Flight Type : "));
        flightTypeComboBox = new JComboBox<>(FlightType.values());
        topPanel.add(flightTypeComboBox);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("      "));
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addFlight(); // Renamed method to match command
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }
    }

    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();

            LocalDate departureDate = null;
            try {
                departureDate = LocalDate.parse(depDateText.getText());
            } catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Departure Date must be in YYYY-MM-DD format.");
            }

            BigDecimal economyPrice;
            try {
                economyPrice = new BigDecimal(economyPriceText.getText());
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Price must be a valid number.");
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

            FlightType selectedFlightType = (FlightType) flightTypeComboBox.getSelectedItem();

            Command addFlightCommand;
            if (selectedFlightType == FlightType.BUDGET) {
                // Use the constructor for Budget flights (single class)
                addFlightCommand = new AddFlight(flightNumber, origin, destination,
                                                 departureDate, economyPrice, capacity);
            } else {
                // Use the constructor for Commercial flights with default distribution
                // The AddFlight command with FlightType handles default distribution
                addFlightCommand = new AddFlight(flightNumber, origin, destination,
                                                 departureDate, economyPrice, capacity, selectedFlightType);
            }

            addFlightCommand.execute(mw.getFlightBookingSystem());
            mw.displayFlights();
            this.setVisible(false);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}