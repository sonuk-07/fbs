package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;

public class AddMealWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField nameText = new JTextField(20);
    private JTextField descriptionText = new JTextField(25); // Longer for description
    private JTextField priceText = new JTextField(10);
    private JComboBox<String> mealTypeComboBox; // <--- Correct: Use a single name

    private JButton addBtn = new JButton("Add Meal");
    private JButton cancelBtn = new JButton("Cancel");

    private JPanel contentPanel; // <--- FIX: Declare contentPanel as a member variable

    public AddMealWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
        }

        setTitle("Add New Meal");
        setSize(450, 350); // Adjusted size for better spacing
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPanel = new JPanel(new GridBagLayout()); // <--- Initialize here
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(nameText, gbc);

        // Row 1: Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(descriptionText, gbc);

        // Row 2: Price
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Price (£):"), gbc);
        gbc.gridx = 1;
        contentPanel.add(priceText, gbc);

        // Row 3: Meal Type
        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        mealTypeComboBox = new JComboBox<>(mealTypes); // <--- Correct: Only this line to initialize

        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(mealTypeComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addMeal();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void addMeal() {
        try {
            String name = nameText.getText().trim();
            String description = descriptionText.getText().trim();
            String priceStr = priceText.getText().trim();
            String mealTypeName = (String) mealTypeComboBox.getSelectedItem();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || mealTypeName.isEmpty()) {
                throw new FlightBookingSystemException("All fields must be filled out.");
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new FlightBookingSystemException("Price cannot be negative.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Price must be a valid number (e.g., 10.50).");
            }

            MealType type = null;
            for (MealType mt : MealType.values()) {
                if (mt.getDisplayName().equals(mealTypeName)) {
                    type = mt;
                    break;
                }
            }
            if (type == null) {
                throw new FlightBookingSystemException("Selected meal type is invalid.");
            }

            int newMealId = mw.getFlightBookingSystem().generateNextMealId();
            Meal meal = new Meal(newMealId, name, description, price, type);
            mw.getFlightBookingSystem().addMeal(meal);
            
            FlightBookingSystemData.store(mw.getFlightBookingSystem());
            
            JOptionPane.showMessageDialog(this, 
                "Meal '" + meal.getName() + "' (ID: " + meal.getId() + ") added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            mw.displayMeals();
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