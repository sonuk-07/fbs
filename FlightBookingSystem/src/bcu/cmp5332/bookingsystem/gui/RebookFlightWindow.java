package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class RebookFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;

    private JComboBox<Customer> customerComboBox;
    private JComboBox<BookingSegmentWrapper> oldFlightComboBox;
    private JComboBox<Flight> newFlightComboBox;
    private JComboBox<CommercialClassType> newClassComboBox;

    private JLabel rebookFeeLabel;

    private JButton loadCustomersButton;
    private JButton loadOldFlightsButton;
    private JButton loadNewFlightsButton;
    private JButton calculateFeeButton;
    private JButton rebookButton;
    private JButton cancelButton;

    private Booking selectedBooking;
    private Flight selectedOldFlight;

    public RebookFlightWindow(MainWindow mw) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        initialize();
    }

    private void initialize() {
        setTitle("Rebook Flight");
        setSize(550, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mw);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Select Customer:"), gbc);
        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.addActionListener(this);
        mainPanel.add(customerComboBox, gbc);
        gbc.gridx = 2;
        loadCustomersButton = new JButton("Load Customers");
        loadCustomersButton.addActionListener(e -> loadCustomers());
        mainPanel.add(loadCustomersButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Old Flight to Rebook:"), gbc);
        gbc.gridx = 1;
        oldFlightComboBox = new JComboBox<>();
        oldFlightComboBox.addActionListener(this);
        mainPanel.add(oldFlightComboBox, gbc);
        gbc.gridx = 2;
        loadOldFlightsButton = new JButton("Load Old Flights");
        loadOldFlightsButton.addActionListener(this);
        mainPanel.add(loadOldFlightsButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("New Flight:"), gbc);
        gbc.gridx = 1;
        newFlightComboBox = new JComboBox<>();
        newFlightComboBox.addActionListener(this);
        mainPanel.add(newFlightComboBox, gbc);
        gbc.gridx = 2;
        loadNewFlightsButton = new JButton("Load New Flights");
        loadNewFlightsButton.addActionListener(e -> loadNewFlights());
        mainPanel.add(loadNewFlightsButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("New Class:"), gbc);
        gbc.gridx = 1;
        newClassComboBox = new JComboBox<>(CommercialClassType.values());
        newClassComboBox.addActionListener(this);
        mainPanel.add(newClassComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Rebooking Fee:"), gbc);
        gbc.gridx = 1;
        rebookFeeLabel = new JLabel("£0.00");
        rebookFeeLabel.setFont(rebookFeeLabel.getFont().deriveFont(Font.BOLD, 14f));
        mainPanel.add(rebookFeeLabel, gbc);
        gbc.gridx = 2;
        calculateFeeButton = new JButton("Calculate Fee");
        calculateFeeButton.addActionListener(this);
        mainPanel.add(calculateFeeButton, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rebookButton = new JButton("Rebook Flight");
        rebookButton.addActionListener(this);
        buttonPanel.add(rebookButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomers();
        loadNewFlights();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == customerComboBox) {
            loadOldFlightsForSelectedCustomer();
        } else if (e.getSource() == oldFlightComboBox) {
            BookingSegmentWrapper wrapper = (BookingSegmentWrapper) oldFlightComboBox.getSelectedItem();
            if (wrapper != null) {
                selectedBooking = wrapper.getBooking();
                selectedOldFlight = wrapper.getFlight();
            } else {
                selectedBooking = null;
                selectedOldFlight = null;
            }
            calculateRebookFee();
        } else if (e.getSource() == newFlightComboBox || e.getSource() == newClassComboBox) {
            calculateRebookFee();
        } else if (e.getSource() == loadOldFlightsButton) {
            loadOldFlightsForSelectedCustomer();
        } else if (e.getSource() == loadNewFlightsButton) {
            loadNewFlights();
        } else if (e.getSource() == calculateFeeButton) {
            calculateRebookFee();
        } else if (e.getSource() == rebookButton) {
            performRebook();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    private void loadCustomers() {
        customerComboBox.removeAllItems();
        customerComboBox.addItem(null);
        try {
            fbs.getCustomers().forEach(customerComboBox::addItem);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOldFlightsForSelectedCustomer() {
        oldFlightComboBox.removeAllItems();
        oldFlightComboBox.addItem(null);
        Customer customer = (Customer) customerComboBox.getSelectedItem();
        if (customer != null) {
            for (Booking booking : customer.getBookings()) {
                if (!booking.isCancelled()) {
                    if (booking.getOutboundFlight() != null) {
                        oldFlightComboBox.addItem(new BookingSegmentWrapper(booking, booking.getOutboundFlight(), "Outbound"));
                    }
                    if (booking.getReturnFlight() != null) {
                        oldFlightComboBox.addItem(new BookingSegmentWrapper(booking, booking.getReturnFlight(), "Return"));
                    }
                }
            }
        }
        selectedBooking = null;
        selectedOldFlight = null;
        calculateRebookFee();
    }

    private void loadNewFlights() {
        newFlightComboBox.removeAllItems();
        newFlightComboBox.addItem(null);
        try {
            fbs.getFlights().stream()
               .filter(Flight::hasAnySeatsLeft)
               .forEach(newFlightComboBox::addItem);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading new flights: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        calculateRebookFee();
    }

    private void calculateRebookFee() {
        if (selectedBooking == null || selectedOldFlight == null ||
            newFlightComboBox.getSelectedItem() == null || newClassComboBox.getSelectedItem() == null) {
            rebookFeeLabel.setText("£0.00");
            return;
        }

        try {
            BigDecimal fee = fbs.calculateRebookFee(
                selectedBooking,
                (Flight) newFlightComboBox.getSelectedItem(),
                (CommercialClassType) newClassComboBox.getSelectedItem()
            );
            rebookFeeLabel.setText("£" + fee.setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            rebookFeeLabel.setText("Error");
        }
    }

    private void performRebook() {
        if (selectedBooking == null || selectedOldFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select an existing flight segment to rebook.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Flight newFlight = (Flight) newFlightComboBox.getSelectedItem();
        if (newFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a new flight.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CommercialClassType newClass = (CommercialClassType) newClassComboBox.getSelectedItem();
        if (newClass == null) {
            JOptionPane.showMessageDialog(this, "Please select a new class.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!newFlight.isClassAvailable(newClass)) {
                 throw new FlightBookingSystemException("New flight " + newFlight.getFlightNumber() + " has no seats left in " + newClass.getClassName() + " class.");
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm rebook from " + selectedOldFlight.getFlightNumber() +
                    " to " + newFlight.getFlightNumber() + " in " + newClass.getClassName() + " class?\n" +
                    "Rebook Fee: " + rebookFeeLabel.getText(),
                    "Confirm Rebooking", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                fbs.cancelBooking(selectedBooking.getCustomer(), selectedOldFlight);
                fbs.addBooking(selectedBooking.getCustomer(), newFlight, null, newClass, null);

                FlightBookingSystemData.store(fbs);

                JOptionPane.showMessageDialog(this, "Flight rebooked successfully! Old booking cancelled, new booking created.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                mw.refreshCurrentView();
                dispose();
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Rebooking failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static class BookingSegmentWrapper {
        private Booking booking;
        private Flight flight;
        private String segmentType;

        public BookingSegmentWrapper(Booking booking, Flight flight, String segmentType) {
            this.booking = booking;
            this.flight = flight;
            this.segmentType = segmentType;
        }

        public Booking getBooking() {
            return booking;
        }

        public Flight getFlight() {
            return flight;
        }

        @Override
        public String toString() {
            return booking.getCustomer().getName() + " - " + segmentType + " Flight: " + flight.getFlightNumber() +
                   " (" + flight.getOrigin() + " to " + flight.getDestination() + ")";
        }
    }
}