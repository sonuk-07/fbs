package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem; // Keep this import, it's used internally
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException; 
import java.time.format.DateTimeFormatter; // Might not be directly used in this class, but good to keep if needed later
import java.util.List;

public class IssueBookingWindow extends JFrame {

    private MainWindow mainWindow; // Now only storing MainWindow
    // No longer need a direct 'private FlightBookingSystem fbs;' field here

    // UI Components for selections
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Flight> outboundFlightComboBox;
    private JComboBox<Flight> returnFlightComboBox; 
    private JComboBox<CommercialClassType> classTypeComboBox;
    private JComboBox<Meal> mealComboBox; 

    private JButton bookButton;
    private JButton cancelButton;

    // === MODIFIED CONSTRUCTOR ===
    public IssueBookingWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initialize();
    }
    // ============================

    private void initialize() {
        setTitle("Issue New Booking");
        setSize(550, 400); 
        setLocationRelativeTo(mainWindow);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Select Customer:"), gbc);
        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        loadCustomers(); 
        inputPanel.add(customerComboBox, gbc);

        // Outbound Flight Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Select Outbound Flight:"), gbc);
        gbc.gridx = 1;
        outboundFlightComboBox = new JComboBox<>();
        loadFlights(outboundFlightComboBox); 
        inputPanel.add(outboundFlightComboBox, gbc);

        // Return Flight Selection (Optional)
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Select Return Flight (Optional):"), gbc);
        gbc.gridx = 1;
        returnFlightComboBox = new JComboBox<>();
        returnFlightComboBox.addItem(null); 
        loadFlights(returnFlightComboBox); 
        inputPanel.add(returnFlightComboBox, gbc);

        // Class Type Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Select Class Type:"), gbc);
        gbc.gridx = 1;
        classTypeComboBox = new JComboBox<>(CommercialClassType.values()); 
        inputPanel.add(classTypeComboBox, gbc);

        // Meal Selection (Optional)
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Select Meal (Optional):"), gbc);
        gbc.gridx = 1;
        mealComboBox = new JComboBox<>();
        mealComboBox.addItem(null); 
        loadMeals(); 
        inputPanel.add(mealComboBox, gbc);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bookButton = new JButton("Book");
        bookButton.addActionListener(new BookButtonListener());
        buttonPanel.add(bookButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose()); 
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    /**
     * Populates the customer combo box with available customers from the system.
     * Uses mainWindow.getFlightBookingSystem() to get FBS.
     */
    private void loadCustomers() {
        customerComboBox.removeAllItems();
        // === MODIFIED LINE ===
        mainWindow.getFlightBookingSystem().getCustomers().stream()
           .filter(c -> !c.isDeleted()) 
           .forEach(customerComboBox::addItem);
        // =====================
        if (customerComboBox.getItemCount() > 0) {
            customerComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Populates the flight combo boxes with available flights.
     * Uses mainWindow.getFlightBookingSystem() to get FBS.
     * @param comboBox The JComboBox to populate.
     */
    private void loadFlights(JComboBox<Flight> comboBox) {
        if (comboBox != returnFlightComboBox) { 
             comboBox.removeAllItems(); 
        } else {
             comboBox.removeAllItems();
             comboBox.addItem(null); 
        }

        // === MODIFIED LINE ===
        mainWindow.getFlightBookingSystem().getFlights().stream()
           .filter(f -> !f.isDeleted()) 
           .forEach(comboBox::addItem);
        // =====================
        
        if (comboBox.getItemCount() > 0 && comboBox.getSelectedItem() == null) {
            comboBox.setSelectedIndex(0);
        }
    }

    /**
     * Populates the meal combo box with available meals.
     * Uses mainWindow.getFlightBookingSystem() to get FBS.
     */
    private void loadMeals() {
        mealComboBox.removeAllItems();
        mealComboBox.addItem(null); 
        // === MODIFIED LINE ===
        mainWindow.getFlightBookingSystem().getMeals().forEach(mealComboBox::addItem);
        // =====================
        if (mealComboBox.getItemCount() > 0) {
            mealComboBox.setSelectedIndex(0);
        }
    }

    /**
     * ActionListener for the "Book" button.
     * Uses mainWindow.getFlightBookingSystem() to get FBS.
     */
    private class BookButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
            Flight selectedOutboundFlight = (Flight) outboundFlightComboBox.getSelectedItem();
            Flight selectedReturnFlight = (Flight) returnFlightComboBox.getSelectedItem(); 
            CommercialClassType selectedClassType = (CommercialClassType) classTypeComboBox.getSelectedItem();
            Meal selectedMeal = (Meal) mealComboBox.getSelectedItem(); 

            // --- Basic Input Validation (Client-Side) ---
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Please select a customer.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedOutboundFlight == null) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Please select an outbound flight.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedClassType == null) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Please select a class type.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // === MODIFIED LINE ===
                mainWindow.getFlightBookingSystem().addBooking(selectedCustomer, selectedOutboundFlight, selectedReturnFlight, selectedClassType, selectedMeal);
                FlightBookingSystemData.store(mainWindow.getFlightBookingSystem()); // Access FBS via mainWindow
                // =====================

                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Booking successfully issued!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                if (mainWindow != null) {
                    mainWindow.displayBookings(); // Changed to displayBookings to show new booking
                }
                dispose(); 
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Booking Failed: " + ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "Error saving data after booking: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(IssueBookingWindow.this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}