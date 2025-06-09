package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; // Import the data persistence class
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal; // Import BigDecimal

public class EditBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Booking booking;
    private Customer customer; // Store the customer associated with the booking
    private Flight outboundFlight; // Store the outbound flight
    private Flight returnFlight; // Store the return flight (can be null)

    private JLabel customerNameLabel;
    private JLabel bookingIdLabel;
    private JLabel outboundFlightLabel;
    private JLabel returnFlightLabel; // For return flight details

    private JComboBox<String> classComboBox;
    private JTextField departureDateOutboundText;
    // You might also want a JTextField for return flight date if that's editable
    // private JTextField departureDateReturnText; // Optional: If return flight date is editable

    private JButton saveButton = new JButton("Save Changes");
    private JButton cancelButton = new JButton("Cancel");

    public EditBookingWindow(MainWindow mw, Booking booking) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        this.booking = booking;
        this.customer = booking.getCustomer();
        this.outboundFlight = booking.getOutboundFlight();
        this.returnFlight = booking.getReturnFlight();

        initialize();
        loadBookingDetails();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Edit Booking " + booking.getId());
        setSize(450, 400); // Adjust size as needed
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
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

        // Booking ID
        gbc.gridx = 0; gbc.gridy = row;
        JLabel idLabel = new JLabel("Booking ID:");
        idLabel.setFont(labelFont); idLabel.setForeground(textColor);
        contentPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        bookingIdLabel = new JLabel(String.valueOf(booking.getId()));
        bookingIdLabel.setFont(labelFont); bookingIdLabel.setForeground(textColor);
        contentPanel.add(bookingIdLabel, gbc);
        row++;

        // Customer Name
        gbc.gridx = 0; gbc.gridy = row;
        JLabel custLabel = new JLabel("Customer:");
        custLabel.setFont(labelFont); custLabel.setForeground(textColor);
        contentPanel.add(custLabel, gbc);
        gbc.gridx = 1;
        customerNameLabel = new JLabel(customer.getName() + " (ID: " + customer.getId() + ")");
        customerNameLabel.setFont(labelFont); customerNameLabel.setForeground(textColor);
        contentPanel.add(customerNameLabel, gbc);
        row++;

        // Outbound Flight
        gbc.gridx = 0; gbc.gridy = row;
        JLabel outFlightLbl = new JLabel("Outbound Flight:");
        outFlightLbl.setFont(labelFont); outFlightLbl.setForeground(textColor);
        contentPanel.add(outFlightLbl, gbc);
        gbc.gridx = 1;
        outboundFlightLabel = new JLabel(outboundFlight.getFlightNumber() + " (" + outboundFlight.getOrigin() + " to " + outboundFlight.getDestination() + ")");
        outboundFlightLabel.setFont(labelFont); outboundFlightLabel.setForeground(textColor);
        contentPanel.add(outboundFlightLabel, gbc);
        row++;

        // Return Flight (if exists)
        if (returnFlight != null) {
            gbc.gridx = 0; gbc.gridy = row;
            JLabel retFlightLbl = new JLabel("Return Flight:");
            retFlightLbl.setFont(labelFont); retFlightLbl.setForeground(textColor);
            contentPanel.add(retFlightLbl, gbc);
            gbc.gridx = 1;
            returnFlightLabel = new JLabel(returnFlight.getFlightNumber() + " (" + returnFlight.getOrigin() + " to " + returnFlight.getDestination() + ")");
            returnFlightLabel.setFont(labelFont); returnFlightLabel.setForeground(textColor);
            contentPanel.add(returnFlightLabel, gbc);
            row++;
        }

        // Booked Class
        gbc.gridx = 0; gbc.gridy = row;
        JLabel classLabel = new JLabel("Booked Class:");
        classLabel.setFont(labelFont); classLabel.setForeground(textColor);
        contentPanel.add(classLabel, gbc);
        gbc.gridx = 1;
        classComboBox = new JComboBox<>(new String[]{"Economy", "Premium Economy", "Business", "First"});
        classComboBox.setFont(textFieldFont); classComboBox.setForeground(textColor);
        contentPanel.add(classComboBox, gbc);
        row++;

        // Outbound Departure Date
        gbc.gridx = 0; gbc.gridy = row;
        JLabel depDateOutboundLabel = new JLabel("Outbound Departure Date (YYYY-MM-DD):");
        depDateOutboundLabel.setFont(labelFont); depDateOutboundLabel.setForeground(textColor);
        contentPanel.add(depDateOutboundLabel, gbc);
        gbc.gridx = 1;
        departureDateOutboundText = new JTextField(10);
        departureDateOutboundText.setFont(textFieldFont); departureDateOutboundText.setForeground(textColor);
        contentPanel.add(departureDateOutboundText, gbc);
        row++;

        // Optional: Return Departure Date (if you want to allow editing this)
        // if (returnFlight != null) {
        //     gbc.gridx = 0; gbc.gridy = row;
        //     JLabel depDateReturnLabel = new JLabel("Return Departure Date (YYYY-MM-DD):");
        //     depDateReturnLabel.setFont(labelFont); depDateReturnLabel.setForeground(textColor);
        //     contentPanel.add(depDateReturnLabel, gbc);
        //     gbc.gridx = 1;
        //     departureDateReturnText = new JTextField(10);
        //     departureDateReturnText.setFont(textFieldFont); departureDateReturnText.setForeground(textColor);
        //     contentPanel.add(departureDateReturnText, gbc);
        //     row++;
        // }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.BUTTON_PANEL_BOTTOM_PADDING, 0));

        customizeButton(saveButton);
        customizeButton(cancelButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void loadBookingDetails() {
        // Set initial values in the fields
        classComboBox.setSelectedItem(booking.getBookedClass().getClassName());
        departureDateOutboundText.setText(booking.getOutboundFlight().getDepartureDate().toString());
        // If you added return date field:
        // if (returnFlight != null) {
        //     departureDateReturnText.setText(booking.getReturnFlight().getDepartureDate().toString());
        // }
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
        if (ae.getSource() == saveButton) {
            try {
                // 1. Get new values from GUI fields
                CommercialClassType newClass = CommercialClassType.fromClassName((String) classComboBox.getSelectedItem());
                LocalDate newOutboundDepartureDate = LocalDate.parse(departureDateOutboundText.getText().trim());

                // Optional: If you enabled editing return flight date:
                // LocalDate newReturnDepartureDate = null;
                // if (returnFlight != null) {
                //     newReturnDepartureDate = LocalDate.parse(departureDateReturnText.getText().trim());
                // }

                // 2. Validate inputs
                if (newOutboundDepartureDate.isBefore(fbs.getSystemDate())) {
                    throw new FlightBookingSystemException("Outbound departure date cannot be in the past.");
                }
                // Optional: If validating return date:
                // if (newReturnDepartureDate != null && newReturnDepartureDate.isBefore(fbs.getSystemDate())) {
                //     throw new FlightBookingSystemException("Return departure date cannot be in the past.");
                // }
                // Add any other necessary validations (e.g., class availability on the flight)

                // 3. Call the editBooking method in FlightBookingSystem
                // This method updates the in-memory Booking object and Flight passenger counts
                fbs.editBooking(customer, outboundFlight, newOutboundDepartureDate, newClass);

                // If you enabled editing return flight date:
                // if (returnFlight != null && newReturnDepartureDate != null) {
                //     fbs.editBooking(customer, returnFlight, newReturnDepartureDate, newClass); // Re-use or adapt for return flight
                // }

                // --- IMPORTANT: Persist the changes to the text file ---
                // This call saves the entire state of flights, customers, and bookings
                FlightBookingSystemData.store(fbs);

                JOptionPane.showMessageDialog(this,
                    "Booking " + booking.getId() + " updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                mw.refreshCurrentView(); // Refresh the main window's display (e.g., booking list)
                this.dispose(); // Close the edit window

            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving data: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Print stack trace for debugging
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Print stack trace for debugging
            }
        } else if (ae.getSource() == cancelButton) {
            this.dispose(); // Close the window without saving
        }
    }
}