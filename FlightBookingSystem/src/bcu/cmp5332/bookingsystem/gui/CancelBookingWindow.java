package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer; // Import Customer
import bcu.cmp5332.bookingsystem.model.Flight;   // Import Flight
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Customer customer;     // Now takes a Customer object
    private Flight flightToCancel; // Now takes a Flight object

    private JButton confirmButton;
    private JButton cancelButton;

    // Modified Constructor: Takes Customer and Flight instead of Booking
    public CancelBookingWindow(MainWindow mw, Customer customer, Flight flightToCancel) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        this.customer = customer;
        this.flightToCancel = flightToCancel;
        initialize();
    }

    private void initialize() {
        // Use customer and flight information for the title
        setTitle("Confirm Cancellation - Customer: " + customer.getName() + " - Flight: " + flightToCancel.getFlightNumber());
        setSize(400, 220); // Adjusted size for more info
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(231, 76, 60)); // Accent Red
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("Confirm Cancellation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Message Label with more details for confirmation
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Are you sure you want to cancel the booking for:<br>" +
            "<b>Customer:</b> " + customer.getName() + " (ID: " + customer.getId() + ")<br>" +
            "<b>Flight:</b> " + flightToCancel.getFlightNumber() + " (" + flightToCancel.getOrigin() + " to " + flightToCancel.getDestination() + ")<br>" +
            "This action cannot be undone.</div></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245)); // Light Gray BG

        confirmButton = new JButton("Confirm Cancellation");
        confirmButton.setBackground(new Color(231, 76, 60)); // Red for destructive action
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setFocusPainted(false);
        confirmButton.addActionListener(this);
        
        cancelButton = new JButton("Keep Booking");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(new Color(44, 62, 80)); // Text Dark
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == confirmButton) {
            try {
                // Call the cancelBooking method that takes Customer and Flight
                fbs.cancelBooking(this.customer, this.flightToCancel); 

                // Find the specific booking that was cancelled to get its ID for the message
                // This is slightly complex as cancelBooking(Customer, Flight) doesn't return the Booking object
                // We'll have to iterate or ensure the FBS method updates the booking object itself.
                // Assuming the FBS method prints the Booking ID, or we can find it here.
                // For a more robust solution, the FBS method should return the cancelled booking or its ID.
                
                // Let's find the booking that was actually cancelled to display its ID.
                // (Note: This is a simplified way. A better FBS design would pass the booking object itself or return the cancelled booking's ID).
                Booking actualCancelledBooking = null;
                for(Booking b : customer.getBookings()) {
                    if (b.isCancelled() && (b.getOutboundFlight().equals(flightToCancel) || (b.getReturnFlight() != null && b.getReturnFlight().equals(flightToCancel)))) {
                        actualCancelledBooking = b;
                        break;
                    }
                }
                
                String bookingIdMsg = (actualCancelledBooking != null) ? "Booking ID " + actualCancelledBooking.getId() : "A booking";

                JOptionPane.showMessageDialog(this, bookingIdMsg + " has been cancelled.", 
                                               "Cancellation Successful", JOptionPane.INFORMATION_MESSAGE);
                mw.refreshCurrentView(); // Refresh the main window's booking view to show the status change
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage(), 
                                               "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace(); // Print stack trace for debugging
            }
        } else if (ae.getSource() == cancelButton) {
            this.dispose(); // Close without cancelling
        }
    }
}