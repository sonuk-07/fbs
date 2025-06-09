package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Meal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class BookingDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Booking booking;

    private JLabel statusLabel;

    private JButton editBookingButton;
    private JButton cancelBookingButton;
    private JButton rebookBookingButton;
    private JButton backButton;

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

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.PRIMARY_BLUE);
        headerPanel.setBorder(DesignConstants.HEADER_PANEL_BORDER);
        JLabel titleLabel = new JLabel("Booking Details");
        titleLabel.setFont(DesignConstants.DETAILS_TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(DesignConstants.DETAILS_PANEL_COMPOUND_BORDER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignConstants.DETAILS_INSET_GAP, DesignConstants.DETAILS_INSET_GAP, DesignConstants.DETAILS_INSET_GAP, DesignConstants.DETAILS_INSET_GAP);
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
        statusLabel.setForeground(booking.isCancelled() ? DesignConstants.STATUS_CANCELLED_RED : DesignConstants.STATUS_ACTIVE_GREEN);
        statusLabel.setFont(DesignConstants.STATUS_FONT);
        detailsPanel.add(statusLabel, gbc);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        editBookingButton = new JButton("Edit Booking");
        customizeButton(editBookingButton, DesignConstants.BUTTON_BLUE, DesignConstants.BUTTON_HOVER_BLUE, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, Color.WHITE);
        editBookingButton.setEnabled(!booking.isCancelled()); 

        cancelBookingButton = new JButton("Cancel Booking");
        customizeButton(cancelBookingButton, DesignConstants.STATUS_CANCELLED_RED, DesignConstants.STATUS_CANCELLED_RED.darker(), DesignConstants.BUTTON_BEVEL_PADDING_BORDER, Color.WHITE);
        cancelBookingButton.setEnabled(!booking.isCancelled());

        rebookBookingButton = new JButton("Rebook Booking");
        customizeButton(rebookBookingButton, DesignConstants.REBOOK_BUTTON_ORANGE, DesignConstants.REBOOK_BUTTON_HOVER_ORANGE, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, Color.WHITE);
        rebookBookingButton.setEnabled(!booking.isCancelled());

        backButton = new JButton("Back to Bookings");
        customizeButton(backButton, Color.LIGHT_GRAY, Color.GRAY, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.TEXT_DARK);

        buttonPanel.add(editBookingButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(rebookBookingButton);
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
        label.setForeground(DesignConstants.TEXT_DARK);
        if (isBold) {
            label.setFont(DesignConstants.DETAILS_LABEL_FONT);
        } else {
            label.setFont(DesignConstants.DETAILS_VALUE_FONT);
        }
        return label;
    }

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
        if (ae.getSource() == editBookingButton) {
            new EditBookingWindow(mw, booking);
            this.dispose();
        } else if (ae.getSource() == cancelBookingButton) {
            new CancelBookingWindow(mw, booking.getCustomer(), booking.getOutboundFlight()); 
            this.dispose();
        } else if (ae.getSource() == rebookBookingButton) {
            new RebookFlightWindow(mw);
            this.dispose();
        } else if (ae.getSource() == backButton) {
            mw.displayBookings();
            this.dispose();
        }
    }
}