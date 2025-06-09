package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class EditBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Booking booking;

    private JLabel bookingIdLabel;
    private JComboBox<String> mealComboBox;
    private JComboBox<String> classComboBox;

    private JButton saveButton;
    private JButton cancelButton;

    public EditBookingWindow(MainWindow mw, Booking booking) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        this.booking = booking;
        initialize();
    }

    private void initialize() {
        setTitle("Edit Booking - ID: " + booking.getId());
        setSize(500, 400);
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.PRIMARY_BLUE);
        headerPanel.setBorder(DesignConstants.HEADER_PANEL_BORDER);
        JLabel titleLabel = new JLabel("Edit Booking Details");
        titleLabel.setFont(DesignConstants.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(DesignConstants.FORM_PANEL_BORDER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignConstants.FORM_INSET_GAP, DesignConstants.FORM_INSET_GAP, DesignConstants.FORM_INSET_GAP, DesignConstants.FORM_INSET_GAP);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Booking ID:"), gbc);
        gbc.gridx = 1;
        bookingIdLabel = new JLabel(String.valueOf(booking.getId()));
        bookingIdLabel.setFont(DesignConstants.DETAILS_LABEL_FONT);
        formPanel.add(bookingIdLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(booking.getCustomer().getName()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Meal:"), gbc);
        gbc.gridx = 1;
        String[] dummyMeals = {"None", "Standard", "Vegetarian", "Vegan"};
        mealComboBox = new JComboBox<>(dummyMeals);
        if (booking.getMeal() != null) {
            mealComboBox.setSelectedItem(booking.getMeal().getName());
        } else {
            mealComboBox.setSelectedItem("None");
        }
        formPanel.add(mealComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        String[] dummyClasses = {"Economy", "Business", "First"};
        classComboBox = new JComboBox<>(dummyClasses);
        if (booking.getBookedClass() != null) {
            classComboBox.setSelectedItem(booking.getBookedClass().getClassName());
        }
        formPanel.add(classComboBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);

        saveButton = new JButton("Save Changes");
        saveButton.setFont(DesignConstants.BUTTON_FONT);
        saveButton.setBackground(DesignConstants.STATUS_ACTIVE_GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(DesignConstants.BUTTON_FONT);
        cancelButton.setBackground(DesignConstants.SECONDARY_BUTTON_BG);
        cancelButton.setForeground(DesignConstants.TEXT_DARK);
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
            try {
                String selectedMealName = (String) mealComboBox.getSelectedItem();
                String selectedClassName = (String) classComboBox.getSelectedItem();
                
                JOptionPane.showMessageDialog(this, "Changes saved for Booking ID: " + booking.getId() + 
                                               "\n(Selected Meal: " + selectedMealName + 
                                               ", Selected Class: " + selectedClassName + ")", 
                                               "Success", JOptionPane.INFORMATION_MESSAGE);
                mw.refreshCurrentView();
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }
}