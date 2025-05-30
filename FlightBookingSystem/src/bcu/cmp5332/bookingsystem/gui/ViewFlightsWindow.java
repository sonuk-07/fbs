package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Flight;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ViewFlightsWindow extends JFrame {

    private MainWindow mw;
    private JTable flightsTable;

    public ViewFlightsWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("All Flights");
        setSize(1000, 500);
        setLocationRelativeTo(mw); // Center relative to main window
        setLayout(new BorderLayout());

        // Display flights in a table
        List<Flight> flightsList = mw.getFlightBookingSystem().getFlights();
        String[] columns = new String[]{"ID", "Flight No", "Origin", "Destination", "Departure Date", "Economy Price", "Capacity", "Flight Type", "Status"};

        Object[][] data = new Object[flightsList.size()][9];
        for (int i = 0; i < flightsList.size(); i++) {
            Flight flight = flightsList.get(i);
            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = flight.getEconomyPrice();
            data[i][6] = flight.getCapacity();
            data[i][7] = flight.getFlightType();
            data[i][8] = flight.isDeleted() ? "Removed" : "Active";
        }

        flightsTable = new JTable(data, columns);
        flightsTable.setFillsViewportHeight(true);
        flightsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        flightsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        flightsTable.setRowHeight(20);
        flightsTable.setDefaultEditor(Object.class, null); // Disable editing

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        flightsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        flightsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Price
        flightsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Capacity

        add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        setVisible(true);
    }
}