package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; // Import FlightBookingSystemData

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException; // Keep this import for IOException
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * GUI window for issuing new bookings in the flight booking system.
 * Allows users to select customers, flights, classes, and meals to create bookings.
 */
public class IssueBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;

    // Customer selection
    private JComboBox<Customer> customerComboBox;
    private JButton loadCustomersButton;

    // Flight selection
    private JComboBox<Flight> outboundFlightComboBox;
    private JComboBox<Flight> FlightreturnFlightComboBox;
    private JButton loadFlightsButton;
    private JCheckBox returnFlightCheckBox;

    // Class selection
    private JComboBox<CommercialClassType> classComboBox;

    // Meal selection
    private JComboBox<Meal> mealComboBox;
    private JButton loadMealsButton;
    private JLabel mealFilterLabel;

    // Price display
    private JLabel outboundPriceLabel;
    private JLabel returnPriceLabel;
    private JLabel mealPriceLabel;
    private JLabel totalPriceLabel;

    // Action buttons
    private JButton calculatePriceButton;
    private JButton issueBookingButton;
    private JButton cancelButton;

    public IssueBookingWindow(MainWindow mw) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        initialize();
    }

    private void initialize() {
        setTitle("Issue New Booking");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mw);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Customer selection section
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Customer:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(250, 25));
        formPanel.add(customerComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadCustomersButton = new JButton("Load Customers");
        loadCustomersButton.addActionListener(this);
        formPanel.add(loadCustomersButton, gbc);

        // Outbound flight selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Outbound Flight:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        outboundFlightComboBox = new JComboBox<>();
        outboundFlightComboBox.setPreferredSize(new Dimension(250, 25));
        outboundFlightComboBox.addActionListener(this);
        formPanel.add(outboundFlightComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadFlightsButton = new JButton("Load Flights");
        loadFlightsButton.addActionListener(this);
        formPanel.add(loadFlightsButton, gbc);

        // Return flight checkbox
        gbc.gridx = 0; gbc.gridy = 2;
        returnFlightCheckBox = new JCheckBox("Include Return Flight");
        returnFlightCheckBox.addActionListener(this);
        formPanel.add(returnFlightCheckBox, gbc);

        // Return flight selection
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Return Flight:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        FlightreturnFlightComboBox = new JComboBox<>();
        FlightreturnFlightComboBox.setPreferredSize(new Dimension(250, 25));
        FlightreturnFlightComboBox.setEnabled(false);
        FlightreturnFlightComboBox.addActionListener(this);
        formPanel.add(FlightreturnFlightComboBox, gbc);

        // Class selection
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Class:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        classComboBox = new JComboBox<>(CommercialClassType.values());
        classComboBox.addActionListener(this);
        formPanel.add(classComboBox, gbc);

        // Meal selection
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Meal:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mealComboBox = new JComboBox<>();
        mealComboBox.setPreferredSize(new Dimension(250, 25));
        mealComboBox.addActionListener(this);
        formPanel.add(mealComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadMealsButton = new JButton("Load Meals");
        loadMealsButton.addActionListener(this);
        formPanel.add(loadMealsButton, gbc);

        // Meal filter information
        gbc.gridx = 1; gbc.gridy = 6;
        mealFilterLabel = new JLabel("(Meals filtered by customer preference)");
        mealFilterLabel.setFont(mealFilterLabel.getFont().deriveFont(Font.ITALIC, 10f));
        mealFilterLabel.setForeground(Color.GRAY);
        formPanel.add(mealFilterLabel, gbc);

        // Price display section
        JPanel pricePanel = new JPanel(new GridBagLayout());
        pricePanel.setBorder(BorderFactory.createTitledBorder("Price Breakdown"));

        gbc.gridx = 0; gbc.gridy = 0;
        pricePanel.add(new JLabel("Outbound Flight Price:"), gbc);
        gbc.gridx = 1;
        outboundPriceLabel = new JLabel("£0.00");
        pricePanel.add(outboundPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pricePanel.add(new JLabel("Return Flight Price:"), gbc);
        gbc.gridx = 1;
        returnPriceLabel = new JLabel("£0.00");
        pricePanel.add(returnPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pricePanel.add(new JLabel("Meal Price:"), gbc);
        gbc.gridx = 1;
        mealPriceLabel = new JLabel("£0.00");
        pricePanel.add(mealPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalLabel = new JLabel("Total Price:");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        pricePanel.add(totalLabel, gbc);
        gbc.gridx = 1;
        totalPriceLabel = new JLabel("£0.00");
        totalPriceLabel.setFont(totalPriceLabel.getFont().deriveFont(Font.BOLD));
        pricePanel.add(totalPriceLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        calculatePriceButton = new JButton("Calculate Price");
        calculatePriceButton.addActionListener(this);

        issueBookingButton = new JButton("Issue Booking");
        issueBookingButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(calculatePriceButton);
        buttonPanel.add(issueBookingButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(pricePanel, BorderLayout.SOUTH);

        // Add main panel and button panel to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        loadCustomers();
        loadFlights();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadCustomersButton) {
            loadCustomers();
        } else if (e.getSource() == loadFlightsButton) {
            loadFlights();
        } else if (e.getSource() == loadMealsButton || e.getSource() == customerComboBox) {
            loadMeals();
        } else if (e.getSource() == returnFlightCheckBox) {
            FlightreturnFlightComboBox.setEnabled(returnFlightCheckBox.isSelected());
            if (!returnFlightCheckBox.isSelected()) {
                FlightreturnFlightComboBox.setSelectedIndex(-1);
            }
            calculatePrice();
        } else if (e.getSource() == outboundFlightComboBox ||
                   e.getSource() == FlightreturnFlightComboBox ||
                   e.getSource() == classComboBox ||
                   e.getSource() == mealComboBox) {
            calculatePrice();
        } else if (e.getSource() == calculatePriceButton) {
            calculatePrice();
        } else if (e.getSource() == issueBookingButton) {
            issueBooking();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    private void loadCustomers() {
        try {
            customerComboBox.removeAllItems();
            customerComboBox.addItem(null); // Add null option for "Select Customer"

            for (Customer customer : fbs.getCustomers()) {
                if (!customer.isDeleted()) {
                    customerComboBox.addItem(customer);
                }
            }

            if (customerComboBox.getItemCount() > 1) {
                customerComboBox.setSelectedIndex(0);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFlights() {
        try {
            outboundFlightComboBox.removeAllItems();
            FlightreturnFlightComboBox.removeAllItems();

            outboundFlightComboBox.addItem(null); // Add null option
            FlightreturnFlightComboBox.addItem(null); // Add null option

            for (Flight flight : fbs.getFlights()) {
                if (!flight.isDeleted() && flight.hasAnySeatsLeft()) {
                    outboundFlightComboBox.addItem(flight);
                    FlightreturnFlightComboBox.addItem(flight);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMeals() {
        try {
            mealComboBox.removeAllItems();
            mealComboBox.addItem(null); // Add null option for "No Meal"

            Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
            if (selectedCustomer != null) {
                MealType preferredType = selectedCustomer.getPreferredMealType();
                List<Meal> filteredMeals = fbs.getMealsFilteredByPreference(preferredType);

                for (Meal meal : filteredMeals) {
                    mealComboBox.addItem(meal);
                }

                mealFilterLabel.setText("(Filtered by preference: " + preferredType.getDisplayName() + ")");
            } else {
                mealFilterLabel.setText("(Select customer first)");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meals: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculatePrice() {
        try {
            BigDecimal outboundPrice = BigDecimal.ZERO;
            BigDecimal returnPrice = BigDecimal.ZERO;
            BigDecimal mealPrice = BigDecimal.ZERO;

            Flight outboundFlight = (Flight) outboundFlightComboBox.getSelectedItem();
            CommercialClassType selectedClass = (CommercialClassType) classComboBox.getSelectedItem();

            if (outboundFlight != null && selectedClass != null) {
                outboundPrice = outboundFlight.getDynamicPrice(selectedClass, fbs.getSystemDate());
            }

            if (returnFlightCheckBox.isSelected()) {
                Flight returnFlight = (Flight) FlightreturnFlightComboBox.getSelectedItem();
                if (returnFlight != null && selectedClass != null) {
                    returnPrice = returnFlight.getDynamicPrice(selectedClass, fbs.getSystemDate());
                }
            }

            Meal selectedMeal = (Meal) mealComboBox.getSelectedItem();
            if (selectedMeal != null) {
                mealPrice = selectedMeal.getPrice();
            }

            BigDecimal totalPrice = outboundPrice.add(returnPrice).add(mealPrice);

            outboundPriceLabel.setText("£" + outboundPrice.setScale(2, RoundingMode.HALF_UP));
            returnPriceLabel.setText("£" + returnPrice.setScale(2, RoundingMode.HALF_UP));
            mealPriceLabel.setText("£" + mealPrice.setScale(2, RoundingMode.HALF_UP));
            totalPriceLabel.setText("£" + totalPrice.setScale(2, RoundingMode.HALF_UP));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating price: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void issueBooking() {
        try {
            // Validate selections
            Customer customer = (Customer) customerComboBox.getSelectedItem();
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Flight outboundFlight = (Flight) outboundFlightComboBox.getSelectedItem();
            if (outboundFlight == null) {
                JOptionPane.showMessageDialog(this, "Please select an outbound flight.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            CommercialClassType selectedClass = (CommercialClassType) classComboBox.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this, "Please select a class.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Flight returnFlight = null;
            if (returnFlightCheckBox.isSelected()) {
                returnFlight = (Flight) FlightreturnFlightComboBox.getSelectedItem();
                if (returnFlight == null) {
                    JOptionPane.showMessageDialog(this, "Please select a return flight or uncheck return flight option.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Meal selectedMeal = (Meal) mealComboBox.getSelectedItem();

            // Validate class availability and seat capacity
            if (!outboundFlight.isClassAvailable(selectedClass)) {
                JOptionPane.showMessageDialog(this, "Selected class is not available on outbound flight.",
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!outboundFlight.isClassAvailable(selectedClass)) {
                JOptionPane.showMessageDialog(this, "No seats available on outbound flight for selected class.",
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (returnFlight != null) {
                if (!returnFlight.isClassAvailable(selectedClass)) {
                    JOptionPane.showMessageDialog(this, "Selected class is not available on return flight.",
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!returnFlight.isClassAvailable(selectedClass)) {
                    JOptionPane.showMessageDialog(this, "No seats available on return flight for selected class.",
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Issue the booking
            fbs.addBooking(customer, outboundFlight, returnFlight, selectedClass, selectedMeal);

            // --- FIX START ---
            // Call the static store method from FlightBookingSystemData
            FlightBookingSystemData.store(mw.getFlightBookingSystem());
            // --- FIX END ---

            JOptionPane.showMessageDialog(this, "Booking successfully issued!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            mw.refreshCurrentView();
            dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { // Catch any other unexpected exceptions
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Print full stack trace for debugging
        }
    }
}