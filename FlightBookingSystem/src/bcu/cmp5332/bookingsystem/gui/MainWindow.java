package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

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
    
    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;

    private FlightBookingSystem fbs;
    
    // Toggle for seat display mode
    private JToggleButton seatDisplayToggle;
    private boolean showAvailable = true; // true = show available, false = show occupied
    
    // Current flights table for refreshing
    private JTable currentFlightsTable;

    public MainWindow(FlightBookingSystem fbs) {
        initialize();
        this.fbs = fbs;
    }
    
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Flight Booking Management System");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //adding adminMenu menu and menu items
        adminMenu = new JMenu("Admin");
        menuBar.add(adminMenu);

        adminExit = new JMenuItem("Exit");
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        // adding Flights menu and menu items
        flightsMenu = new JMenu("Flights");
        menuBar.add(flightsMenu);

        flightsView = new JMenuItem("View");
        flightsAdd = new JMenuItem("Add");
        flightsDel = new JMenuItem("Delete");
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.add(flightsDel);
        // adding action listener for Flights menu items
        for (int i = 0; i < flightsMenu.getItemCount(); i++) {
            flightsMenu.getItem(i).addActionListener(this);
        }
        
        // adding Bookings menu and menu items
        bookingsMenu = new JMenu("Bookings");
        menuBar.add(bookingsMenu);
        
        bookingsIssue = new JMenuItem("Issue");
        bookingsUpdate = new JMenuItem("Update");
        bookingsCancel = new JMenuItem("Cancel");
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.add(bookingsCancel);
        // adding action listener for Bookings menu items
        for (int i = 0; i < bookingsMenu.getItemCount(); i++) {
            bookingsMenu.getItem(i).addActionListener(this);
        }

        // adding Customers menu and menu items
        customersMenu = new JMenu("Customers");
        menuBar.add(customersMenu);

        custView = new JMenuItem("View");
        custAdd = new JMenuItem("Add");
        custDel = new JMenuItem("Delete");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.add(custDel);
        // adding action listener for Customers menu items
        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);

        setSize(1200, 600); // Increased window size for better display

        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        /* Uncomment the following line to not terminate the console app when the window is closed */
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);        
    }	

    /* Uncomment the following code to run the GUI version directly from the IDE */
    //    public static void main(String[] args) throws IOException, FlightBookingSystemException {
    //        FlightBookingSystem fbs = FlightBookingSystemData.load();
    //        new MainWindow(fbs);			
    //    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == adminExit) {
            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        } else if (ae.getSource() == flightsView) {
            displayFlights();
            
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
            
        } else if (ae.getSource() == flightsDel) {
            // TODO: Implement flight deletion functionality
            
        } else if (ae.getSource() == bookingsIssue) {
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
            
        } else if (ae.getSource() == seatDisplayToggle) {
            // Toggle between showing available and occupied seats
            showAvailable = !showAvailable;
            updateSeatDisplayToggle();
            refreshFlightsDisplay();
        }
    }

    private void updateSeatDisplayToggle() {
        if (showAvailable) {
            seatDisplayToggle.setText("Showing: Available Seats");
            seatDisplayToggle.setBackground(new Color(200, 255, 200));
            seatDisplayToggle.setToolTipText("Click to show occupied seats instead");
        } else {
            seatDisplayToggle.setText("Showing: Occupied Seats");
            seatDisplayToggle.setBackground(new Color(255, 220, 200));
            seatDisplayToggle.setToolTipText("Click to show available seats instead");
        }
    }

    private void refreshFlightsDisplay() {
        if (currentFlightsTable != null) {
            displayFlights();
        }
    }

    public void displayFlights() {
        List<Flight> flightsList = fbs.getFlights();
        
        // Dynamic headers based on display mode
        String seatDisplayText = showAvailable ? "Available" : "Occupied";
        String[] columns = new String[]{
            "Flight No", "Origin", "Destination", "Date", "Type", "Total Seats", 
            "Economy (" + seatDisplayText + ")", "Premium Eco (" + seatDisplayText + ")", 
            "Business (" + seatDisplayText + ")", "First (" + seatDisplayText + ")"
        };

        Object[][] data = new Object[flightsList.size()][10];
        for (int i = 0; i < flightsList.size(); i++) {
            Flight flight = flightsList.get(i);
            
            data[i][0] = flight.getFlightNumber();
            data[i][1] = flight.getOrigin();
            data[i][2] = flight.getDestination();
            data[i][3] = flight.getDepartureDate() != null ? 
                        flight.getDepartureDate().toString() : "TBD";
            data[i][4] = flight.getFlightType() == FlightType.COMMERCIAL ? "Commercial" : "Budget";
            data[i][5] = flight.getCapacity();
            
            // Initialize all class columns
            data[i][6] = "-"; // Economy
            data[i][7] = "-"; // Premium Economy
            data[i][8] = "-"; // Business
            data[i][9] = "-"; // First
            
            if (flight.getFlightType() == FlightType.COMMERCIAL && flight.getClassCapacities() != null) {
                Map<CommercialClassType, Integer> classCapacities = flight.getClassCapacities();
                Map<CommercialClassType, Integer> occupiedSeats = flight.getOccupiedSeatsMap();
                
                for (Map.Entry<CommercialClassType, Integer> entry : classCapacities.entrySet()) {
                    CommercialClassType classType = entry.getKey();
                    int capacity = entry.getValue();
                    int occupied = occupiedSeats.getOrDefault(classType, 0);
                    int available = capacity - occupied;
                    
                    // Choose display format based on toggle
                    String displayText;
                    if (showAvailable) {
                        displayText = available + "/" + capacity;
                    } else {
                        displayText = occupied + "/" + capacity;
                    }
                    
                    switch (classType) {
                        case ECONOMY:
                            data[i][6] = displayText;
                            break;
                        case PREMIUM_ECONOMY:
                            data[i][7] = displayText;
                            break;
                        case BUSINESS:
                            data[i][8] = displayText;
                            break;
                        case FIRST:
                            data[i][9] = displayText;
                            break;
                    }
                }
            } else if (flight.getFlightType() == FlightType.BUDGET) {
                // Budget flight - only economy
                int occupied = 0;
                if (flight.getOccupiedSeatsMap() != null) {
                    occupied = flight.getOccupiedSeatsMap().getOrDefault(CommercialClassType.ECONOMY, 0);
                }
                int available = flight.getCapacity() - occupied;
                
                if (showAvailable) {
                    data[i][6] = available + "/" + flight.getCapacity();
                } else {
                    data[i][6] = occupied + "/" + flight.getCapacity();
                }
            }
        }

        JTable table = new JTable(data, columns);
        currentFlightsTable = table;
        
        // Enhanced table styling
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        // Header styling
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(245, 245, 250));
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        table.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);  // Flight No
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Origin
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Destination
        table.getColumnModel().getColumn(3).setPreferredWidth(90);  // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(5).setPreferredWidth(70);  // Total Seats
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Economy
        table.getColumnModel().getColumn(7).setPreferredWidth(120); // Premium Eco
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Business
        table.getColumnModel().getColumn(9).setPreferredWidth(80);  // First
        
        // Custom cell renderer for seat availability/occupancy with color coding
        SeatDisplayRenderer seatRenderer = new SeatDisplayRenderer();
        table.getColumnModel().getColumn(6).setCellRenderer(seatRenderer); // Economy
        table.getColumnModel().getColumn(7).setCellRenderer(seatRenderer); // Premium Eco
        table.getColumnModel().getColumn(8).setCellRenderer(seatRenderer); // Business
        table.getColumnModel().getColumn(9).setCellRenderer(seatRenderer); // First
        
        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Flight No
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Type
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Total Seats
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(245, 245, 250));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Initialize toggle button
        seatDisplayToggle = new JToggleButton();
        seatDisplayToggle.setFont(new Font("SansSerif", Font.BOLD, 12));
        seatDisplayToggle.setFocusPainted(false);
        seatDisplayToggle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        seatDisplayToggle.addActionListener(this);
        updateSeatDisplayToggle();
        
        // Add info label
        JLabel infoLabel = new JLabel("Seat Information Format: " + 
            (showAvailable ? "Available" : "Occupied") + "/Total");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        
        controlPanel.add(seatDisplayToggle);
        controlPanel.add(new JLabel("     ")); // Spacer
        controlPanel.add(infoLabel);
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        this.getContentPane().removeAll();
        this.getContentPane().add(mainPanel);
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Custom cell renderer for seat display with color coding
     */
    private class SeatDisplayRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setHorizontalAlignment(JLabel.CENTER);
            
            if (value != null && !value.equals("-")) {
                String text = value.toString();
                if (text.contains("/")) {
                    String[] parts = text.split("/");
                    if (parts.length == 2) {
                        try {
                            int displayValue = Integer.parseInt(parts[0]);
                            int total = Integer.parseInt(parts[1]);
                            
                            // Color coding based on display mode
                            if (showAvailable) {
                                // Available seats coloring
                                if (displayValue == 0) {
                                    setBackground(isSelected ? new Color(255, 200, 200) : new Color(255, 230, 230));
                                    setForeground(new Color(150, 0, 0));
                                } else if (displayValue < total * 0.3) {
                                    setBackground(isSelected ? new Color(255, 220, 200) : new Color(255, 245, 230));
                                    setForeground(new Color(200, 100, 0));
                                } else {
                                    setBackground(isSelected ? new Color(200, 255, 200) : new Color(240, 255, 240));
                                    setForeground(new Color(0, 120, 0));
                                }
                            } else {
                                // Occupied seats coloring (reverse logic)
                                if (displayValue == total) {
                                    setBackground(isSelected ? new Color(255, 200, 200) : new Color(255, 230, 230));
                                    setForeground(new Color(150, 0, 0));
                                } else if (displayValue > total * 0.7) {
                                    setBackground(isSelected ? new Color(255, 220, 200) : new Color(255, 245, 230));
                                    setForeground(new Color(200, 100, 0));
                                } else {
                                    setBackground(isSelected ? new Color(200, 255, 200) : new Color(240, 255, 240));
                                    setForeground(new Color(0, 120, 0));
                                }
                            }
                        } catch (NumberFormatException e) {
                            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                            setForeground(table.getForeground());
                        }
                    }
                }
            } else {
                setBackground(isSelected ? table.getSelectionBackground() : new Color(250, 250, 250));
                setForeground(new Color(150, 150, 150));
            }
            
            return this;
        }
    }
	
}