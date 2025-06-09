package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.MealType;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField nameText = new JTextField(20);
    private JTextField phoneText = new JTextField(20);
    private JTextField emailText = new JTextField(20);
    private JTextField ageText = new JTextField(5);
    private JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private JComboBox<String> preferredMealTypeComboBox;

    private JButton addBtn = new JButton("Add Customer");
    private JButton cancelBtn = new JButton("Cancel");

    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Add New Customer");
        setSize(500, 420);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);
        contentPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = DesignConstants.TABLE_ROW_FONT;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        nameText.setFont(labelFont);
        nameText.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(nameText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        phoneText.setFont(labelFont);
        phoneText.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(phoneText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailText.setFont(labelFont);
        emailText.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(emailText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(labelFont);
        ageLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        ageText.setFont(labelFont);
        ageText.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(ageText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(labelFont);
        genderLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        genderComboBox.setFont(labelFont);
        genderComboBox.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(genderComboBox, gbc);

        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        preferredMealTypeComboBox = new JComboBox<>(mealTypes);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel mealTypeLabel = new JLabel("Preferred Meal Type:");
        mealTypeLabel.setFont(labelFont);
        mealTypeLabel.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(mealTypeLabel, gbc);
        gbc.gridx = 1;
        preferredMealTypeComboBox.setFont(labelFont);
        preferredMealTypeComboBox.setForeground(DesignConstants.TEXT_DARK);
        contentPanel.add(preferredMealTypeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.BUTTON_PANEL_BOTTOM_PADDING, 0));

        customizeButton(addBtn);
        customizeButton(cancelBtn);

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

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
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void addCustomer() {
        try {
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String preferredMealTypeName = (String) preferredMealTypeComboBox.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || ageText.getText().trim().isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            int age;
            try {
                age = Integer.parseInt(ageText.getText().trim());
                if (age <= 0 || age > 150) {
                    throw new FlightBookingSystemException("Age must be a positive integer between 1 and 150.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Age must be a valid number.");
            }

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                throw new FlightBookingSystemException("Please enter a valid email address.");
            }

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

            int newCustomerId = mw.getFlightBookingSystem().generateNextCustomerId();

            Customer customer = new Customer(newCustomerId, name, phone, email, age, gender, preferredMealType);

            mw.getFlightBookingSystem().addCustomer(customer);

            FlightBookingSystemData.store(mw.getFlightBookingSystem());

            JOptionPane.showMessageDialog(this,
                "Customer " + customer.getName() + " (ID: " + customer.getId() + ") added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            mw.displayCustomers();
            this.dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}