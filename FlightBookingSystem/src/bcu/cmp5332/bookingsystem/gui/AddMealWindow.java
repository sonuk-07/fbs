package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;

public class AddMealWindow extends JFrame implements ActionListener {

    private MainWindow mw;

    private JTextField nameText = new JTextField(20);
    private JTextField descriptionText = new JTextField(25);
    private JTextField priceText = new JTextField(10);
    private JComboBox<String> mealTypeComboBox;

    private JButton addBtn = new JButton("Add Meal");
    private JButton cancelBtn = new JButton("Cancel");

    private JPanel contentPanel;

    public AddMealWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Could not set system look and feel: " + ex.getMessage());
        }

        setTitle("Add New Meal");
        setSize(450, 350);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);
        contentPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = DesignConstants.TABLE_ROW_FONT;
        Font textFieldFont = DesignConstants.TABLE_ROW_FONT;
        Color textColor = DesignConstants.TEXT_DARK;

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(textColor);
        contentPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        nameText.setFont(textFieldFont);
        nameText.setForeground(textColor);
        contentPanel.add(nameText, gbc);

        // Row 1: Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(labelFont);
        descriptionLabel.setForeground(textColor);
        contentPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        descriptionText.setFont(textFieldFont);
        descriptionText.setForeground(textColor);
        contentPanel.add(descriptionText, gbc);

        // Row 2: Price
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel priceLabel = new JLabel("Price (£):");
        priceLabel.setFont(labelFont);
        priceLabel.setForeground(textColor);
        contentPanel.add(priceLabel, gbc);
        gbc.gridx = 1;
        priceText.setFont(textFieldFont);
        priceText.setForeground(textColor);
        contentPanel.add(priceText, gbc);

        // Row 3: Meal Type
        String[] mealTypes = new String[MealType.values().length];
        for (int i = 0; i < MealType.values().length; i++) {
            mealTypes[i] = MealType.values()[i].getDisplayName();
        }
        mealTypeComboBox = new JComboBox<>(mealTypes);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel mealTypeLabel = new JLabel("Meal Type:");
        mealTypeLabel.setFont(labelFont);
        mealTypeLabel.setForeground(textColor);
        contentPanel.add(mealTypeLabel, gbc);
        gbc.gridx = 1;
        mealTypeComboBox.setFont(textFieldFont);
        mealTypeComboBox.setForeground(textColor);
        contentPanel.add(mealTypeComboBox, gbc);

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