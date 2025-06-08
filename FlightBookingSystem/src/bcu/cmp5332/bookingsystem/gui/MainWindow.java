package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

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

    private JMenuItem custViewActive;
    private JMenuItem custViewAll;
    private JMenuItem custAdd;

    private FlightBookingSystem fbs;
    private JPanel contentPanel;
    
    // View windows
    private ViewFlightWindow viewFlightWindow;
    private ViewCustomerWindow viewCustomerWindow;

    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
        showFlightView(false); // Default view on startup - show active flights
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
        setSize(1200, 600);

        setupMenuBar();
        setupContentPanel();
        
        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Admin Menu
        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);
        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        // Flights Menu
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
        
        // Bookings Menu
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

        // Customers Menu
        customersMenu = new JMenu("Customers");
        menuBar.add(customersMenu);
        custViewActive = new JMenuItem("View Active Customers Only");
        custViewAll = new JMenuItem("View All Customers (Including Deleted)");
        custAdd = new JMenuItem("Add Customer");

        customersMenu.add(custViewActive);
        customersMenu.add(custViewAll);
        customersMenu.addSeparator();
        customersMenu.add(custAdd);
        
        custViewActive.addActionListener(this);
        custViewAll.addActionListener(this);
        custAdd.addActionListener(this);
    }

    private void setupContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.getContentPane().add(contentPanel);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == adminExit) {
            exitApplication();
        } 
        // Flight Menu Actions
        else if (ae.getSource() == flightsViewActive) {
            showFlightView(false);
        } else if (ae.getSource() == flightsViewAll) {
            showFlightView(true);
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
        } 
        // Customer Menu Actions
        else if (ae.getSource() == custViewActive) {
            showCustomerView(false);
        } else if (ae.getSource() == custViewAll) {
            showCustomerView(true);
        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);
        }
        // Booking Menu Actions
        else if (ae.getSource() == bookingsIssue) {
            // TODO: Implement booking issue functionality
            JOptionPane.showMessageDialog(this, "Booking functionality not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource() == bookingsUpdate) {
            // TODO: Implement booking update functionality
            JOptionPane.showMessageDialog(this, "Booking functionality not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource() == bookingsCancel) {
            // TODO: Implement booking cancellation functionality
            JOptionPane.showMessageDialog(this, "Booking functionality not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showFlightView(boolean showAll) {
        if (viewCustomerWindow != null) {
            viewCustomerWindow.cleanup();
            viewCustomerWindow = null;
        }
        
        if (viewFlightWindow == null) {
            viewFlightWindow = new ViewFlightWindow(this, fbs);
        }
        
        contentPanel.removeAll();
        contentPanel.add(viewFlightWindow.getPanel(), BorderLayout.CENTER);
        viewFlightWindow.displayFlights(showAll);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCustomerView(boolean showAll) {
        if (viewFlightWindow != null) {
            viewFlightWindow.cleanup();
            viewFlightWindow = null;
        }
        
        if (viewCustomerWindow == null) {
            viewCustomerWindow = new ViewCustomerWindow(this, fbs);
        }
        
        contentPanel.removeAll();
        contentPanel.add(viewCustomerWindow.getPanel(), BorderLayout.CENTER);
        viewCustomerWindow.displayCustomers(showAll);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void exitApplication() {
        try {
            FlightBookingSystemData.store(fbs);
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Exit", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }

    public void refreshCurrentView() {
        if (viewFlightWindow != null && viewFlightWindow.isVisible()) {
            viewFlightWindow.refreshView();
        }
        if (viewCustomerWindow != null && viewCustomerWindow.isVisible()) {
            viewCustomerWindow.refreshView();
        }
    }

 // In MainWindow.java

    public void displayFlights() {
        // Check if the flight view is currently displayed and refresh it
        if (viewFlightWindow != null && contentPanel.isAncestorOf(viewFlightWindow.getPanel())) {
            viewFlightWindow.displayFlights(false); // Assuming you want to display active flights after adding
            // If you want to retain the current 'view all' or 'view active' state,
            // you would need to store that state in MainWindow or viewFlightWindow
        } else {
            // If the flight view is not currently visible, make it visible and display active flights
            showFlightView(false);
        }
    }

    public void displayCustomers() {
        // Check if the customer view is currently displayed and refresh it
        if (viewCustomerWindow != null && contentPanel.isAncestorOf(viewCustomerWindow.getPanel())) {
            viewCustomerWindow.displayCustomers(false); // Assuming you want to display active customers after adding
            // Similar to flights, if you want to retain the current 'view all' or 'view active' state
        } else {
            // If the customer view is not currently visible, make it visible and display active customers
            showCustomerView(false);
        }
    }
}