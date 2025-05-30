package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.RemoveFlight; // Corrected to your command
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RemoveFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTable flightTable; // Table to display flights for selection
    private JButton removeBtn = new JButton("Remove Selected Flight");
    private JButton cancelBtn = new JButton("Cancel");

    public RemoveFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Remove Flight");
        setSize(800, 400);
        setLocationRelativeTo(mw); // Center relative to main window
        setLayout(new BorderLayout());

        // Panel for table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Select Flight to Remove"));

        // Display flights in a table
        displayFlightsInTable();
        if (flightTable != null) { // Only add if table was successfully created
            tablePanel.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        } else {
            tablePanel.add(new JLabel("No flights to display or an error occurred.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        add(tablePanel, BorderLayout.CENTER);


        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void displayFlightsInTable() {
        List<Flight> flightsList = mw.getFlightBookingSystem().getFlights();
        // Filter to only show active (not removed) flights if desired, or show all with status
        List<Flight> activeFlights = flightsList.stream()
                                            .filter(f -> !f.isDeleted())
                                            .collect(java.util.stream.Collectors.toList());


        String[] columns = new String[]{"ID", "Flight No", "Origin", "Destination", "Departure Date", "Economy Price", "Capacity", "Flight Type", "Status"};

        Object[][] data = new Object[activeFlights.size()][9]; // Only show active flights here
        for (int i = 0; i < activeFlights.size(); i++) {
            Flight flight = activeFlights.get(i);
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

        flightTable = new JTable(data, columns);
        flightTable.setFillsViewportHeight(true);
        flightTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        flightTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        flightTable.setRowHeight(20);
        flightTable.setDefaultEditor(Object.class, null); // Disable editing

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        flightTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        flightTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Price
        flightTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Capacity

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removeBtn) {
            removeFlight();
        } else if (e.getSource() == cancelBtn) {
            dispose(); // Close this window
        }
    }

    private void removeFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight from the table to remove.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightIdToRemove = (int) flightTable.getValueAt(selectedRow, 0); // Get ID from first column
        String flightNumberDisplay = flightTable.getValueAt(selectedRow, 1).toString(); // Get Flight No for display

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove Flight " + flightNumberDisplay + " (ID: " + flightIdToRemove + ")?\n" +
            "This action cannot be undone.",
            "Confirm Flight Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Command removeFlightCommand = new RemoveFlight(flightIdToRemove);
                removeFlightCommand.execute(mw.getFlightBookingSystem(), null); // Execute the command

                JOptionPane.showMessageDialog(this, "Flight " + flightNumberDisplay + " removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close current window
                new ViewFlightsWindow(mw); // Re-open view flights window to show updated list
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Removing Flight", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}