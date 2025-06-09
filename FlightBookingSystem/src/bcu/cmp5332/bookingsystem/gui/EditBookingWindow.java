package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking; // Import Booking model
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem; // Assuming you might need this
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate; // Assuming you might use LocalDate for dates

public class EditBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs; // You might need this for updates
    private Booking booking; // The booking to be edited

    // Example UI components for editing (you'll expand these)
    private JLabel bookingIdLabel;
    private JComboBox<String> mealComboBox; // Example: to change meal
    private JComboBox<String> classComboBox; // Example: to change class

    private JButton saveButton;
    private JButton cancelButton;

    // Correct Constructor
    public EditBookingWindow(MainWindow mw, Booking booking) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem(); // Get FBS instance
        this.booking = booking;
        initialize();
    }

    private void initialize() {
        setTitle("Edit Booking - ID: " + booking.getId());
        setSize(500, 400); // Adjusted size
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(34, 107, 172)); // Primary Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("Edit Booking Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel (using GridBagLayout for flexible arrangement)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 107, 172).darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Booking ID (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Booking ID:"), gbc);
        gbc.gridx = 1;
        bookingIdLabel = new JLabel(String.valueOf(booking.getId()));
        bookingIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(bookingIdLabel, gbc);

        // Row 1: Customer Name (read-only)
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(booking.getCustomer().getName()), gbc);

        // Row 2: Meal selection (Example editable field)
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Meal:"), gbc);
        gbc.gridx = 1;
        // Populate mealComboBox with available meals from FBS
        // For now, dummy data
        String[] dummyMeals = {"None", "Standard", "Vegetarian", "Vegan"};
        mealComboBox = new JComboBox<>(dummyMeals);
        if (booking.getMeal() != null) {
            mealComboBox.setSelectedItem(booking.getMeal().getName());
        } else {
            mealComboBox.setSelectedItem("None");
        }
        formPanel.add(mealComboBox, gbc);
        
        // Row 3: Class selection (Example editable field)
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        // Populate classComboBox with available classes from FBS
        // For now, dummy data
        String[] dummyClasses = {"Economy", "Business", "First"};
        classComboBox = new JComboBox<>(dummyClasses);
        if (booking.getBookedClass() != null) {
            classComboBox.setSelectedItem(booking.getBookedClass().getClassName());
        }
        formPanel.add(classComboBox, gbc);


        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245)); // Light Gray BG

        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113)); // Accent Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(new Color(44, 62, 80)); // Text Dark
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == saveButton) {
            // TODO: Implement actual logic to save changes to the booking object
            // You would get the selected values from mealComboBox, classComboBox etc.
            // and call a method in fbs to update the booking.
            
            // Example: Update the booking object (replace with actual logic)
            // For now, just show a message.
            try {
                // Example of getting selected values
                String selectedMealName = (String) mealComboBox.getSelectedItem();
                String selectedClassName = (String) classComboBox.getSelectedItem();

                // Call a method in FBS to update.
                // You'll need to implement this method in FlightBookingSystem
                // fbs.updateBookingDetails(booking.getId(), selectedMealName, selectedClassName); 
                
                JOptionPane.showMessageDialog(this, "Changes saved for Booking ID: " + booking.getId() + 
                                               "\n(Selected Meal: " + selectedMealName + 
                                               ", Selected Class: " + selectedClassName + ")", 
                                               "Success", JOptionPane.INFORMATION_MESSAGE);
                mw.refreshCurrentView(); // Refresh the main window's booking view
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == cancelButton) {
            this.dispose(); // Close without saving
        }
    }
}