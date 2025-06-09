package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Meal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class BookingDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Booking booking; // The specific booking to display

    // ... (rest of the JLabel declarations remain the same) ...
    private JLabel idLabel;
    private JLabel customerLabel;
    private JLabel outboundFlightLabel;
    private JLabel returnFlightLabel;
    private JLabel bookedClassLabel;
    private JLabel mealLabel;
    private JLabel bookingDateLabel;
    private JLabel bookedPriceOutboundLabel;
    private JLabel bookedPriceReturnLabel;
    private JLabel cancellationFeeLabel;
    private JLabel rebookFeeLabel;
    private JLabel totalBookingPriceLabel;
    private JLabel statusLabel;

    private JButton editBookingButton;
    private JButton cancelBookingButton;
    private JButton rebookBookingButton; // NEW: Declare the rebook button
    private JButton backButton;

    private static final Color PRIMARY_BLUE = new Color(34, 107, 172);
    private static final Color LIGHT_GRAY_BG = new Color(245, 245, 245);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color ACCENT_GREEN = new Color(46, 204, 113);
    private static final Color ACCENT_RED = new Color(231, 76, 60);

    public BookingDetailsWindow(MainWindow mw, Booking booking) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        this.booking = booking;

        initialize();
    }

    private void initialize() {
        setTitle("Booking Details - ID: " + booking.getId());
        setSize(550, 700);
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_GRAY_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("Booking Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addRow(detailsPanel, "Booking ID:", booking.getId(), gbc, 0);
        
        Customer customer = booking.getCustomer();
        addRow(detailsPanel, "Customer:", 
               customer != null ? customer.getName() + " (ID: " + customer.getId() + ")" : "N/A", 
               gbc, 1);
        
        Flight outboundFlight = booking.getOutboundFlight();
        addRow(detailsPanel, "Outbound Flight:", 
               outboundFlight != null ? outboundFlight.getFlightNumber() + 
               " (" + outboundFlight.getOrigin() + " to " + outboundFlight.getDestination() + 
               " on " + outboundFlight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")" : "N/A", 
               gbc, 2);

        Flight returnFlight = booking.getReturnFlight();
        addRow(detailsPanel, "Return Flight:", 
               returnFlight != null ? returnFlight.getFlightNumber() + 
               " (" + returnFlight.getOrigin() + " to " + returnFlight.getDestination() + 
               " on " + returnFlight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")" : "N/A", 
               gbc, 3);
        
        addRow(detailsPanel, "Booked Class:", 
               booking.getBookedClass() != null ? booking.getBookedClass().getClassName() : "N/A", 
               gbc, 4);
        
        Meal meal = booking.getMeal();
        addRow(detailsPanel, "Meal:", 
               meal != null ? meal.getName() + " (£" + String.format("%.2f", meal.getPrice()) + ")" : "None", 
               gbc, 5);
        
        addRow(detailsPanel, "Booking Date:", 
               booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
               gbc, 6);
        addRow(detailsPanel, "Outbound Price:", "£" + String.format("%.2f", booking.getBookedPriceOutbound()), gbc, 7);
        addRow(detailsPanel, "Return Price:", "£" + String.format("%.2f", booking.getBookedPriceReturn()), gbc, 8);
        addRow(detailsPanel, "Total Booking Price:", "£" + String.format("%.2f", booking.getTotalBookingPrice()), gbc, 9);
        addRow(detailsPanel, "Cancellation Fee:", "£" + String.format("%.2f", booking.getCancellationFee()), gbc, 10);
        addRow(detailsPanel, "Rebook Fee:", "£" + String.format("%.2f", booking.getRebookFee()), gbc, 11);
        
        gbc.gridx = 0;
        gbc.gridy = 12;
        detailsPanel.add(createLabel("Status:", true), gbc);
        gbc.gridx = 1;
        statusLabel = createLabel(booking.isCancelled() ? "Cancelled" : "Active", false);
        statusLabel.setForeground(booking.isCancelled() ? ACCENT_RED : ACCENT_GREEN);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsPanel.add(statusLabel, gbc);


        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LIGHT_GRAY_BG);

        editBookingButton = new JButton("Edit Booking");
        editBookingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editBookingButton.setBackground(PRIMARY_BLUE);
        editBookingButton.setForeground(Color.WHITE);
        editBookingButton.setFocusPainted(false);
        editBookingButton.addActionListener(this);
        editBookingButton.setEnabled(!booking.isCancelled()); 

        cancelBookingButton = new JButton("Cancel Booking");
        cancelBookingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBookingButton.setBackground(ACCENT_RED);
        cancelBookingButton.setForeground(Color.WHITE);
        cancelBookingButton.setFocusPainted(false);
        cancelBookingButton.addActionListener(this);
        cancelBookingButton.setEnabled(!booking.isCancelled());

        // NEW: Rebook Booking Button
        rebookBookingButton = new JButton("Rebook Booking");
        rebookBookingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rebookBookingButton.setBackground(new Color(255, 165, 0)); // Orange color for Rebook
        rebookBookingButton.setForeground(Color.WHITE);
        rebookBookingButton.setFocusPainted(false);
        rebookBookingButton.addActionListener(this);
        rebookBookingButton.setEnabled(!booking.isCancelled()); // Enable only if not cancelled

        backButton = new JButton("Back to Bookings");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(TEXT_DARK);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);

        buttonPanel.add(editBookingButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(rebookBookingButton); // NEW: Add the rebook button to the panel
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
    
    private void addRow(JPanel panel, String labelText, Object value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, true), gbc);
        
        gbc.gridx = 1;
        panel.add(createLabel(value.toString(), false), gbc);
    }

    private JLabel createLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_DARK);
        if (isBold) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == editBookingButton) {
            new EditBookingWindow(mw, booking);
            this.dispose();
        } else if (ae.getSource() == cancelBookingButton) {
            new CancelBookingWindow(mw, booking.getCustomer(), booking.getOutboundFlight()); 
            this.dispose();
        } else if (ae.getSource() == rebookBookingButton) { // NEW: Handle rebook button click
            new RebookFlightWindow(mw); // Open the RebookFlightWindow
            this.dispose(); // Close the current details window
        } else if (ae.getSource() == backButton) {
            mw.displayBookings();
            this.dispose();
        }
    }
}