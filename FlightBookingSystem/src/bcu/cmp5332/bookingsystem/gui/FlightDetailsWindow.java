package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class FlightDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Flight flight;

    private JLabel flightIdValueLabel = new JLabel(); 
    private JLabel flightNoValueLabel = new JLabel();
    private JLabel originValueLabel = new JLabel();
    private JLabel destinationValueLabel = new JLabel();
    private JLabel depDateValueLabel = new JLabel();
    private JLabel economyPriceValueLabel = new JLabel();
    private JLabel totalCapacityValueLabel = new JLabel();
    private JLabel flightTypeValueLabel = new JLabel();
    private JLabel statusValueLabel = new JLabel(); 

    private JPanel commercialClassPanel;
    private JTable classCapacityTable;
    private DefaultTableModel classCapacityTableModel;

    private JButton closeBtn = new JButton("Close");
    private JButton removeFlightBtn = new JButton("Remove Flight");

    public FlightDetailsWindow(MainWindow mw, Flight flight) {
        this.mw = mw;
        this.flight = flight;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set LookAndFeel: " + ex);
        }

        setTitle("Flight Details: " + flight.getFlightNumber());
        setSize(600, 580);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Basic Flight Information", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 150)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Flight ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; flightIdValueLabel.setText(String.valueOf(flight.getId()));
        infoPanel.add(flightIdValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Flight No:"), gbc);
        gbc.gridx = 1; flightNoValueLabel.setText(flight.getFlightNumber());
        infoPanel.add(flightNoValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1; originValueLabel.setText(flight.getOrigin());
        infoPanel.add(originValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; destinationValueLabel.setText(flight.getDestination());
        infoPanel.add(destinationValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Departure Date:"), gbc);
        gbc.gridx = 1; depDateValueLabel.setText(flight.getDepartureDate() != null ? 
                             flight.getDepartureDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        infoPanel.add(depDateValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Economy Price (£):"), gbc);
        gbc.gridx = 1; economyPriceValueLabel.setText(flight.getEconomyPrice().toPlainString());
        infoPanel.add(economyPriceValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Total Capacity:"), gbc);
        gbc.gridx = 1; totalCapacityValueLabel.setText(String.valueOf(flight.getCapacity()));
        infoPanel.add(totalCapacityValueLabel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Flight Type:"), gbc);
        gbc.gridx = 1; flightTypeValueLabel.setText(flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget");
        infoPanel.add(flightTypeValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; 
        statusValueLabel.setText(flight.isDeleted() ? "Removed" : "Active");
        statusValueLabel.setForeground(flight.isDeleted() ? new Color(200, 50, 50) : new Color(50, 150, 50));
        statusValueLabel.setFont(statusValueLabel.getFont().deriveFont(Font.BOLD, 12f));
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        commercialClassPanel = new JPanel(new BorderLayout());
        commercialClassPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Seat Class Information (Available Seats)", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 150)));
        commercialClassPanel.setVisible(true);

        if (flight.getFlightType() == FlightType.COMMERCIAL) {
            setupCommercialClassTable();
            JScrollPane scrollPane = new JScrollPane(classCapacityTable);
            scrollPane.getViewport().setBackground(Color.WHITE);
            commercialClassPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            int occupiedEconomy = flight.getOccupiedSeatsByClass(CommercialClassType.ECONOMY);
            int availableEconomy = flight.getCapacity() - occupiedEconomy;

            JLabel budgetInfoLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                "Budget Flight: All " + flight.getCapacity() + " seats are Economy Class.<br>" +
                "Available Seats: <span style='font-weight: bold; color: " + (availableEconomy > 0 ? "green" : "red") + ";'>" + availableEconomy + "</span></div></html>"
            );
            budgetInfoLabel.setHorizontalAlignment(JLabel.CENTER);
            budgetInfoLabel.setVerticalAlignment(JLabel.CENTER);
            budgetInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            commercialClassPanel.add(budgetInfoLabel, BorderLayout.CENTER);
        }

        mainPanel.add(commercialClassPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        removeFlightBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeFlightBtn.setBackground(new Color(200, 70, 70));
        removeFlightBtn.setForeground(Color.WHITE);
        removeFlightBtn.setFocusPainted(false);
        removeFlightBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        bottomPanel.add(removeFlightBtn);

        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        closeBtn.setBackground(new Color(100, 150, 200));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        bottomPanel.add(closeBtn);
        
        removeFlightBtn.addActionListener(this);
        closeBtn.addActionListener(this);

        if (flight.isDeleted()) {
            removeFlightBtn.setEnabled(false);
            removeFlightBtn.setText("Flight Already Removed");
        } else {
            // FIX: Check if flight has passengers instead of bookings
            if (!flight.getPassengers().isEmpty()) { 
                 removeFlightBtn.setEnabled(false);
                 removeFlightBtn.setText("Cannot Remove (Has Passengers)"); // Updated message
                 removeFlightBtn.setBackground(new Color(180, 180, 180));
            }
        }

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void setupCommercialClassTable() {
        String[] columns = new String[]{"Class Type", "Total Capacity", "Available Seats"}; 
        classCapacityTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        Map<CommercialClassType, Integer> classCapacities = flight.getClassCapacities();
        
        CommercialClassType[] displayOrder = {
            CommercialClassType.FIRST,
            CommercialClassType.BUSINESS,
            CommercialClassType.PREMIUM_ECONOMY,
            CommercialClassType.ECONOMY
        };
        
        for (CommercialClassType type : displayOrder) {
            int total = classCapacities.getOrDefault(type, 0);
            int occupied = flight.getOccupiedSeatsByClass(type);
            int available = total - occupied;
            
            classCapacityTableModel.addRow(new Object[]{
                type.getClassName(),
                total,
                available
            });
        }

        classCapacityTable = new JTable(classCapacityTableModel);
        
        classCapacityTable.setRowHeight(25);
        classCapacityTable.setShowGrid(true);
        classCapacityTable.setGridColor(new Color(220, 220, 220));
        classCapacityTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        classCapacityTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        classCapacityTable.getTableHeader().setBackground(new Color(230, 230, 240));
        classCapacityTable.getTableHeader().setForeground(new Color(40, 40, 40));
        classCapacityTable.getTableHeader().setReorderingAllowed(false);

        classCapacityTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        classCapacityTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        classCapacityTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        classCapacityTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        classCapacityTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        classCapacityTable.getColumnModel().getColumn(2).setCellRenderer(new AvailableSeatRenderer());
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
                    
                    statusValueLabel.setText("Removed");
                    statusValueLabel.setForeground(new Color(200, 50, 50));
                    removeFlightBtn.setEnabled(false);
                    removeFlightBtn.setText("Flight Already Removed");
                    
                    mw.displayFlights();
                    this.dispose();
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
                int totalCapacity = 0;
                try {
                    totalCapacity = (int) table.getModel().getValueAt(row, 1);
                } catch (ClassCastException | IndexOutOfBoundsException e) {
                    System.err.println("Error getting total capacity for renderer: " + e.getMessage());
                }
                
                if (availableSeats == 0) {
                    setBackground(isSelected ? new Color(255, 180, 180) : new Color(255, 220, 220));
                    setForeground(new Color(180, 0, 0));
                } else if (totalCapacity > 0 && availableSeats < totalCapacity * 0.3) {
                    setBackground(isSelected ? new Color(255, 220, 180) : new Color(255, 240, 220));
                    setForeground(new Color(200, 100, 0));
                } else {
                    setBackground(isSelected ? new Color(180, 255, 180) : new Color(220, 255, 220));
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