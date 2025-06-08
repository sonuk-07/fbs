package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ViewFlightWindow implements ActionListener {
    
    private MainWindow mainWindow;
    private FlightBookingSystem fbs;
    private JPanel panel;
    
    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    private JLabel titleLabel;
    private JPopupMenu flightPopupMenu;
    private JMenuItem viewDetailsMenuItem;
    private JMenuItem removeFlightMenuItem;
    
    private boolean showAllFlights = false;
    private boolean isVisible = false;

    public ViewFlightWindow(MainWindow mainWindow, FlightBookingSystem fbs) {
        this.mainWindow = mainWindow;
        this.fbs = fbs;
        initializeComponents();
    }
    
    private void initializeComponents() {
        panel = new JPanel(new BorderLayout());
        
        String[] columns = new String[]{
            "ID", "Flight No", "Origin", "Destination", "Date", "Type", "Economy Price (£)", "Total Capacity", "Status"
        };
        
        flightsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flightsTable = new JTable(flightsTableModel);
        setupTable();
        setupPopupMenu();
        
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);
    }
    
    private void setupTable() {
        flightsTable.setRowHeight(30);
        flightsTable.setShowGrid(true);
        flightsTable.setGridColor(new Color(220, 220, 220));
        flightsTable.setSelectionBackground(new Color(230, 240, 255));
        flightsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        flightsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        flightsTable.getTableHeader().setBackground(new Color(245, 245, 250));
        flightsTable.getTableHeader().setForeground(new Color(60, 60, 60));
        flightsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        flightsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        flightsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        flightsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        flightsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        flightsTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        flightsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        flightsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        flightsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        flightsTable.getColumnModel().getColumn(8).setPreferredWidth(70);
        
        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        flightsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        flightsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        flightsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        flightsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        flightsTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
    }
    
    private void setupPopupMenu() {
        flightPopupMenu = new JPopupMenu();
        viewDetailsMenuItem = new JMenuItem("View Details");
        removeFlightMenuItem = new JMenuItem("Remove Flight");
        
        viewDetailsMenuItem.addActionListener(this);
        removeFlightMenuItem.addActionListener(this);
        
        flightPopupMenu.add(viewDetailsMenuItem);
        flightPopupMenu.add(removeFlightMenuItem);
        
        flightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = flightsTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    flightsTable.setRowSelectionInterval(row, row);
                }
                showFlightPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showFlightPopup(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && flightsTable.getSelectedRow() != -1) {
                    viewSelectedFlightDetails();
                }
            }

            private void showFlightPopup(MouseEvent e) {
                if (e.isPopupTrigger() && flightsTable.getSelectedRow() != -1) {
                    String currentStatus = (String) flightsTableModel.getValueAt(flightsTable.getSelectedRow(), 8);
                    removeFlightMenuItem.setEnabled("Active".equals(currentStatus));
                    flightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    
    public void displayFlights(boolean showAll) {
        this.showAllFlights = showAll;
        this.isVisible = true;
        
        List<Flight> flightsList;
        String tableTitle;

        if (showAllFlights) {
            flightsList = fbs.getAllFlights();
            tableTitle = "All Flights (Including Removed)";
        } else {
            flightsList = fbs.getFlights();
            tableTitle = "Active Future Flights";
        }
        
        flightsTableModel.setRowCount(0);
        LocalDate today = LocalDate.now();

        for (Flight flight : flightsList) {
            String status;
            if (flight.isDeleted()) {
                status = "Removed";
            } else if (flight.getDepartureDate() != null && flight.getDepartureDate().isBefore(today)) {
                status = "Departed";
            } else {
                status = "Active";
            }

            flightsTableModel.addRow(new Object[]{
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate() != null ? 
                    flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "TBD",
                flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget",
                flight.getEconomyPrice().toPlainString(),
                flight.getCapacity(),
                status
            });
        }
        
        titleLabel.setText(tableTitle);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == viewDetailsMenuItem) {
            viewSelectedFlightDetails();
        } else if (ae.getSource() == removeFlightMenuItem) {
            removeSelectedFlight();
        }
    }
    
    private void viewSelectedFlightDetails() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainWindow, "Please select a flight to view details.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) flightsTableModel.getValueAt(selectedRow, 0);
        try {
            Flight selectedFlight = fbs.getFlightByIDIncludingDeleted(flightId);
            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(mainWindow, "Flight details could not be retrieved. It might have been permanently removed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new FlightDetailsWindow(mainWindow, selectedFlight);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainWindow, "Error viewing flight details: " + ex.getMessage(), "Flight Details Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainWindow, "No flight selected for removal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String status = (String) flightsTableModel.getValueAt(selectedRow, 8);
        if (status.equals("Removed")) {
            JOptionPane.showMessageDialog(mainWindow, "This flight has already been removed.", "Already Removed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (status.equals("Departed")) {
            JOptionPane.showMessageDialog(mainWindow, "Departed flights cannot be removed.", "Cannot Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int flightIdToDelete = (int) flightsTableModel.getValueAt(selectedRow, 0);
        String flightNumber = (String) flightsTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(mainWindow, 
            "Are you sure you want to remove flight " + flightNumber + " (ID: " + flightIdToDelete + ")?\n" +
            "This will mark the flight as removed and it will no longer appear in active lists.", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = fbs.removeFlightById(flightIdToDelete);

                if (removed) {
                    FlightBookingSystemData.store(fbs);
                    
                    JOptionPane.showMessageDialog(mainWindow, 
                        "Flight " + flightNumber + " (ID: " + flightIdToDelete + ") has been successfully marked as removed.", 
                        "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    displayFlights(showAllFlights);
                } else {
                    JOptionPane.showMessageDialog(mainWindow, 
                        "Flight with ID " + flightIdToDelete + " was not found or could not be removed.", 
                        "Removal Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FlightBookingSystemException | IOException ex) {
                JOptionPane.showMessageDialog(mainWindow, "Error removing flight: " + ex.getMessage(), "Removal Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow, "An unexpected error occurred during removal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void refreshView() {
        if (isVisible) {
            displayFlights(showAllFlights);
        }
    }
    
    public void cleanup() {
        isVisible = false;
    }
}