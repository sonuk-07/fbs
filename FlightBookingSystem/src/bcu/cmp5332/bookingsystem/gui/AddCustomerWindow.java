package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.MealType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mainWindow;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAge;
    private JTextField txtGender;
    private JComboBox<String> cmbPreferredMealType;

    private JButton addButton;
    private JButton cancelButton;

    public AddCustomerWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initialize();
    }

    private void initialize() {
        setTitle("Add New Customer");
        setSize(450, 400);
        setLocationRelativeTo(mainWindow);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        mainPanel.add(createFormField("Name:", txtName = new JTextField(20)));
        mainPanel.add(createFormField("Phone:", txtPhone = new JTextField(20)));
        mainPanel.add(createFormField("Email:", txtEmail = new JTextField(20)));
        mainPanel.add(createFormField("Age:", txtAge = new JTextField(20)));
        mainPanel.add(createFormField("Gender:", txtGender = new JTextField(20)));

        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        cmbPreferredMealType = new JComboBox<>(mealTypes);
        mainPanel.add(createFormField("Preferred Meal:", cmbPreferredMealType));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        addButton = new JButton("Add Customer");
        addButton.setBackground(new Color(50, 150, 200));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 80, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);

        buttonsPanel.add(addButton);
        buttonsPanel.add(cancelButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonsPanel);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createFormField(String labelText, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 10));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        panel.add(label);
        panel.add(component);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addButton) {
            addCustomer();
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }

    private void addCustomer() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String gender = txtGender.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());
            MealType preferredMealType = MealType.values()[cmbPreferredMealType.getSelectedIndex()];

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || gender.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (age <= 0) {
                 JOptionPane.showMessageDialog(this, "Age must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            FlightBookingSystem fbs = mainWindow.getFlightBookingSystem();
            int newId = fbs.generateNextCustomerId();

            Customer customer = new Customer(newId, name, phone, email, age, gender, preferredMealType);
            fbs.addCustomer(customer);

            JOptionPane.showMessageDialog(this, "Customer added successfully!\nID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
            
            if (mainWindow.getTitle().contains("Flight Booking Management System")) {
                 mainWindow.displayCustomers();
            }

            try {
                FlightBookingSystemData.store(fbs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }

            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}