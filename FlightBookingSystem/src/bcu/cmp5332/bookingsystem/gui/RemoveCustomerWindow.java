package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RemoveCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mainWindow;
    private JTextField txtCustomerId;
    private JButton removeButton;
    private JButton cancelButton;

    public RemoveCustomerWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initialize();
    }

    private void initialize() {
        setTitle("Remove Customer");
        setSize(350, 180);
        setLocationRelativeTo(mainWindow);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.setOpaque(false);
        JLabel idLabel = new JLabel("Customer ID:");
        idLabel.setPreferredSize(new Dimension(100, idLabel.getPreferredSize().height));
        txtCustomerId = new JTextField(15);
        idPanel.add(idLabel);
        idPanel.add(txtCustomerId);
        mainPanel.add(idPanel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        removeButton = new JButton("Remove Customer");
        removeButton.setBackground(new Color(200, 80, 80));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(150, 150, 150));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);

        buttonsPanel.add(removeButton);
        buttonsPanel.add(cancelButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonsPanel);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == removeButton) {
            removeCustomer();
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }

    private void removeCustomer() {
        try {
            int customerId = Integer.parseInt(txtCustomerId.getText().trim());
            FlightBookingSystem fbs = mainWindow.getFlightBookingSystem();

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove customer with ID: " + customerId + "?\n" +
                "This will mark the customer as removed.",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = fbs.removeCustomerById(customerId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer ID " + customerId + " removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (mainWindow.getTitle().contains("Flight Booking Management System")) {
                         mainWindow.displayCustomers();
                    }

                    try {
                        FlightBookingSystemData.store(fbs);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                    }
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Customer with ID " + customerId + " not found or already removed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error removing customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}