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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate; // Import LocalDate

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
    private JLabel statusValueLabel = new JLabel(); // This will now display "Departed"

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
        setSize(680, 580);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.ETCHED_BORDER, "Basic Flight Information", TitledBorder.LEFT, TitledBorder.TOP,
            DesignConstants.TITLED_BORDER_FONT, DesignConstants.INFO_PANEL_TITLE_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H, DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Flight ID:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; flightIdValueLabel.setText(String.valueOf(flight.getId()));
        flightIdValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(flightIdValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Flight No:", true), gbc);
        gbc.gridx = 1; flightNoValueLabel.setText(flight.getFlightNumber());
        flightNoValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(flightNoValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Origin:", true), gbc);
        gbc.gridx = 1; originValueLabel.setText(flight.getOrigin());
        originValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(originValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Destination:", true), gbc);
        gbc.gridx = 1; destinationValueLabel.setText(flight.getDestination());
        destinationValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(destinationValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Departure Date:", true), gbc);
        gbc.gridx = 1; depDateValueLabel.setText(flight.getDepartureDate() != null ?
                                     flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        depDateValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(depDateValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Economy Price (£):", true), gbc);
        gbc.gridx = 1; economyPriceValueLabel.setText(flight.getEconomyPrice().toPlainString());
        economyPriceValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(economyPriceValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Total Capacity:", true), gbc);
        gbc.gridx = 1; totalCapacityValueLabel.setText(String.valueOf(flight.getCapacity()));
        totalCapacityValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(totalCapacityValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Flight Type:", true), gbc);
        gbc.gridx = 1; flightTypeValueLabel.setText(flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget");
        flightTypeValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(flightTypeValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Status:", true), gbc);
        gbc.gridx = 1;
        updateStatusLabel(); // Call the new method to set status
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        commercialClassPanel = new JPanel(new BorderLayout());
        commercialClassPanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.ETCHED_BORDER, "Seat Class Information (Available Seats)", TitledBorder.LEFT, TitledBorder.TOP,
            DesignConstants.TITLED_BORDER_FONT, DesignConstants.INFO_PANEL_TITLE_COLOR));
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
                "Available Seats: <span style='font-weight: bold; color: " + (availableEconomy > 0 ? toHtmlColor(DesignConstants.STATUS_ACTIVE_GREEN) : toHtmlColor(DesignConstants.STATUS_CANCELLED_RED)) + ";'>" + availableEconomy + "</span></div></html>"
            );
            budgetInfoLabel.setHorizontalAlignment(JLabel.CENTER);
            budgetInfoLabel.setVerticalAlignment(JLabel.CENTER);
            budgetInfoLabel.setFont(DesignConstants.BUDGET_INFO_FONT);
            commercialClassPanel.add(budgetInfoLabel, BorderLayout.CENTER);
        }

        mainPanel.add(commercialClassPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));

        customizeButton(removeFlightBtn, DesignConstants.REMOVE_BUTTON_RED, DesignConstants.REMOVE_BUTTON_HOVER_RED, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.BUTTON_TEXT_COLOR);
        bottomPanel.add(removeFlightBtn);

        customizeButton(closeBtn, DesignConstants.CLOSE_BUTTON_BG, DesignConstants.CLOSE_BUTTON_HOVER, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.BUTTON_TEXT_COLOR);
        bottomPanel.add(closeBtn);

        // Logic for enabling/disabling remove flight button
        if (flight.isDeleted()) {
            removeFlightBtn.setEnabled(false);
            removeFlightBtn.setText("Flight Already Removed");
            removeFlightBtn.setBackground(DesignConstants.DISABLED_BUTTON_BG);
            removeFlightBtn.addMouseListener(new MouseAdapter() { // Disable hover effect
                @Override public void mouseEntered(MouseEvent e) {}
                @Override public void mouseExited(MouseEvent e) {}
            });
        } else {
            // Check if flight has departed or has passengers
            if (flight.getDepartureDate().isBefore(mw.getFlightBookingSystem().getSystemDate())) {
                removeFlightBtn.setEnabled(false);
                removeFlightBtn.setText("Cannot Remove (Departed)");
                removeFlightBtn.setBackground(DesignConstants.DISABLED_BUTTON_BG);
                removeFlightBtn.addMouseListener(new MouseAdapter() { // Disable hover effect
                    @Override public void mouseEntered(MouseEvent e) {}
                    @Override public void mouseExited(MouseEvent e) {}
                });
            } else if (!flight.getPassengers().isEmpty()) {
                removeFlightBtn.setEnabled(false);
                removeFlightBtn.setText("Cannot Remove (Has Passengers)");
                removeFlightBtn.setBackground(DesignConstants.DISABLED_BUTTON_BG);
                removeFlightBtn.addMouseListener(new MouseAdapter() { // Disable hover effect
                    @Override public void mouseEntered(MouseEvent e) {}
                    @Override public void mouseExited(MouseEvent e) {}
                });
            }
        }

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    /**
     * Determines and sets the text and color for the flight status label.
     */
    private void updateStatusLabel() {
        if (flight.isDeleted()) {
            statusValueLabel.setText("Removed");
            statusValueLabel.setForeground(DesignConstants.STATUS_CANCELLED_RED);
        } else if (flight.getDepartureDate().isBefore(mw.getFlightBookingSystem().getSystemDate())) {
            // Flight has departed
            statusValueLabel.setText("Departed");
            statusValueLabel.setForeground(DesignConstants.STATUS_DEPARTED_ORANGE); // You'll need to define this color
        } else {
            // Flight is active and has not departed
            statusValueLabel.setText("Active");
            statusValueLabel.setForeground(DesignConstants.STATUS_ACTIVE_GREEN);
        }
        statusValueLabel.setFont(DesignConstants.STATUS_FONT);
    }


    private JLabel createLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setForeground(DesignConstants.TEXT_DARK);
        label.setFont(isBold ? DesignConstants.INFO_LABEL_FONT.deriveFont(Font.BOLD) : DesignConstants.INFO_LABEL_FONT);
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

    private String toHtmlColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
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

        classCapacityTable.setRowHeight(DesignConstants.TABLE_ROW_HEIGHT);
        classCapacityTable.setShowGrid(true);
        classCapacityTable.setGridColor(DesignConstants.TABLE_GRID_COLOR);
        classCapacityTable.setFont(DesignConstants.TABLE_ROW_FONT);
        classCapacityTable.getTableHeader().setFont(DesignConstants.TABLE_HEADER_FONT);
        classCapacityTable.getTableHeader().setBackground(DesignConstants.TABLE_HEADER_BG_COLOR);
        classCapacityTable.getTableHeader().setForeground(DesignConstants.TABLE_HEADER_FOREGROUND_COLOR);
        classCapacityTable.getTableHeader().setReorderingAllowed(false);

        classCapacityTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        classCapacityTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        classCapacityTable.getColumnModel().getColumn(2).setPreferredWidth(120);

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
                // Ensure no passengers or has departed before trying to remove
                if (flight.getDepartureDate().isBefore(mw.getFlightBookingSystem().getSystemDate())) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot remove a flight that has already departed.",
                        "Removal Failed", JOptionPane.WARNING_MESSAGE);
                    return; // Exit if already departed
                }
                if (!flight.getPassengers().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot remove a flight with existing passengers. Please remove all bookings first.",
                        "Removal Failed", JOptionPane.WARNING_MESSAGE);
                    return; // Exit if has passengers
                }

                boolean removed = mw.getFlightBookingSystem().removeFlightById(flight.getId());

                if (removed) {
                    FlightBookingSystemData.store(mw.getFlightBookingSystem());

                    JOptionPane.showMessageDialog(this,
                        "Flight " + flight.getFlightNumber() + " (ID: " + flight.getId() + ") has been successfully marked as removed.",
                        "Removal Successful", JOptionPane.INFORMATION_MESSAGE);

                    // Update UI immediately after successful removal
                    updateStatusLabel(); // Re-evaluate status (will now be "Removed")
                    removeFlightBtn.setEnabled(false);
                    removeFlightBtn.setText("Flight Already Removed");
                    removeFlightBtn.setBackground(DesignConstants.DISABLED_BUTTON_BG);
                    removeFlightBtn.addMouseListener(new MouseAdapter() { // Disable hover effect
                        @Override public void mouseEntered(MouseEvent e) {}
                        @Override public void mouseExited(MouseEvent e) {}
                    });

                    mw.displayFlights(); // Refresh the main window's flight list
                    this.dispose(); // Close the details window
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
                    setBackground(isSelected ? DesignConstants.AVAILABLE_SEATS_NONE_BG.darker() : DesignConstants.AVAILABLE_SEATS_NONE_BG);
                    setForeground(DesignConstants.AVAILABLE_SEATS_NONE_FG);
                } else if (totalCapacity > 0 && availableSeats < totalCapacity * 0.3) {
                    setBackground(isSelected ? DesignConstants.AVAILABLE_SEATS_LOW_BG.darker() : DesignConstants.AVAILABLE_SEATS_LOW_BG);
                    setForeground(DesignConstants.AVAILABLE_SEATS_LOW_FG);
                } else {
                    setBackground(isSelected ? DesignConstants.AVAILABLE_SEATS_FULL.darker() : DesignConstants.AVAILABLE_SEATS_FULL);
                    setForeground(DesignConstants.BUTTON_TEXT_COLOR);
                }
            } else {
                setBackground(isSelected ? table.getSelectionBackground() : DesignConstants.TABLE_CELL_DEFAULT_BG);
                setForeground(DesignConstants.TEXT_MUTED);
            }

            return this;
        }
    }
}