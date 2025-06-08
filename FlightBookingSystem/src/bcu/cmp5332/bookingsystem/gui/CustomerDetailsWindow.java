package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Flight;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class CustomerDetailsWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private Customer customer;

    private JLabel customerIdValueLabel = new JLabel();
    private JLabel nameValueLabel = new JLabel();
    private JLabel phoneValueLabel = new JLabel();
    private JLabel emailValueLabel = new JLabel();
    private JLabel ageValueLabel = new JLabel();
    private JLabel genderValueLabel = new JLabel();
    private JLabel preferredMealValueLabel = new JLabel();
    private JLabel statusValueLabel = new JLabel();

    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;

    private JButton closeBtn = new JButton("Close");

    public CustomerDetailsWindow(MainWindow mw, Customer customer) {
        this.mw = mw;
        this.customer = customer;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set LookAndFeel: " + ex);
        }

        setTitle("Customer Details: " + customer.getName());
        setSize(800, 650);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Customer Information", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 150)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; customerIdValueLabel.setText(String.valueOf(customer.getId()));
        infoPanel.add(customerIdValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; nameValueLabel.setText(customer.getName());
        infoPanel.add(nameValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; phoneValueLabel.setText(customer.getPhone());
        infoPanel.add(phoneValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; emailValueLabel.setText(customer.getEmail());
        infoPanel.add(emailValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; ageValueLabel.setText(String.valueOf(customer.getAge()));
        infoPanel.add(ageValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; genderValueLabel.setText(customer.getGender());
        infoPanel.add(genderValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Preferred Meal:"), gbc);
        gbc.gridx = 1; preferredMealValueLabel.setText(customer.getPreferredMealType().getDisplayName());
        infoPanel.add(preferredMealValueLabel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; 
        statusValueLabel.setText(customer.isDeleted() ? "Removed" : "Active");
        statusValueLabel.setForeground(customer.isDeleted() ? new Color(200, 50, 50) : new Color(50, 150, 50));
        statusValueLabel.setFont(statusValueLabel.getFont().deriveFont(Font.BOLD, 12f));
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel bookingsPanel = new JPanel(new BorderLayout());
        bookingsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Customer Bookings", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 150)));

        setupBookingsTable();
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        bookingsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(bookingsPanel, BorderLayout.CENTER);

        // FIX: The constructor JPanel(FlowLayout) is undefined -> JPanel(new FlowLayout(...))
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); 
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        closeBtn.setBackground(new Color(100, 150, 200));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        bottomPanel.add(closeBtn);
        closeBtn.addActionListener(this);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void setupBookingsTable() {
        String[] columns = new String[]{
            "Booking Date", "Outbound Flight", "Return Flight", "Class", "Outbound Price (£)", "Return Price (£)", "Meal", "Cancellation Fee (£)"
        };

        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
                mealName = booking.getMeal().getName();
            }

            bookingsTableModel.addRow(new Object[]{
                booking.getBookingDate().format(dtf),
                outboundFlightDetails,
                returnFlightDetails,
                booking.getBookedClass().getClassName(),
                booking.getBookedPriceOutbound().toPlainString(),
                booking.getBookedPriceReturn().toPlainString(),
                mealName,
                booking.getCancellationFee() != null ? booking.getCancellationFee().toPlainString() : "N/A"
            });
        }

        bookingsTable = new JTable(bookingsTableModel);

        bookingsTable.setRowHeight(25);
        bookingsTable.setShowGrid(true);
        bookingsTable.setGridColor(new Color(220, 220, 220));
        bookingsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        bookingsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(new Color(230, 230, 240));
        bookingsTable.getTableHeader().setForeground(new Color(40, 40, 40));
        bookingsTable.getTableHeader().setReorderingAllowed(false);
        
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(90);

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
            this.dispose();
        }
    }
}