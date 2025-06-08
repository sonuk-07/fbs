package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.CommercialClassType; // For booking details
import bcu.cmp5332.bookingsystem.model.Flight; // For flight details in bookings

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter; // For formatting dates

public class CustomerDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Customer customer;

    // Labels for customer details
    private JLabel customerIdLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel phoneLabel = new JLabel();
    private JLabel emailLabel = new JLabel();
    private JLabel ageLabel = new JLabel();
    private JLabel genderLabel = new JLabel();
    private JLabel preferredMealLabel = new JLabel();
    private JLabel statusLabel = new JLabel(); // For customer's deleted status

    // Table for bookings
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;

    private JButton closeBtn = new JButton("Close");

    public CustomerDetailsWindow(MainWindow mw, Customer customer) {
        this.mw = mw;
        this.customer = customer;
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

        setTitle("Customer Details: " + customer.getName());
        setSize(700, 600); // Increased size to accommodate bookings table
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Customer Information Panel ---
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 5, 5)); // 8 rows for details
        infoPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));

        infoPanel.add(new JLabel("Customer ID: "));
        customerIdLabel.setText(String.valueOf(customer.getId()));
        infoPanel.add(customerIdLabel);

        infoPanel.add(new JLabel("Name: "));
        nameLabel.setText(customer.getName());
        infoPanel.add(nameLabel);

        infoPanel.add(new JLabel("Phone: "));
        phoneLabel.setText(customer.getPhone());
        infoPanel.add(phoneLabel);

        infoPanel.add(new JLabel("Email: "));
        emailLabel.setText(customer.getEmail());
        infoPanel.add(emailLabel);

        infoPanel.add(new JLabel("Age: "));
        ageLabel.setText(String.valueOf(customer.getAge()));
        infoPanel.add(ageLabel);

        infoPanel.add(new JLabel("Gender: "));
        genderLabel.setText(customer.getGender());
        infoPanel.add(genderLabel);

        infoPanel.add(new JLabel("Preferred Meal: "));
        preferredMealLabel.setText(customer.getPreferredMealType().getDisplayName()); // Using getDisplayName()
        infoPanel.add(preferredMealLabel);
        
        infoPanel.add(new JLabel("Status: ")); // Customer status
        statusLabel.setText(customer.isDeleted() ? "Removed" : "Active");
        statusLabel.setForeground(customer.isDeleted() ? Color.RED : Color.GREEN.darker());
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(statusLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // --- Bookings Panel ---
        JPanel bookingsPanel = new JPanel(new BorderLayout());
        bookingsPanel.setBorder(BorderFactory.createTitledBorder("Customer Bookings"));

        setupBookingsTable(); // Method to set up the bookings table
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        bookingsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(bookingsPanel, BorderLayout.CENTER);

        // --- Bottom Panel with Close Button ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        closeBtn.addActionListener(this);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw); // Center relative to MainWindow
        setVisible(true);
    }

    /**
     * Sets up the JTable for displaying customer bookings.
     */
    private void setupBookingsTable() {
        String[] columns = new String[]{
            "Booking Date", "Outbound Flight", "Return Flight", "Class", "Outbound Price (£)", "Return Price (£)", "Meal", "Cancellation Fee (£)"
        };

        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Booking booking : customer.getBookings()) {
            String outboundFlightDetails = "N/A";
            if (booking.getOutboundFlight() != null) {
                Flight of = booking.getOutboundFlight();
                outboundFlightDetails = of.getFlightNumber() + " (" + of.getOrigin() + " to " + of.getDestination() + ")";
            }

            String returnFlightDetails = "N/A";
            if (booking.getReturnFlight() != null) {
                Flight rf = booking.getReturnFlight();
                returnFlightDetails = rf.getFlightNumber() + " (" + rf.getOrigin() + " to " + rf.getDestination() + ")";
            }
            
            String mealName = "None";
            if (booking.getMeal() != null) {
                mealName = booking.getMeal().getName(); // Assuming Meal has getDisplayName()
            }

            bookingsTableModel.addRow(new Object[]{
                booking.getBookingDate().format(dtf),
                outboundFlightDetails,
                returnFlightDetails,
                booking.getBookedClass().getClassName(), // Using getClassName() for CommercialClassType
                booking.getBookedPriceOutbound().toPlainString(),
                booking.getBookedPriceReturn().toPlainString(),
                mealName,
                booking.getCancellationFee() != null ? booking.getCancellationFee().toPlainString() : "N/A"
            });
        }

        bookingsTable = new JTable(bookingsTableModel);

        // Styling for the bookings table
        bookingsTable.setRowHeight(25);
        bookingsTable.setShowGrid(true);
        bookingsTable.setGridColor(new Color(220, 220, 220));
        bookingsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        bookingsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        bookingsTable.getTableHeader().setBackground(new Color(245, 245, 250));
        bookingsTable.getTableHeader().setForeground(new Color(60, 60, 60));
        bookingsTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths for bookings table
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Booking Date
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Outbound Flight
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Return Flight
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Class
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Outbound Price
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Return Price
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Meal
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Cancellation Fee

        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeBtn) {
            this.dispose(); // Close the window
        }
    }
}