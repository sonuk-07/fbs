package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddCustomer; // The command you provided earlier
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException; // For error handling
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.MealType; // To populate the preferred meal type combo box
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData; // For saving data

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher; // For email validation
import java.util.regex.Pattern; // For email validation

public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JTextField ageText = new JTextField();
    private JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private JComboBox<String> preferredMealTypeComboBox; // Will be populated dynamically

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Add a New Customer");
        setSize(450, 400); // Adjusted size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(7, 2, 5, 5)); // 7 rows for all fields + labels

        topPanel.add(new JLabel("Name : "));
        topPanel.add(nameText);
        topPanel.add(new JLabel("Phone : "));
        topPanel.add(phoneText);
        topPanel.add(new JLabel("Email : "));
        topPanel.add(emailText);
        topPanel.add(new JLabel("Age : "));
        topPanel.add(ageText);
        topPanel.add(new JLabel("Gender : "));
        topPanel.add(genderComboBox);
        
        // Populate preferred meal type combo box
        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        preferredMealTypeComboBox = new JComboBox<>(mealTypes);
        topPanel.add(new JLabel("Preferred Meal Type : "));
        topPanel.add(preferredMealTypeComboBox);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     ")); // Spacer
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw); // Center window relative to MainWindow

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false); // Close the window
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
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || ageText.getText().trim().isEmpty() || gender.isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            // Age validation
            int age;
            try {
                age = Integer.parseInt(ageText.getText().trim());
                if (age <= 0 || age > 150) { // Reasonable age limits
                    throw new FlightBookingSystemException("Age must be a positive integer and reasonable.");
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