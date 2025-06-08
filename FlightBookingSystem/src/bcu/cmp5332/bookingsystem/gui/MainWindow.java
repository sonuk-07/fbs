package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate; // Import LocalDate for date comparison
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter for formatting
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;

    private JMenuItem adminExit;

    private JMenuItem flightsViewActive;
    private JMenuItem flightsViewAll;
    private JMenuItem flightsAdd;

    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;

    private FlightBookingSystem fbs;
    
    private JTable flightsTable; 
    private DefaultTableModel flightsTableModel;
    private JLabel titleLabel; 
    
    private JPopupMenu flightPopupMenu;
    private JMenuItem viewDetailsMenuItem;
    private JMenuItem removeFlightMenuItem;

    private boolean showAllFlights = false; 
    
    private JPanel contentPanel; 

    public MainWindow(FlightBookingSystem fbs) {
        initialize();
        this.fbs = fbs;
        displayFlights(); 
    }
    
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Flight Booking Management System");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);

        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        flightsMenu = new JMenu("Flights");
        menuBar.add(flightsMenu);

        flightsViewActive = new JMenuItem("View Active Flights Only");
        flightsViewAll = new JMenuItem("View All Flights (Including Removed)");
        flightsAdd = new JMenuItem("Add Flight");
        
        flightsMenu.add(flightsViewActive);
        flightsMenu.add(flightsViewAll);
        flightsMenu.addSeparator();
        flightsMenu.add(flightsAdd);
        
        flightsViewActive.addActionListener(this);
        flightsViewAll.addActionListener(this);
        flightsAdd.addActionListener(this);
        
        bookingsMenu = new JMenu("Bookings");
        menuBar.add(bookingsMenu);
        
        bookingsIssue = new JMenuItem("Issue Booking");
        bookingsUpdate = new JMenuItem("Update Booking");
        bookingsCancel = new JMenuItem("Cancel Booking");
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);
        for (int i = 0; i < bookingsMenu.getItemCount(); i++) {
            bookingsMenu.getItem(i).addActionListener(this);
        }

        customersMenu = new JMenu("Customers");
        menuBar.add(customersMenu);

        custView = new JMenuItem("View Customers");
        custAdd = new JMenuItem("Add Customer");
        custDel = new JMenuItem("Delete Customer");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);
        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);

        setSize(1200, 600); 

        contentPanel = new JPanel(new BorderLayout());
        this.getContentPane().add(contentPanel); 
        
        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }	

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == adminExit) {
            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        } else if (ae.getSource() == flightsViewActive) {
            showAllFlights = false;
            displayFlights();
        } else if (ae.getSource() == flightsViewAll) {
            showAllFlights = true;
            displayFlights();
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
        } else if (ae.getSource() == viewDetailsMenuItem) {
            viewSelectedFlightDetails();
        } else if (ae.getSource() == removeFlightMenuItem) {
            removeSelectedFlight();
        } 
        else if (ae.getSource() == bookingsIssue) {
            // TODO: Implement booking issue functionality
        } else if (ae.getSource() == bookingsUpdate) {
            // TODO: Implement booking update functionality
        } else if (ae.getSource() == bookingsCancel) {
            // TODO: Implement booking cancellation functionality
        } else if (ae.getSource() == custView) {
            // TODO: Implement customer view functionality
        } else if (ae.getSource() == custAdd) {
            // TODO: Implement customer add functionality
        } else if (ae.getSource() == custDel) {
            // TODO: Implement customer delete functionality
        }
    }

    public void displayFlights() {
        List<Flight> flightsList;
        String tableTitle;

        if (showAllFlights) {
            flightsList = fbs.getAllFlights();
            tableTitle = "All Flights (Including Removed)";
        } else {
            // For active flights, filter out departed ones unless explicitly viewing all
            flightsList = fbs.getFlights(); // This should get future active flights
            tableTitle = "Active Future Flights";
        }
        
        String[] columns = new String[]{
            "ID", "Flight No", "Origin", "Destination", "Date", "Type", "Economy Price (£)", "Total Capacity", "Status"
        };

        if (flightsTableModel == null) {
            flightsTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            flightsTable = new JTable(flightsTableModel);

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
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && flightsTable.getSelectedRow() != -1) {
                        viewSelectedFlightDetails();
                    }
                }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger() && flightsTable.getSelectedRow() != -1) {
                        // Get the status from the table, not just checking isDeleted()
                        String currentStatus = (String) flightsTableModel.getValueAt(flightsTable.getSelectedRow(), 8);
                        // Only enable remove if the flight is 'Active' (not 'Removed' or 'Departed')
                        removeFlightMenuItem.setEnabled("Active".equals(currentStatus));
                        flightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            
            flightsTable.setRowHeight(30);
            flightsTable.setShowGrid(true);
            flightsTable.setGridColor(new Color(220, 220, 220));
            flightsTable.setSelectionBackground(new Color(230, 240, 255));
            flightsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
            
            flightsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
            flightsTable.getTableHeader().setBackground(new Color(245, 245, 250));
            flightsTable.getTableHeader().setForeground(new Color(60, 60, 60));
            flightsTable.getTableHeader().setReorderingAllowed(false);
            
            flightsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
            flightsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
            flightsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            flightsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            flightsTable.getColumnModel().getColumn(4).setPreferredWidth(90);
            flightsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
            flightsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
            flightsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
            flightsTable.getColumnModel().getColumn(8).setPreferredWidth(70);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            flightsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            flightsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            flightsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
            flightsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
            flightsTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

            titleLabel = new JLabel(); 

            contentPanel.removeAll(); 
            
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.add(titleLabel, BorderLayout.NORTH); 
            contentPanel.add(new JScrollPane(flightsTable), BorderLayout.CENTER); 
            contentPanel.revalidate();
            contentPanel.repaint();

        }

        flightsTableModel.setRowCount(0); 
        LocalDate today = LocalDate.now(); // Get current date

        for (Flight flight : flightsList) {
            String status;
            if (flight.isDeleted()) {
                status = "Removed";
            } else if (flight.getDepartureDate() != null && flight.getDepartureDate().isBefore(today)) {
                status = "Departed"; // Flight has departed
            } else {
                status = "Active"; // Flight is active and in the future
            }

            flightsTableModel.addRow(new Object[]{
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                // Use DateTimeFormatter for consistency if needed, though direct access is fine
                flight.getDepartureDate() != null ? 
                    flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "TBD",
                flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget",
                flight.getEconomyPrice().toPlainString(),
                flight.getCapacity(),
                status
            });
        }
        
        titleLabel.setText(tableTitle); 
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void viewSelectedFlightDetails() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to view details.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) flightsTableModel.getValueAt(selectedRow, 0); 
        try {
            // Ensure you get the flight by ID from the FBS, which contains all flights
            Flight selectedFlight = fbs.getFlightByIDIncludingDeleted(flightId); 
            if (selectedFlight == null) {
                 JOptionPane.showMessageDialog(MainWindow.this, "Flight details could not be retrieved. It might have been permanently removed.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            new FlightDetailsWindow(MainWindow.this, selectedFlight);
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(MainWindow.this, "Error viewing flight details: " + ex.getMessage(), "Flight Details Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No flight selected for removal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get the current status from the table row
        String status = (String) flightsTableModel.getValueAt(selectedRow, 8);
        if (status.equals("Removed")) {
            JOptionPane.showMessageDialog(this, "This flight has already been removed.", "Already Removed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (status.equals("Departed")) {
            JOptionPane.showMessageDialog(this, "Departed flights cannot be removed.", "Cannot Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int flightIdToDelete = (int) flightsTableModel.getValueAt(selectedRow, 0); 
        String flightNumber = (String) flightsTableModel.getValueAt(selectedRow, 1); 

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to remove flight " + flightNumber + " (ID: " + flightIdToDelete + ")?\n" +
            "This will mark the flight as removed and it will no longer appear in active lists.", 
            "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = fbs.removeFlightById(flightIdToDelete); 

                if (removed) {
                    FlightBookingSystemData.store(fbs);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Flight " + flightNumber + " (ID: " + flightIdToDelete + ") has been successfully marked as removed.", 
                        "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    displayFlights(); 
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Flight with ID " + flightIdToDelete + " was not found or could not be removed.", 
                        "Removal Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FlightBookingSystemException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Error removing flight: " + ex.getMessage(), "Removal Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during removal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}