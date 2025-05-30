package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddMeal; // Your command
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.MealType; // Assuming you have a MealType enum

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class AddMealWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField nameText = new JTextField();
    private JTextField priceText = new JTextField();
    private JComboBox<MealType> mealTypeComboBox; // Assuming you have a MealType enum

    private JButton addBtn = new JButton("Add Meal");
    private JButton cancelBtn = new JButton("Cancel");

    public AddMealWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Add New Meal");
        setSize(350, 200);
        setLocationRelativeTo(mw); // Center relative to main window
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Rows for Name, Price, Type
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Meal Name : "));
        formPanel.add(nameText);
        formPanel.add(new JLabel("Price : "));
        formPanel.add(priceText);

        formPanel.add(new JLabel("Meal Type : "));
        mealTypeComboBox = new JComboBox<>(MealType.values()); // Populate from your MealType enum
        formPanel.add(mealTypeComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addMeal();
        } else if (ae.getSource() == cancelBtn) {
            dispose();
        }
    }

    private void addMeal() {
        try {
            String name = nameText.getText();
            BigDecimal price;
            try {
                price = new BigDecimal(priceText.getText());
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new FlightBookingSystemException("Price cannot be negative.");
                }
            } catch (NumberFormatException nfe) {
                throw new FlightBookingSystemException("Price must be a valid number.");
            }
            MealType mealType = (MealType) mealTypeComboBox.getSelectedItem();

            if (name.isEmpty()) {
                throw new FlightBookingSystemException("Meal Name cannot be empty.");
            }
            if (mealType == null) {
                throw new FlightBookingSystemException("Please select a Meal Type.");
            }

            Command addMealCommand = new AddMeal(name, price, mealType); // Your AddMeal command
            addMealCommand.execute(mw.getFlightBookingSystem(), null);

            JOptionPane.showMessageDialog(this, "Meal '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Optionally, refresh a meal list if one is open or show it
            if (mw.isVisible()) { // Check if main window is still open
                // You might want to open or refresh ViewAllMealsWindow here
                // For now, just dispose
                // new ViewAllMealsWindow(mw); // Uncomment if you want it to refresh automatically
            }
            dispose(); // Close this window

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}