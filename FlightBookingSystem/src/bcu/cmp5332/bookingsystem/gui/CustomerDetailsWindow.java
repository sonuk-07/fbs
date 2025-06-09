package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.ETCHED_BORDER, "Customer Information", TitledBorder.LEFT, TitledBorder.TOP,
            DesignConstants.TITLED_BORDER_FONT, DesignConstants.INFO_PANEL_TITLE_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H, DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Customer ID:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; customerIdValueLabel.setText(String.valueOf(customer.getId()));
        customerIdValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(customerIdValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Name:", true), gbc);
        gbc.gridx = 1; nameValueLabel.setText(customer.getName());
        nameValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(nameValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Phone:", true), gbc);
        gbc.gridx = 1; phoneValueLabel.setText(customer.getPhone());
        phoneValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(phoneValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Email:", true), gbc);
        gbc.gridx = 1; emailValueLabel.setText(customer.getEmail());
        emailValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(emailValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Age:", true), gbc);
        gbc.gridx = 1; ageValueLabel.setText(String.valueOf(customer.getAge()));
        ageValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(ageValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Gender:", true), gbc);
        gbc.gridx = 1; genderValueLabel.setText(customer.getGender());
        genderValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(genderValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Preferred Meal:", true), gbc);
        gbc.gridx = 1; preferredMealValueLabel.setText(customer.getPreferredMealType().getDisplayName());
        preferredMealValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(preferredMealValueLabel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Status:", true), gbc);
        gbc.gridx = 1; 
        statusValueLabel.setText(customer.isDeleted() ? "Removed" : "Active");
        statusValueLabel.setForeground(customer.isDeleted() ? DesignConstants.STATUS_CANCELLED_RED : DesignConstants.STATUS_ACTIVE_GREEN);
        statusValueLabel.setFont(DesignConstants.STATUS_FONT);
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel bookingsPanel = new JPanel(new BorderLayout());
        bookingsPanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.ETCHED_BORDER, "Customer Bookings", TitledBorder.LEFT, TitledBorder.TOP,
            DesignConstants.TITLED_BORDER_FONT, DesignConstants.INFO_PANEL_TITLE_COLOR));

        setupBookingsTable();
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        bookingsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(bookingsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP)); 
        customizeButton(closeBtn, DesignConstants.CLOSE_BUTTON_BG, DesignConstants.CLOSE_BUTTON_HOVER, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.BUTTON_TEXT_COLOR);
        bottomPanel.add(closeBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private JLabel createLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setForeground(DesignConstants.TEXT_DARK);
        label.setFont(isBold ? DesignConstants.INFO_LABEL_FONT.deriveFont(Font.BOLD) : DesignConstants.INFO_LABEL_FONT);
        return label;
    }

    private void customizeButton(JButton button, Color bgColor, Color hoverColor, javax.swing.border.Border border, Color textColor) {
        button.setFont(DesignConstants.BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(border);
        button.addActionListener(this);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
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

        bookingsTable.setRowHeight(DesignConstants.TABLE_ROW_HEIGHT);
        bookingsTable.setShowGrid(true);
        bookingsTable.setGridColor(DesignConstants.TABLE_GRID_COLOR);
        bookingsTable.setFont(DesignConstants.TABLE_ROW_FONT);
        
        bookingsTable.getTableHeader().setFont(DesignConstants.TABLE_HEADER_FONT);
        bookingsTable.getTableHeader().setBackground(DesignConstants.TABLE_HEADER_BG_COLOR);
        bookingsTable.getTableHeader().setForeground(DesignConstants.TABLE_HEADER_FOREGROUND_COLOR);
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