package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class SearchCustomersWindow extends JFrame implements ActionListener {

    private MainWindow mainWindow;
    private JTextField txtSearchTerm;
    private JButton searchButton;
    private JButton cancelButton;
    private JTextArea resultArea;

    public SearchCustomersWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initialize();
    }

    private void initialize() {
        setTitle("Search Customers");
        setSize(500, 400);
        setLocationRelativeTo(mainWindow);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Search Input Panel
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchInputPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search by Name/ID/Phone/Email:"); // Updated label
        txtSearchTerm = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(50, 150, 200));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(this);
        cancelButton = new JButton("Close");
        cancelButton.setBackground(new Color(150, 150, 150));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);

        searchInputPanel.add(searchLabel);
        searchInputPanel.add(txtSearchTerm);
        searchInputPanel.add(searchButton);
        searchInputPanel.add(cancelButton);

        mainPanel.add(searchInputPanel, BorderLayout.NORTH);

        // Results Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchButton) {
            performSearch();
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }

    private void performSearch() {
        String searchTerm = txtSearchTerm.getText().trim().toLowerCase();
        resultArea.setText(""); // Clear previous results

        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FlightBookingSystem fbs = mainWindow.getFlightBookingSystem();
        List<Customer> customers = fbs.getCustomers(); // Get active customers

        List<Customer> foundCustomers = customers.stream()
            .filter(customer ->
                String.valueOf(customer.getId()).toLowerCase().contains(searchTerm) ||
                customer.getName().toLowerCase().contains(searchTerm) ||
                customer.getPhone().toLowerCase().contains(searchTerm) ||
                customer.getEmail().toLowerCase().contains(searchTerm) // Removed passport from search
            )
            .collect(Collectors.toList());

        if (foundCustomers.isEmpty()) {
            resultArea.setText("No customers found matching '" + searchTerm + "'.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Found ").append(foundCustomers.size()).append(" customer(s) matching '").append(searchTerm).append("':\n\n");
            for (Customer customer : foundCustomers) {
                sb.append(customer.getDetailsShort()).append("\n");
            }
            resultArea.setText(sb.toString());
            resultArea.setCaretPosition(0);
        }
    }
}