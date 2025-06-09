package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CancelBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Customer customer;
    private Flight flightToCancel;

    private JButton confirmButton;
    private JButton cancelButton;

    public CancelBookingWindow(MainWindow mw, Customer customer, Flight flightToCancel) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        this.customer = customer;
        this.flightToCancel = flightToCancel;
        initialize();
    }

    private void initialize() {
        setTitle("Confirm Cancellation - Customer: " + customer.getName() + " - Flight: " + flightToCancel.getFlightNumber());
        setSize(400, 220);
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.STATUS_CANCELLED_RED);
        headerPanel.setBorder(DesignConstants.TOP_BOTTOM_HEADER_BORDER);
        JLabel titleLabel = new JLabel("Confirm Cancellation");
        titleLabel.setFont(DesignConstants.HEADER_FONT);
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
        messageLabel.setFont(DesignConstants.MESSAGE_LABEL_FONT);
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        // Confirm Button
        confirmButton = new JButton("Confirm Cancellation");
        customizeButton(confirmButton, DesignConstants.STATUS_CANCELLED_RED, DesignConstants.CANCEL_BUTTON_HOVER_RED,
                        DesignConstants.BUTTON_BEVEL_PADDING_BORDER, Color.WHITE);
        
        // Cancel Button
        cancelButton = new JButton("Keep Booking");
        customizeButton(cancelButton, DesignConstants.SECONDARY_BUTTON_BG, DesignConstants.SECONDARY_BUTTON_HOVER_BG,
                        DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.TEXT_DARK);
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Helper method to customize buttons for consistent styling and hover effect
    private void customizeButton(JButton button, Color bgColor, Color hoverColor, javax.swing.border.Border border, Color textColor) {
        button.setFont(DesignConstants.BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(border);
        button.addActionListener(this);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == confirmButton) {
            try {
                fbs.cancelBooking(this.customer, this.flightToCancel); 
                
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
                mw.refreshCurrentView();
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage(), 
                                               "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }
}