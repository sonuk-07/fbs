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
    private JMenu mealsMenu;    

    private JMenuItem adminExit;

    private JMenuItem flightsViewActive;
    private JMenuItem flightsViewAll;
    private JMenuItem flightsAdd;

    private JMenuItem bookingsIssue;
    private JMenuItem bookingsViewAll;

    private JMenuItem custViewActive;
    private JMenuItem custViewAll;
    private JMenuItem custAdd;

    private JMenuItem mealsView;
    private JMenuItem mealsAdd;        

    private FlightBookingSystem fbs;
    private JPanel contentPanel;
    
    private ViewFlightWindow viewFlightWindow;
    private ViewCustomerWindow viewCustomerWindow;
    private ViewMealWindow viewMealWindow;    
    private ViewBookingWindow viewBookingWindow;

    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
        showFlightView(false);
    }
    
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set LookAndFeel: " + ex.getMessage());
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
        
        bookingsViewAll = new JMenuItem("View All Bookings"); 
        bookingsIssue = new JMenuItem("Issue New Booking");
        
        bookingsMenu.add(bookingsViewAll); 
        bookingsMenu.addSeparator();
        bookingsMenu.add(bookingsIssue);
        
        bookingsViewAll.addActionListener(this);
        bookingsIssue.addActionListener(this);

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

        mealsMenu = new JMenu("Meals");
        menuBar.add(mealsMenu);
        
        mealsView = new JMenuItem("View All Meals");    
        mealsAdd = new JMenuItem("Add Meal");
        
        mealsMenu.add(mealsView);
        mealsMenu.addSeparator();
        mealsMenu.add(mealsAdd);
        
        mealsView.addActionListener(this);
        mealsAdd.addActionListener(this);
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
        else if (ae.getSource() == flightsViewActive) {
            showFlightView(false);
        } else if (ae.getSource() == flightsViewAll) {
            showFlightView(true);
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
        }    
        else if (ae.getSource() == custViewActive) {
            showCustomerView(false);
        } else if (ae.getSource() == custViewAll) {
            showCustomerView(true);
        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);
        }
        else if (ae.getSource() == mealsView) {
            showMealView();
        } else if (ae.getSource() == mealsAdd) {
            new AddMealWindow(this);
        }
        else if (ae.getSource() == bookingsViewAll) {
            showBookingView();
        }
        else if (ae.getSource() == bookingsIssue) {
            new IssueBookingWindow(this);
        }
    }

    private void showFlightView(boolean showAll) {
        if (viewCustomerWindow != null) {
            viewCustomerWindow.cleanup();
            viewCustomerWindow = null;
        }
        if (viewMealWindow != null) {    
            viewMealWindow.cleanup();
            viewMealWindow = null;
        }
        if (viewBookingWindow != null) {
            viewBookingWindow.cleanup();
            viewBookingWindow = null;
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
        if (viewMealWindow != null) {    
            viewMealWindow.cleanup();
            viewMealWindow = null;
        }
        if (viewBookingWindow != null) {
            viewBookingWindow.cleanup();
            viewBookingWindow = null;
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

    private void showMealView() {
        if (viewFlightWindow != null) {
            viewFlightWindow.cleanup();
            viewFlightWindow = null;
        }
        if (viewCustomerWindow != null) {
            viewCustomerWindow.cleanup();
            viewCustomerWindow = null;
        }
        if (viewBookingWindow != null) {
            viewBookingWindow.cleanup();
            viewBookingWindow = null;
        }
        
        if (viewMealWindow == null) {
            viewMealWindow = new ViewMealWindow(this, fbs);
        }
        
        contentPanel.removeAll();
        contentPanel.add(viewMealWindow.getPanel(), BorderLayout.CENTER);
        viewMealWindow.displayMeals();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showBookingView() {
        if (viewFlightWindow != null) {
            viewFlightWindow.cleanup();
            viewFlightWindow = null;
        }
        if (viewCustomerWindow != null) {
            viewCustomerWindow.cleanup();
            viewCustomerWindow = null;
        }
        if (viewMealWindow != null) {
            viewMealWindow.cleanup();
            viewMealWindow = null;
        }

        if (viewBookingWindow == null) {
            viewBookingWindow = new ViewBookingWindow(this, fbs);
        }
        
        contentPanel.removeAll();
        contentPanel.add(viewBookingWindow.getPanel(), BorderLayout.CENTER);
        viewBookingWindow.displayBookings(); 
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
        if (viewFlightWindow != null && contentPanel.isAncestorOf(viewFlightWindow.getPanel())) {
            viewFlightWindow.refreshView();
        } else if (viewCustomerWindow != null && contentPanel.isAncestorOf(viewCustomerWindow.getPanel())) {
            viewCustomerWindow.refreshView();
        } else if (viewMealWindow != null && contentPanel.isAncestorOf(viewMealWindow.getPanel())) {
            viewMealWindow.refreshView();
        } else if (viewBookingWindow != null && contentPanel.isAncestorOf(viewBookingWindow.getPanel())) {
            viewBookingWindow.refreshView();
        }
    }

    public void displayFlights() {
        if (viewFlightWindow != null && contentPanel.isAncestorOf(viewFlightWindow.getPanel())) {
            viewFlightWindow.displayFlights(false);    
        } else {
            showFlightView(false);
        }
    }

    public void displayCustomers() {
        if (viewCustomerWindow != null && contentPanel.isAncestorOf(viewCustomerWindow.getPanel())) {
            viewCustomerWindow.displayCustomers(false);    
        } else {
            showCustomerView(false);
        }
    }

    public void displayMeals() {
        if (viewMealWindow != null && contentPanel.isAncestorOf(viewMealWindow.getPanel())) {
            viewMealWindow.displayMeals();
        } else {
            showMealView();
        }
    }

    public void displayBookings() { 
        if (viewBookingWindow != null && contentPanel.isAncestorOf(viewBookingWindow.getPanel())) {
            viewBookingWindow.displayBookings();
        } else {
            showBookingView();
        }
    }
}