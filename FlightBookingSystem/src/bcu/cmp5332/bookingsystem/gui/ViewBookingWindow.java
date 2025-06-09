package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class ViewBookingWindow {

    private JPanel mainPanel;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private FlightBookingSystem fbs;
    private MainWindow mw;

    public ViewBookingWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.PRIMARY_BLUE);
        headerPanel.setBorder(DesignConstants.HEADER_PANEL_BORDER);
        JLabel titleLabel = new JLabel("All Bookings");
        titleLabel.setFont(DesignConstants.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Customer", "Outbound Flight", "Return Flight", "Class", "Meal", "Booking Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(DesignConstants.TABLE_ROW_FONT);
        bookingsTable.getTableHeader().setFont(DesignConstants.TABLE_HEADER_FONT);
        bookingsTable.setRowHeight(DesignConstants.TABLE_ROW_HEIGHT);
        bookingsTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(DesignConstants.SCROLL_PANE_BORDER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        bookingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = bookingsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        if (tableModel.getRowCount() > 0 && tableModel.getValueAt(selectedRow, 0) instanceof Integer) {
                            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                            try {
                                Booking selectedBooking = fbs.getBookingByID(bookingId);
                                if (selectedBooking != null) {
                                    new BookingDetailsWindow(mw, selectedBooking);
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

    public void displayBookings() {
        tableModel.setRowCount(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (fbs.getBookings().isEmpty()) {
                bookingsTable.setFont(DesignConstants.TABLE_EMPTY_MESSAGE_FONT);
                bookingsTable.setForeground(DesignConstants.TEXT_DARK.darker());
                bookingsTable.clearSelection();
                tableModel.addRow(new Object[]{"", "", "", "No bookings to display.", "", "", "", ""});
                return;
            }
            
            bookingsTable.setFont(DesignConstants.TABLE_ROW_FONT);
            bookingsTable.setForeground(DesignConstants.TEXT_DARK);

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
                String status = booking.isCancelled() ? "Cancelled" : "Active";

                tableModel.addRow(new Object[]{
                    booking.getId(),
                    customerName,
                    outboundFlightNum,
                    returnFlightNum,
                    bookedClass,
                    mealName,
                    bookingDate,
                    status
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
        displayBookings();
    }
}