package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class IssueBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;

    private JComboBox<Customer> customerComboBox;
    private JButton loadCustomersButton;

    private JComboBox<Flight> outboundFlightComboBox;
    private JComboBox<Flight> returnFlightComboBox;
    private JButton loadFlightsButton;
    private JCheckBox returnFlightCheckBox;

    private JComboBox<CommercialClassType> classComboBox;

    private JComboBox<Meal> mealComboBox;
    private JButton loadMealsButton;
    private JLabel mealFilterLabel;

    private JLabel outboundPriceLabel;
    private JLabel returnPriceLabel;
    private JLabel mealPriceLabel;
    private JLabel totalPriceLabel;

    private JButton calculatePriceButton;
    private JButton issueBookingButton;
    private JButton cancelButton;

    public IssueBookingWindow(MainWindow mw) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Issue New Booking");
        setSize(600, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mw);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);
        mainPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = DesignConstants.TABLE_ROW_FONT;
        Font componentFont = DesignConstants.TABLE_ROW_FONT;
        Color textColor = DesignConstants.TEXT_DARK;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(labelFont);
        customerLabel.setForeground(textColor);
        formPanel.add(customerLabel, gbc);

        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(250, DesignConstants.COMPONENT_HEIGHT));
        customerComboBox.setFont(componentFont);
        customerComboBox.setForeground(textColor);
        formPanel.add(customerComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadCustomersButton = new JButton("Load Customers");
        customizeButton(loadCustomersButton);
        loadCustomersButton.addActionListener(this);
        formPanel.add(loadCustomersButton, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel outboundFlightLabel = new JLabel("Outbound Flight:");
        outboundFlightLabel.setFont(labelFont);
        outboundFlightLabel.setForeground(textColor);
        formPanel.add(outboundFlightLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        outboundFlightComboBox = new JComboBox<>();
        outboundFlightComboBox.setPreferredSize(new Dimension(250, DesignConstants.COMPONENT_HEIGHT));
        outboundFlightComboBox.setFont(componentFont);
        outboundFlightComboBox.setForeground(textColor);
        outboundFlightComboBox.addActionListener(this);
        formPanel.add(outboundFlightComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadFlightsButton = new JButton("Load Flights");
        customizeButton(loadFlightsButton);
        loadFlightsButton.addActionListener(this);
        formPanel.add(loadFlightsButton, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        returnFlightCheckBox = new JCheckBox("Include Return Flight");
        returnFlightCheckBox.setFont(labelFont);
        returnFlightCheckBox.setForeground(textColor);
        returnFlightCheckBox.setBackground(DesignConstants.LIGHT_GRAY_BG);
        returnFlightCheckBox.addActionListener(this);
        formPanel.add(returnFlightCheckBox, gbc);
        gbc.gridwidth = 1;
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel returnFlightActualLabel = new JLabel("Return Flight:");
        returnFlightActualLabel.setFont(labelFont);
        returnFlightActualLabel.setForeground(textColor);
        formPanel.add(returnFlightActualLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        returnFlightComboBox = new JComboBox<>();
        returnFlightComboBox.setPreferredSize(new Dimension(250, DesignConstants.COMPONENT_HEIGHT));
        returnFlightComboBox.setFont(componentFont);
        returnFlightComboBox.setForeground(textColor);
        returnFlightComboBox.setEnabled(false);
        returnFlightComboBox.addActionListener(this);
        formPanel.add(returnFlightComboBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel classLabel = new JLabel("Class:");
        classLabel.setFont(labelFont);
        classLabel.setForeground(textColor);
        formPanel.add(classLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        classComboBox = new JComboBox<>(CommercialClassType.values());
        classComboBox.setFont(componentFont);
        classComboBox.setForeground(textColor);
        classComboBox.addActionListener(this);
        formPanel.add(classComboBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel mealLabel = new JLabel("Meal:");
        mealLabel.setFont(labelFont);
        mealLabel.setForeground(textColor);
        formPanel.add(mealLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mealComboBox = new JComboBox<>();
        mealComboBox.setPreferredSize(new Dimension(250, DesignConstants.COMPONENT_HEIGHT));
        mealComboBox.setFont(componentFont);
        mealComboBox.setForeground(textColor);
        mealComboBox.addActionListener(this);
        formPanel.add(mealComboBox, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        loadMealsButton = new JButton("Load Meals");
        customizeButton(loadMealsButton);
        loadMealsButton.addActionListener(this);
        formPanel.add(loadMealsButton, gbc);
        row++;

        gbc.gridx = 1; gbc.gridy = row;
        gbc.gridwidth = 2;
        mealFilterLabel = new JLabel("(Meals filtered by customer preference)");
        mealFilterLabel.setFont(DesignConstants.SMALL_ITALIC_FONT);
        mealFilterLabel.setForeground(DesignConstants.TEXT_MUTED);
        formPanel.add(mealFilterLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel pricePanel = new JPanel(new GridBagLayout());
        pricePanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        pricePanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.TITLED_BORDER_STYLE,
            "Price Breakdown",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            DesignConstants.TABLE_HEADER_FONT,
            DesignConstants.TEXT_DARK
        ));

        Font priceLabelFont = DesignConstants.BUTTON_FONT;
        Color priceColor = DesignConstants.TEXT_HIGHLIGHT;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel outboundPriceTitle = new JLabel("Outbound Flight Price:");
        outboundPriceTitle.setFont(labelFont);
        outboundPriceTitle.setForeground(textColor);
        pricePanel.add(outboundPriceTitle, gbc);
        gbc.gridx = 1;
        outboundPriceLabel = new JLabel("£0.00");
        outboundPriceLabel.setFont(priceLabelFont);
        outboundPriceLabel.setForeground(priceColor);
        pricePanel.add(outboundPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel returnPriceTitle = new JLabel("Return Flight Price:");
        returnPriceTitle.setFont(labelFont);
        returnPriceTitle.setForeground(textColor);
        pricePanel.add(returnPriceTitle, gbc);
        gbc.gridx = 1;
        returnPriceLabel = new JLabel("£0.00");
        returnPriceLabel.setFont(priceLabelFont);
        returnPriceLabel.setForeground(priceColor);
        pricePanel.add(returnPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel mealPriceTitle = new JLabel("Meal Price:");
        mealPriceTitle.setFont(labelFont);
        mealPriceTitle.setForeground(textColor);
        pricePanel.add(mealPriceTitle, gbc);
        gbc.gridx = 1;
        mealPriceLabel = new JLabel("£0.00");
        mealPriceLabel.setFont(priceLabelFont);
        mealPriceLabel.setForeground(priceColor);
        pricePanel.add(mealPriceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalLabel = new JLabel("Total Price:");
        totalLabel.setFont(DesignConstants.BOLD_PRICE_FONT);
        totalLabel.setForeground(DesignConstants.TEXT_DARK);
        pricePanel.add(totalLabel, gbc);
        gbc.gridx = 1;
        totalPriceLabel = new JLabel("£0.00");
        totalPriceLabel.setFont(DesignConstants.BOLD_PRICE_FONT);
        totalPriceLabel.setForeground(DesignConstants.ACCENT_COLOR);
        pricePanel.add(totalPriceLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.BUTTON_PANEL_BOTTOM_PADDING, 0));

        calculatePriceButton = new JButton("Calculate Price");
        issueBookingButton = new JButton("Issue Booking");
        cancelButton = new JButton("Cancel");

        customizeButton(calculatePriceButton);
        customizeButton(issueBookingButton);
        customizeButton(cancelButton);

        buttonPanel.add(calculatePriceButton);
        buttonPanel.add(issueBookingButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(pricePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomers();
        loadFlights();

        setVisible(true);
    }

    private void customizeButton(JButton button) {
        button.setBackground(DesignConstants.BUTTON_BLUE);
        button.setForeground(DesignConstants.BUTTON_TEXT_COLOR);
        button.setFont(DesignConstants.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(DesignConstants.BUTTON_PADDING_BORDER);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(DesignConstants.BUTTON_HOVER_BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DesignConstants.BUTTON_BLUE);
            }
        });
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
            returnFlightComboBox.setEnabled(returnFlightCheckBox.isSelected());
            if (!returnFlightCheckBox.isSelected()) {
                returnFlightComboBox.setSelectedIndex(-1);
            }
            calculatePrice();
        } else if (e.getSource() == outboundFlightComboBox ||
                   e.getSource() == returnFlightComboBox ||
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
            customerComboBox.addItem(null);

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
            returnFlightComboBox.removeAllItems();

            outboundFlightComboBox.addItem(null);
            returnFlightComboBox.addItem(null);

            for (Flight flight : fbs.getFlights()) {
                if (!flight.isDeleted() && flight.hasAnySeatsLeft()) {
                    outboundFlightComboBox.addItem(flight);
                    returnFlightComboBox.addItem(flight);
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
            mealComboBox.addItem(null);

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
                Flight returnFlight = (Flight) returnFlightComboBox.getSelectedItem();
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
            outboundPriceLabel.setText("£0.00");
            returnPriceLabel.setText("£0.00");
            mealPriceLabel.setText("£0.00");
            totalPriceLabel.setText("£0.00");
        }
    }

    private void issueBooking() {
        try {
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
                returnFlight = (Flight) returnFlightComboBox.getSelectedItem();
                if (returnFlight == null) {
                    JOptionPane.showMessageDialog(this, "Please select a return flight or uncheck return flight option.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (outboundFlight.equals(returnFlight)) {
                    JOptionPane.showMessageDialog(this, "Outbound and return flights cannot be the same.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (returnFlight.getDepartureDate().isBefore(outboundFlight.getDepartureDate())) {
                    JOptionPane.showMessageDialog(this, "Return flight must depart on or after the outbound flight.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Meal selectedMeal = (Meal) mealComboBox.getSelectedItem();

            // Corrected: Reverted to isClassAvailable() as per original request
            if (!outboundFlight.isClassAvailable(selectedClass)) {
                JOptionPane.showMessageDialog(this, "Selected class is not available on outbound flight.",
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (returnFlight != null) {
                // Corrected: Reverted to isClassAvailable() as per original request
                if (!returnFlight.isClassAvailable(selectedClass)) {
                    JOptionPane.showMessageDialog(this, "Selected class is not available on return flight.",
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            fbs.addBooking(customer, outboundFlight, returnFlight, selectedClass, selectedMeal);
            
            FlightBookingSystemData.store(mw.getFlightBookingSystem());
            
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}