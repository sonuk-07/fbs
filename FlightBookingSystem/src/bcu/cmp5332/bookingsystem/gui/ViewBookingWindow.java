package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking; // Import Booking model
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter; // Import MouseAdapter
import java.awt.event.MouseEvent;   // Import MouseEvent
import java.time.format.DateTimeFormatter; // Import for date formatting

public class ViewBookingWindow {

    private JPanel mainPanel;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private FlightBookingSystem fbs;
    private MainWindow mw;

    // Define a consistent color palette (matching other GUI classes)
    private static final Color PRIMARY_BLUE = new Color(34, 107, 172);
    private static final Color LIGHT_GRAY_BG = new Color(245, 245, 245);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public ViewBookingWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_GRAY_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("All Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Customer", "Outbound Flight", "Return Flight", "Class", "Meal", "Booking Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookingsTable.setRowHeight(25);
        bookingsTable.setFillsViewportHeight(true); // Makes table fill the height of its scrollpane

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE); // Background color of the viewport
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1)); // Border for the scroll pane

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // NEW: Add MouseListener for double-click to view booking details
        bookingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int selectedRow = bookingsTable.getSelectedRow();
                    if (selectedRow != -1) { // A row is actually selected
                        // Ensure it's not the "No bookings to display." row
                        if (tableModel.getRowCount() > 0 && tableModel.getValueAt(selectedRow, 0) instanceof Integer) {
                            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                            try {
                                Booking selectedBooking = fbs.getBookingByID(bookingId); // Get booking from FBS
                                if (selectedBooking != null) {
                                    new BookingDetailsWindow(mw, selectedBooking); // Open details window
                                } else {
                                    JOptionPane.showMessageDialog(mainPanel, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(mainPanel, "Error retrieving booking details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * Populates the table with booking data from the FlightBookingSystem.
     * Displays all bookings, including cancelled ones, with their status.
     */
    public void displayBookings() {
        tableModel.setRowCount(0); // Clear existing data

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Retrieve all bookings from the FlightBookingSystem
            if (fbs.getBookings().isEmpty()) {
                // If no data, display a message
                bookingsTable.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                bookingsTable.setForeground(TEXT_DARK.darker());
                bookingsTable.clearSelection();
                tableModel.addRow(new Object[]{"", "", "", "No bookings to display.", "", "", "", ""});
                return; // Exit method if no bookings
            }
            
            bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            bookingsTable.setForeground(TEXT_DARK);

            for (Booking booking : fbs.getBookings()) {
                String customerName = booking.getCustomer() != null ? booking.getCustomer().getName() : "N/A";
                String outboundFlightNum = booking.getOutboundFlight() != null ? booking.getOutboundFlight().getFlightNumber() : "N/A";
                
                String returnFlightNum = "N/A";
                if (booking.getReturnFlight() != null) {
                    returnFlightNum = booking.getReturnFlight().getFlightNumber();
                }
                
                String bookedClass = booking.getBookedClass() != null ? booking.getBookedClass().getClassName() : "N/A";
                String mealName = booking.getMeal() != null ? booking.getMeal().getName() : "None";
                String bookingDate = booking.getBookingDate() != null ? booking.getBookingDate().format(dateFormatter) : "N/A";
                String status = booking.isCancelled() ? "Cancelled" : "Active"; // Get booking status

                tableModel.addRow(new Object[]{
                    booking.getId(),         // Booking ID (should be an Integer)
                    customerName,            // Customer Name
                    outboundFlightNum,       // Outbound Flight Number
                    returnFlightNum,         // Return Flight Number
                    bookedClass,             // Class
                    mealName,                // Meal Name
                    bookingDate,             // Booking Date
                    status                   // Booking Status
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "Error displaying bookings: " + e.getMessage(), "Display Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
        }
        mainPanel = null;
        bookingsTable = null;
        tableModel = null;
        fbs = null;
        mw = null;
        System.out.println("ViewBookingWindow cleaned up.");
    }

    public void refreshView() {
        displayBookings(); // Just re-display all bookings to refresh
    }
}