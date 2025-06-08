package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException; // Import for try-catch
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; // Import for saving data
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException; // For handling IOException from data storage
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane; // For showing messages
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class FlightDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Flight flight;

    private JLabel flightIdLabel = new JLabel(); 
    private JLabel flightNoLabel = new JLabel();
    private JLabel originLabel = new JLabel();
    private JLabel destinationLabel = new JLabel();
    private JLabel depDateLabel = new JLabel();
    private JLabel economyPriceLabel = new JLabel();
    private JLabel totalCapacityLabel = new JLabel();
    private JLabel flightTypeLabel = new JLabel();
    private JLabel statusLabel = new JLabel(); 

    private JPanel commercialClassPanel;
    private JTable classCapacityTable;

    private JButton closeBtn = new JButton("Close");
    private JButton removeFlightBtn = new JButton("Remove Flight"); // New remove button

    public FlightDetailsWindow(MainWindow mw, Flight flight) {
        this.mw = mw;
        this.flight = flight;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Flight Details: " + flight.getFlightNumber());
        setSize(550, 550); // Increased height to accommodate the button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridLayout(9, 2, 5, 5)); 
        infoPanel.setBorder(BorderFactory.createTitledBorder("Basic Flight Information"));

        infoPanel.add(new JLabel("Flight ID: ")); 
        flightIdLabel.setText(String.valueOf(flight.getId()));
        infoPanel.add(flightIdLabel);

        infoPanel.add(new JLabel("Flight No: "));
        flightNoLabel.setText(flight.getFlightNumber());
        infoPanel.add(flightNoLabel);

        infoPanel.add(new JLabel("Origin: "));
        originLabel.setText(flight.getOrigin());
        infoPanel.add(originLabel);

        infoPanel.add(new JLabel("Destination: "));
        destinationLabel.setText(flight.getDestination());
        infoPanel.add(destinationLabel);

        infoPanel.add(new JLabel("Departure Date: "));
        depDateLabel.setText(flight.getDepartureDate() != null ? 
                             flight.getDepartureDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        infoPanel.add(depDateLabel);

        infoPanel.add(new JLabel("Economy Price (£): "));
        economyPriceLabel.setText(flight.getEconomyPrice().toPlainString());
        infoPanel.add(economyPriceLabel);

        infoPanel.add(new JLabel("Total Capacity: "));
        totalCapacityLabel.setText(String.valueOf(flight.getCapacity()));
        infoPanel.add(totalCapacityLabel);
        
        infoPanel.add(new JLabel("Flight Type: "));
        flightTypeLabel.setText(flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget");
        infoPanel.add(flightTypeLabel);

        infoPanel.add(new JLabel("Status: ")); 
        statusLabel.setText(flight.isDeleted() ? "Removed" : "Active");
        statusLabel.setForeground(flight.isDeleted() ? Color.RED : Color.GREEN.darker());
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(statusLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        commercialClassPanel = new JPanel(new BorderLayout());
        commercialClassPanel.setBorder(BorderFactory.createTitledBorder("Seat Class Information (Available Seats)"));
        commercialClassPanel.setVisible(true);

        if (flight.getFlightType() == FlightType.COMMERCIAL) {
            setupCommercialClassTable();
        } else {
            int occupiedEconomy = flight.getOccupiedSeatsByClass(CommercialClassType.ECONOMY);
            int availableEconomy = flight.getCapacity() - occupiedEconomy;

            JLabel budgetInfoLabel = new JLabel(
                "<html>Budget Flight: All " + flight.getCapacity() + " seats are Economy Class.<br>" +
                "Available Seats: " + availableEconomy + "</html>"
            );
            budgetInfoLabel.setHorizontalAlignment(JLabel.CENTER);
            budgetInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            commercialClassPanel.add(budgetInfoLabel, BorderLayout.CENTER);
        }

        mainPanel.add(commercialClassPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(removeFlightBtn); // Add the remove button
        bottomPanel.add(closeBtn);
        
        removeFlightBtn.addActionListener(this);
        removeFlightBtn.setBackground(new Color(255, 100, 100)); // Red color for delete
        removeFlightBtn.setForeground(Color.WHITE);
        removeFlightBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeFlightBtn.setFocusPainted(false);
        removeFlightBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Disable remove button if flight is already deleted
        if (flight.isDeleted()) {
            removeFlightBtn.setEnabled(false);
            removeFlightBtn.setText("Flight Already Removed");
        }
        
        closeBtn.addActionListener(this);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void setupCommercialClassTable() {
        String[] columns = new String[]{"Class Type", "Total Capacity", "Available Seats"}; 
        
        Map<CommercialClassType, Integer> classCapacities = flight.getClassCapacities();
        
        CommercialClassType[] sortedTypes = CommercialClassType.values();
        
        Object[][] data = new Object[CommercialClassType.values().length][3];
        int i = 0;
        for (CommercialClassType type : sortedTypes) {
            data[i][0] = type.getClassName(); 
            int total = classCapacities.getOrDefault(type, 0);
            int occupied = flight.getOccupiedSeatsByClass(type);
            int available = total - occupied;
            
            data[i][1] = total;
            data[i][2] = available;
            i++;
        }

        classCapacityTable = new JTable(data, columns);
        
        classCapacityTable.setRowHeight(25);
        classCapacityTable.setShowGrid(true);
        classCapacityTable.setGridColor(new Color(220, 220, 220));
        classCapacityTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        classCapacityTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        classCapacityTable.getTableHeader().setBackground(new Color(245, 245, 250));
        classCapacityTable.getTableHeader().setForeground(new Color(60, 60, 60));
        classCapacityTable.getTableHeader().setReorderingAllowed(false);

        classCapacityTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        classCapacityTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        classCapacityTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        classCapacityTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        classCapacityTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        classCapacityTable.getColumnModel().getColumn(2).setCellRenderer(new AvailableSeatRenderer());

        JScrollPane scrollPane = new JScrollPane(classCapacityTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        commercialClassPanel.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeBtn) {
            this.dispose();
        } else if (ae.getSource() == removeFlightBtn) {
            removeFlight();
        }
    }
    
    private void removeFlight() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove flight " + flight.getFlightNumber() + " (ID: " + flight.getId() + ")?\n" +
            "This will mark the flight as removed and it will no longer appear in active lists.", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = mw.getFlightBookingSystem().removeFlightById(flight.getId()); 

                if (removed) {
                    FlightBookingSystemData.store(mw.getFlightBookingSystem());
                    
                    JOptionPane.showMessageDialog(this, 
                        "Flight " + flight.getFlightNumber() + " (ID: " + flight.getId() + ") has been successfully marked as removed.", 
                        "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update status label in this window
                    statusLabel.setText("Removed");
                    statusLabel.setForeground(Color.RED);
                    removeFlightBtn.setEnabled(false); // Disable button after removal
                    removeFlightBtn.setText("Flight Already Removed");
                    
                    mw.displayFlights(); // Refresh the main window's flight list
                    this.dispose(); // Close this details window
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Flight with ID " + flight.getId() + " was not found or could not be removed.", 
                        "Removal Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FlightBookingSystemException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Error removing flight: " + ex.getMessage(), "Removal Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during removal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class AvailableSeatRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setHorizontalAlignment(JLabel.CENTER);
            
            if (value instanceof Integer) {
                int availableSeats = (Integer) value;
                int totalCapacity = (int) table.getModel().getValueAt(row, 1);
                
                if (availableSeats == 0) {
                    setBackground(isSelected ? new Color(255, 200, 200) : new Color(255, 230, 230));
                    setForeground(new Color(150, 0, 0));
                } else if (availableSeats < totalCapacity * 0.3) {
                    setBackground(isSelected ? new Color(255, 220, 200) : new Color(255, 245, 230));
                    setForeground(new Color(200, 100, 0));
                } else {
                    setBackground(isSelected ? new Color(200, 255, 200) : new Color(240, 255, 240));
                    setForeground(new Color(0, 120, 0));
                }
            } else {
                setBackground(isSelected ? table.getSelectionBackground() : new Color(250, 250, 250));
                setForeground(new Color(150, 150, 150));
            }
            
            return this;
        }
    }
}