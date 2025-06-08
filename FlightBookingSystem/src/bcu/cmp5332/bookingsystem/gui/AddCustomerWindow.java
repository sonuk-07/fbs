package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.MealType;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField nameText = new JTextField(20); // Set preferred column width
    private JTextField phoneText = new JTextField(20);
    private JTextField emailText = new JTextField(20);
    private JTextField ageText = new JTextField(5);
    private JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private JComboBox<String> preferredMealTypeComboBox;

    private JButton addBtn = new JButton("Add Customer"); // More descriptive text
    private JButton cancelBtn = new JButton("Cancel");

    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Add New Customer");
        setSize(500, 420); // Adjusted size for better spacing
        setResizable(false); // Often good for fixed forms
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel using GridBagLayout for flexible arrangement
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill available width
        gbc.anchor = GridBagConstraints.WEST; // Align to the left

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(nameText, gbc);

        // Row 1: Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(phoneText, gbc);

        // Row 2: Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(emailText, gbc);

        // Row 3: Age
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ageText, gbc);

        // Row 4: Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(genderComboBox, gbc);

        // Row 5: Preferred Meal Type
        // Populate preferred meal type combo box
        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        preferredMealTypeComboBox = new JComboBox<>(mealTypes);

        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(new JLabel("Preferred Meal Type:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(preferredMealTypeComboBox, gbc);

        // Buttons Panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); // Align right, add horizontal spacing
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw); // Center window relative to MainWindow

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false); // Close the window
            this.dispose(); // Release resources
        }
    }

    private void addCustomer() {
        try {
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String preferredMealTypeName = (String) preferredMealTypeComboBox.getSelectedItem();

            // --- Input Validation ---
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || ageText.getText().trim().isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            // Age validation
            int age;
            try {
                age = Integer.parseInt(ageText.getText().trim());
                if (age <= 0 || age > 150) { // Reasonable age limits
                    throw new FlightBookingSystemException("Age must be a positive integer and reasonable (1-150).");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Age must be a valid number.");
            }

            // Basic email validation (simple regex)
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                throw new FlightBookingSystemException("Please enter a valid email address.");
            }

            // Find the corresponding MealType enum from display name
            MealType preferredMealType = null;
            for (MealType mt : MealType.values()) {
                if (mt.getDisplayName().equals(preferredMealTypeName)) {
                    preferredMealType = mt;
                    break;
                }
            }
            if (preferredMealType == null) {
                throw new FlightBookingSystemException("Selected preferred meal type is invalid.");
            }

            // Get a new ID from FlightBookingSystem
            int newCustomerId = mw.getFlightBookingSystem().generateNextCustomerId();
            
            // Create the new Customer object
            Customer customer = new Customer(newCustomerId, name, phone, email, age, gender, preferredMealType);

            // Use the FlightBookingSystem to add the customer
            mw.getFlightBookingSystem().addCustomer(customer);
            
            // Save data to file
            FlightBookingSystemData.store(mw.getFlightBookingSystem());
            
            JOptionPane.showMessageDialog(this, 
                "Customer " + customer.getName() + " (ID: " + customer.getId() + ") added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            mw.displayCustomers(); // Refresh the customer list in the main window
            this.dispose(); // Close the add customer window

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Print full stack trace for unexpected errors
        }
    }
}