package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightType; // Import FlightType

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate; // Needed for getDynamicPrice

public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;

    private JMenuItem adminExit;

    private JMenuItem flightsView;
    private JMenuItem flightsAdd;
    private JMenuItem flightsDel;
    private JMenuItem flightsSearch;
    private JMenuItem flightsShow; // Added Show Flight

    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;
    private JMenuItem bookingsSearch;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;
    private JMenuItem custSearch;
    private JMenuItem custShow; // Added Show Customer

    private FlightBookingSystem fbs;

    private JPanel mainContentPanel;
    private JLabel statusLabel;

    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Flight Booking Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        adminMenu = new JMenu("Admin");
        adminMenu.setForeground(new Color(0, 102, 153));
        menuBar.add(adminMenu);
        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        flightsMenu = new JMenu("Flights");
        flightsMenu.setForeground(new Color(0, 102, 153));
        menuBar.add(flightsMenu);
        flightsView = new JMenuItem("View All Flights");
        flightsAdd = new JMenuItem("Add New Flight");
        flightsDel = new JMenuItem("Remove Flight");
        flightsShow = new JMenuItem("Show Flight Details"); // New
        flightsSearch = new JMenuItem("Search Flights");
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.add(flightsDel);
        flightsMenu.addSeparator();
        flightsMenu.add(flightsShow); // Added
        flightsMenu.add(flightsSearch);
        flightsView.addActionListener(this);
        flightsAdd.addActionListener(this);
        flightsDel.addActionListener(this);
        flightsShow.addActionListener(this); // Added listener
        flightsSearch.addActionListener(this);

        bookingsMenu = new JMenu("Bookings");
        bookingsMenu.setForeground(new Color(0, 102, 153));
        menuBar.add(bookingsMenu);
        bookingsIssue = new JMenuItem("Issue New Booking");
        bookingsUpdate = new JMenuItem("Edit Booking");
        bookingsCancel = new JMenuItem("Cancel Booking");
        bookingsSearch = new JMenuItem("Search Bookings");
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);
        bookingsMenu.addSeparator();
        bookingsMenu.add(bookingsSearch);
        bookingsIssue.addActionListener(this);
        bookingsUpdate.addActionListener(this);
        bookingsCancel.addActionListener(this);
        bookingsSearch.addActionListener(this);

        customersMenu = new JMenu("Customers");
        customersMenu.setForeground(new Color(0, 102, 153));
        menuBar.add(customersMenu);
        custView = new JMenuItem("View All Customers");
        custAdd = new JMenuItem("Add New Customer");
        custDel = new JMenuItem("Remove Customer");
        custShow = new JMenuItem("Show Customer Details"); // New
        custSearch = new JMenuItem("Search Customers");
        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);
        customersMenu.addSeparator();
        customersMenu.add(custShow); // Added
        customersMenu.add(custSearch);
        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);
        custShow.addActionListener(this); // Added listener
        custSearch.addActionListener(this);

        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainContentPanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("<html><h1 style='color:#006699;'>Welcome to Flight Booking Management System!</h1>" +
                                        "<p>Use the menus above to manage flights, bookings, and customers.</p></html>", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mainContentPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(220, 230, 240));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("System Ready.");
        statusLabel.setForeground(new Color(50, 50, 50));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        statusLabel.setText("");

        if (ae.getSource() == adminExit) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit? All unsaved data will be lost.",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    FlightBookingSystemData.store(fbs);
                    JOptionPane.showMessageDialog(this, "Data saved successfully.", "Save Status", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        }
        else if (ae.getSource() == flightsView) {
            displayFlights();
            statusLabel.setText("Displaying all flights.");
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
            statusLabel.setText("Opening 'Add New Flight' window.");
        } else if (ae.getSource() == flightsDel) {
            new RemoveFlightWindow(this);
            statusLabel.setText("Opening 'Remove Flight' window.");
        } else if (ae.getSource() == flightsShow) { // New
            new ShowFlightWindow(this);
            statusLabel.setText("Opening 'Show Flight Details' window.");
        } else if (ae.getSource() == flightsSearch) {
            new SearchFlightsWindow(this);
            statusLabel.setText("Opening 'Search Flights' window.");
        }
        else if (ae.getSource() == bookingsIssue) {
            new IssueBookingWindow(this);
            statusLabel.setText("Opening 'Issue New Booking' window.");
        } else if (ae.getSource() == bookingsUpdate) {
            new EditBookingWindow(this);
            statusLabel.setText("Opening 'Edit Booking' window.");
        } else if (ae.getSource() == bookingsCancel) {
            new CancelBookingWindow(this);
            statusLabel.setText("Opening 'Cancel Booking' window.");
        } else if (ae.getSource() == bookingsSearch) {
            new SearchBookingsWindow();
            statusLabel.setText("Opening 'Search Bookings' window.");
        }
        else if (ae.getSource() == custView) {
            displayCustomers();
            statusLabel.setText("Displaying all customers.");
        } else if (ae.getSource() == custAdd) {
            new AddCustomerWindow(this);
            statusLabel.setText("Opening 'Add New Customer' window.");
        } else if (ae.getSource() == custDel) {
            new RemoveCustomerWindow(this);
            statusLabel.setText("Opening 'Remove Customer' window.");
        } else if (ae.getSource() == custShow) { // New
            new ShowCustomerWindow();
            statusLabel.setText("Opening 'Show Customer Details' window.");
        } else if (ae.getSource() == custSearch) {
            new SearchCustomersWindow(this);
            statusLabel.setText("Opening 'Search Customers' window.");
        }
    }

    public void displayFlights() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());

        List<Flight> activeFlights = fbs.getFlights().stream()
                                    .filter(flight -> !flight.isDeleted() && !flight.hasDeparted(fbs.getSystemDate()))
                                    .toList();

        // Updated columns to show total capacity and a summary of available seats
        // The detailed class-wise breakdown will be in 'Show Flight Details'
        String[] columns = new String[]{"ID", "Flight No", "Origin", "Destination", "Departure Date", "Flight Type", "Base Economy Price", "Total Capacity", "Total Seats Available"};
        Object[][] data = new Object[activeFlights.size()][columns.length];
        for (int i = 0; i < activeFlights.size(); i++) {
            Flight flight = activeFlights.get(i);
            int totalSeatsAvailable = 0;
            for (CommercialClassType classType : flight.getAvailableClasses()) {
                totalSeatsAvailable += flight.getRemainingSeatsForClass(classType);
            }

            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = flight.getFlightType().name(); // Using .name() as FlightType does not have getDisplayName()
            data[i][6] = String.format("£%.2f", flight.getEconomyPrice()); // Base Economy Price
            data[i][7] = flight.getCapacity();
            data[i][8] = totalSeatsAvailable; // Sum of available seats across all classes
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("All Active Flights", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void displayCustomers() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());

        List<Customer> activeCustomers = fbs.getCustomers().stream()
                                        .filter(customer -> !customer.isDeleted())
                                        .toList();

        String[] columns = new String[]{"ID", "Name", "Phone", "Email", "Age", "Gender", "Preferred Meal"};
        Object[][] data = new Object[activeCustomers.size()][columns.length];
        for (int i = 0; i < activeCustomers.size(); i++) {
            Customer customer = activeCustomers.get(i);
            data[i][0] = customer.getId();
            data[i][1] = customer.getName();
            data[i][2] = customer.getPhone();
            data[i][3] = customer.getEmail();
            data[i][4] = customer.getAge();
            data[i][5] = customer.getGender();
            data[i][6] = customer.getPreferredMealType().getDisplayName();
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(table);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("All Customers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainContentPanel.add(titleLabel, BorderLayout.NORTH);

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}