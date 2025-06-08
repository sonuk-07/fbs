package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.MealType;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

    // Renamed and New Customer Menu Items
    private JMenuItem custViewActive; // Renamed from custView
    private JMenuItem custViewAll;    // NEW: For viewing all customers including deleted
    private JMenuItem custAdd;
    private JMenuItem custDel; // Consider removing if only popup delete is used

    private FlightBookingSystem fbs;
    
    // Flight related GUI components
    private JTable flightsTable; 
    private DefaultTableModel flightsTableModel;
    private JLabel flightsTitleLabel;
    private JPopupMenu flightPopupMenu;
    private JMenuItem viewDetailsMenuItem;
    private JMenuItem removeFlightMenuItem;

    private boolean showAllFlights = false; 
    
    // Customer related GUI components
    private JTable customersTable;
    private DefaultTableModel customersTableModel;
    private JLabel customersTitleLabel;
    private JPopupMenu customerPopupMenu;
    private JMenuItem viewCustomerDetailsMenuItem;
    private JMenuItem removeCustomerMenuItem;

    private boolean showAllCustomers = false; // NEW: Flag to show all customers (active/deleted)

    private JPanel contentPanel; 
    
    private CurrentView currentView = CurrentView.FLIGHTS; 
    
    private enum CurrentView {
        FLIGHTS,
        CUSTOMERS
    }

    public MainWindow(FlightBookingSystem fbs) {
        initialize();
        this.fbs = fbs;
        displayFlights(); // Default view on startup
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

        // UPDATED Customer Menu Items
        custViewActive = new JMenuItem("View Active Customers Only"); // Renamed
        custViewAll = new JMenuItem("View All Customers (Including Deleted)"); // NEW
        custAdd = new JMenuItem("Add Customer");
        custDel = new JMenuItem("Delete Customer");

        customersMenu.add(custViewActive);  // Add active view
        customersMenu.add(custViewAll);     // Add all view
        customersMenu.addSeparator();       // Separator for better organization
        customersMenu.add(custAdd);
        customersMenu.add(custDel);
        
        custViewActive.addActionListener(this); // Listener for active view
        custViewAll.addActionListener(this);    // Listener for all view (NEW)
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
        } 
        // Flight Menu Actions
        else if (ae.getSource() == flightsViewActive) {
            showAllFlights = false;
            displayFlights();
        } else if (ae.getSource() == flightsViewAll) {
            showAllFlights = true;
            displayFlights();
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
        } else if (ae.getSource() == viewDetailsMenuItem) { // Flight View Details
            viewSelectedFlightDetails();
        } else if (ae.getSource() == removeFlightMenuItem) {
            removeSelectedFlight();
        } 
        // Customer Menu Actions (UPDATED)
        else if (ae.getSource() == custViewActive) { // Handle active customer view
            showAllCustomers = false; // Set flag to show only active
            displayCustomers();
        } else if (ae.getSource() == custViewAll) { // NEW: Handle all customer view
            showAllCustomers = true; // Set flag to show all (including deleted)
            displayCustomers();
        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);
        } else if (ae.getSource() == custDel) {
            JOptionPane.showMessageDialog(this, "Please right-click on a customer to delete.", "Delete Customer", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource() == viewCustomerDetailsMenuItem) {
            viewSelectedCustomerDetails();
        } else if (ae.getSource() == removeCustomerMenuItem) {
            removeSelectedCustomer();
        }
        // Booking Menu Actions
        else if (ae.getSource() == bookingsIssue) {
            // TODO: Implement booking issue functionality
        } else if (ae.getSource() == bookingsUpdate) {
            // TODO: Implement booking update functionality
        } else if (ae.getSource() == bookingsCancel) {
            // TODO: Implement booking cancellation functionality
        }
    }

    public void displayFlights() {
        currentView = CurrentView.FLIGHTS;
        List<Flight> flightsList;
        String tableTitle;

        if (showAllFlights) {
            flightsList = fbs.getAllFlights();
            tableTitle = "All Flights (Including Removed)";
        } else {
            flightsList = fbs.getFlights(); 
            tableTitle = "Active Future Flights";
        }
        
        String[] columns = new String[]{
            "ID", "Flight No", "Origin", "Destination", "Date", "Type", "Economy Price (£)", "Total Capacity", "Status"
        };

        if (flightsTableModel == null || currentView == CurrentView.CUSTOMERS) { // Re-initialize if null or switching views
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

                flightsTitleLabel = new JLabel();
            }

            // Always clear content panel and add flight-specific components when switching or initializing
            contentPanel.removeAll();
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.add(flightsTitleLabel, BorderLayout.NORTH); 
            contentPanel.add(new JScrollPane(flightsTable), BorderLayout.CENTER); 
            contentPanel.revalidate();
            contentPanel.repaint();
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
        
        flightsTitleLabel.setText(tableTitle); 
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // displayCustomers() Method (UPDATED)
    public void displayCustomers() {
        currentView = CurrentView.CUSTOMERS;
        List<Customer> customersList;
        String tableTitle;

        // NEW LOGIC: Filter customers based on showAllCustomers flag
        if (showAllCustomers) {
            customersList = fbs.getAllCustomers(); // Get all customers (including deleted)
            tableTitle = "All Customers (Including Deleted)";
        } else {
            customersList = fbs.getCustomers(); // Get active customers only
            tableTitle = "Active Customers";
        }

        String[] columns = new String[]{
            "ID", "Name", "Phone", "Email", "Age", "Gender", "Preferred Meal", "Status"
        };

        if (customersTableModel == null || currentView == CurrentView.FLIGHTS) { // Re-initialize if null or switching views
            if (customersTableModel == null) {
                customersTableModel = new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                customersTable = new JTable(customersTableModel);

                customerPopupMenu = new JPopupMenu();
                viewCustomerDetailsMenuItem = new JMenuItem("View Details");
                removeCustomerMenuItem = new JMenuItem("Delete Customer");

                viewCustomerDetailsMenuItem.addActionListener(this);
                removeCustomerMenuItem.addActionListener(this);

                customerPopupMenu.add(viewCustomerDetailsMenuItem);
                customerPopupMenu.add(removeCustomerMenuItem);

                customersTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        int row = customersTable.rowAtPoint(e.getPoint());
                        if (row != -1) {
                            customersTable.setRowSelectionInterval(row, row);
                        }
                        showCustomerPopup(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showCustomerPopup(e);
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2 && customersTable.getSelectedRow() != -1) {
                            viewSelectedCustomerDetails();
                        }
                    }

                    private void showCustomerPopup(MouseEvent e) {
                        if (e.isPopupTrigger() && customersTable.getSelectedRow() != -1) {
                            String currentStatus = (String) customersTableModel.getValueAt(customersTable.getSelectedRow(), 7);
                            removeCustomerMenuItem.setEnabled("Active".equals(currentStatus));
                            customerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
                
                customersTable.setRowHeight(30);
                customersTable.setShowGrid(true);
                customersTable.setGridColor(new Color(220, 220, 220));
                customersTable.setSelectionBackground(new Color(230, 240, 255));
                customersTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                customersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
                customersTable.getTableHeader().setBackground(new Color(245, 245, 250));
                customersTable.getTableHeader().setForeground(new Color(60, 60, 60));
                customersTable.getTableHeader().setReorderingAllowed(false);
                
                customersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
                customersTable.getColumnModel().getColumn(1).setPreferredWidth(120);
                customersTable.getColumnModel().getColumn(2).setPreferredWidth(90);
                customersTable.getColumnModel().getColumn(3).setPreferredWidth(150);
                customersTable.getColumnModel().getColumn(4).setPreferredWidth(40);
                customersTable.getColumnModel().getColumn(5).setPreferredWidth(70);
                customersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
                customersTable.getColumnModel().getColumn(7).setPreferredWidth(70);

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                customersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                customersTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
                customersTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
                customersTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

                customersTitleLabel = new JLabel();
            }

            // Always clear content panel and add customer-specific components when switching or initializing
            contentPanel.removeAll();
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.add(customersTitleLabel, BorderLayout.NORTH); 
            contentPanel.add(new JScrollPane(customersTable), BorderLayout.CENTER); 
            contentPanel.revalidate();
            contentPanel.repaint();
        }

        customersTableModel.setRowCount(0);
        for (Customer customer : customersList) {
            String status = customer.isDeleted() ? "Removed" : "Active";
            customersTableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender(),
                customer.getPreferredMealType().getDisplayName(),
                status
            });
        }
        
        customersTitleLabel.setText(tableTitle); 
        
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

    private void viewSelectedCustomerDetails() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view details.", "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) customersTableModel.getValueAt(selectedRow, 0); 
        try {
            // Corrected to use getCustomerByID (capital ID)
            Customer selectedCustomer = fbs.getCustomerByID(customerId); 
            if (selectedCustomer == null) {
                 JOptionPane.showMessageDialog(MainWindow.this, "Customer details could not be retrieved.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            new CustomerDetailsWindow(MainWindow.this, selectedCustomer);
        } catch (FlightBookingSystemException ex) { // Catch the specific exception
            JOptionPane.showMessageDialog(MainWindow.this, "Error viewing customer details: " + ex.getMessage(), "Customer Details Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(MainWindow.this, "An unexpected error occurred: " + ex.getMessage(), "Customer Details Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No customer selected for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String status = (String) customersTableModel.getValueAt(selectedRow, 7);
        if (status.equals("Removed")) {
            JOptionPane.showMessageDialog(this, "This customer has already been removed.", "Already Removed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int customerIdToDelete = (int) customersTableModel.getValueAt(selectedRow, 0); 
        String customerName = (String) customersTableModel.getValueAt(selectedRow, 1); 

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete customer " + customerName + " (ID: " + customerIdToDelete + ")?\n" +
            "This will mark the customer as removed and they will no longer be visible in active lists.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = fbs.removeCustomerById(customerIdToDelete); 

                if (removed) {
                    FlightBookingSystemData.store(fbs);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Customer " + customerName + " (ID: " + customerIdToDelete + ") has been successfully marked as removed.", 
                        "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    displayCustomers();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Customer with ID " + customerIdToDelete + " was not found or could not be removed.", 
                        "Deletion Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FlightBookingSystemException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during deletion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}