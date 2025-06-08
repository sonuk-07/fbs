package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Meal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MealDetailsWindow extends JFrame implements ActionListener { // Implement ActionListener for consistency

    private final Meal meal;

    // Labels for meal details
    private JLabel idValueLabel = new JLabel();
    private JLabel nameValueLabel = new JLabel();
    private JLabel descriptionValueLabel = new JLabel();
    private JLabel priceValueLabel = new JLabel();
    private JLabel typeValueLabel = new JLabel();
    private JLabel statusValueLabel = new JLabel();

    private JButton closeBtn = new JButton("Close"); // Add a close button

    public MealDetailsWindow(Meal meal) {
        this.meal = meal;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle look and feel exception
            System.err.println("Failed to set LookAndFeel: " + ex);
        }

        setTitle("Meal Details: " + meal.getName());
        setSize(450, 300); // Slightly increased size
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Meal Information Panel ---
        JPanel infoPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Meal Information", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 150)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        // ID
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; idValueLabel.setText(String.valueOf(meal.getId()));
        infoPanel.add(idValueLabel, gbc);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; nameValueLabel.setText(meal.getName());
        infoPanel.add(nameValueLabel, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; descriptionValueLabel.setText(meal.getDescription());
        infoPanel.add(descriptionValueLabel, gbc);
        row++;

        // Price
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; priceValueLabel.setText("£" + meal.getPrice().toPlainString());
        infoPanel.add(priceValueLabel, gbc);
        row++;

        // Meal Type
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1; typeValueLabel.setText(meal.getType().getDisplayName());
        infoPanel.add(typeValueLabel, gbc);
        row++;
        
        // Status
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; 
        statusValueLabel.setText(meal.isDeleted() ? "Removed" : "Active");
        statusValueLabel.setForeground(meal.isDeleted() ? new Color(200, 50, 50) : new Color(50, 150, 50));
        statusValueLabel.setFont(statusValueLabel.getFont().deriveFont(Font.BOLD, 12f));
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // --- Bottom Panel with Close Button ---
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
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            this.dispose();
        }
    }
}