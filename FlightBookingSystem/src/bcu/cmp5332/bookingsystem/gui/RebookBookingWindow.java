package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.RebookBooking; // You'll need to create this command
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight; // Potentially needed if rebooking involves new flight

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RebookBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTable cancelledBookingsTable;
    private JButton rebookBtn = new JButton("Rebook Selected Booking");
    private JButton closeBtn = new JButton("Close");

    // Potentially for selecting a new flight if rebooking means changing flights
    // private JTable availableFlightsTable;
    // private JCheckBox changeFlightCheckbox;

    public RebookBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Rebook Cancelled Booking");
        setSize(900, 600);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Select Cancelled Booking to Rebook"));

        displayCancelledBookingsInTable();
        if (cancelledBookingsTable != null) {
            tablePanel.add(new JScrollPane(cancelledBookingsTable), BorderLayout.CENTER);
        } else {
            tablePanel.add(new JLabel("No cancelled bookings found.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        add(tablePanel, BorderLayout.CENTER);

        // If rebooking implies changing flights, you'd add another table here.
        // JPanel flightSelectionPanel = new JPanel(new BorderLayout());
        // flightSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select New Flight (Optional)"));
        // // Logic to display available flights
        // add(flightSelectionPanel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rebookBtn.addActionListener(this);
        closeBtn.addActionListener(this);
        buttonPanel.add(rebookBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void displayCancelledBookingsInTable() {
        List<Booking> bookingsList = mw.getFlightBookingSystem().getBookings();
        List<Booking> cancelledBookings = bookingsList.stream()
                                                      .filter(Booking::isCancelled)
                                                      .collect(Collectors.toList());

        String[] columns = new String[]{"Booking ID", "Flight No", "Customer Name", "Passengers", "Booking Date", "Cancellation Date"};
        Object[][] data = new Object[cancelledBookings.size()][6];
        for (int i = 0; i < cancelledBookings.size(); i++) {
            Booking booking = cancelledBookings.get(i);
            data[i][0] = booking.getId();
            data[i][1] = booking.getFlight().getFlightNumber();
            data[i][2] = booking.getCustomer().getName();
            data[i][3] = booking.getNumberOfPassengers();
            data[i][4] = booking.getBookingDate();
            data[i][5] = booking.getCancellationDate(); // Assuming you have getCancellationDate()
        }

        cancelledBookingsTable = new JTable(data, columns);
        cancelledBookingsTable.setFillsViewportHeight(true);
        cancelledBookingsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        cancelledBookingsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        cancelledBookingsTable.setRowHeight(20);
        cancelledBookingsTable.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cancelledBookingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        cancelledBookingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rebookBtn) {
            rebookBooking();
        } else if (e.getSource() == closeBtn) {
            dispose();
        }
    }

    private void rebookBooking() {
        int selectedRow = cancelledBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a cancelled booking to rebook.", "No Booking Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingIdToRebook = (int) cancelledBookingsTable.getValueAt(selectedRow, 0);
        Booking booking = mw.getFlightBookingSystem().getBookingById(bookingIdToRebook);

        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Selected booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!booking.isCancelled()) {
            JOptionPane.showMessageDialog(this, "Selected booking is not cancelled and cannot be rebooked.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to rebook Booking ID: " + bookingIdToRebook + "?\n" +
            "This will reactivate the booking, potentially on the original flight if available.",
            "Confirm Rebook", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // The RebookBooking command should handle checking flight availability
                // and reactivating the booking.
                // Depending on your RebookBooking command, it might take just booking ID
                // or also a new flight ID if the rebook process implies changing flights.
                Command rebookCommand = new RebookBooking(bookingIdToRebook);
                rebookCommand.execute(mw.getFlightBookingSystem(), null);

                JOptionPane.showMessageDialog(this, "Booking ID " + bookingIdToRebook + " rebooked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // Refresh the list of bookings (e.g., open ViewAllBookingsWindow)
                // new ViewAllBookingsWindow(mw);
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Rebook Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
