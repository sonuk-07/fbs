package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Meal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MealDetailsWindow extends JFrame implements ActionListener {

    private final Meal meal;

    private JLabel idValueLabel = new JLabel();
    private JLabel nameValueLabel = new JLabel();
    private JLabel descriptionValueLabel = new JLabel();
    private JLabel priceValueLabel = new JLabel();
    private JLabel typeValueLabel = new JLabel();
    private JLabel statusValueLabel = new JLabel();

    private JButton closeBtn = new JButton("Close");

    public MealDetailsWindow(Meal meal) {
        this.meal = meal;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Failed to set LookAndFeel: " + ex);
        }

        setTitle("Meal Details: " + meal.getName());
        setSize(450, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            DesignConstants.ETCHED_BORDER, "Meal Information", TitledBorder.LEFT, TitledBorder.TOP,
            DesignConstants.TITLED_BORDER_FONT, DesignConstants.INFO_PANEL_TITLE_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H, DesignConstants.INFO_PANEL_INSET_GAP_V, DesignConstants.INFO_PANEL_INSET_GAP_H);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("ID:", true), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; idValueLabel.setText(String.valueOf(meal.getId()));
        idValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(idValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Name:", true), gbc);
        gbc.gridx = 1; nameValueLabel.setText(meal.getName());
        nameValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(nameValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Description:", true), gbc);
        gbc.gridx = 1; descriptionValueLabel.setText(meal.getDescription());
        descriptionValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(descriptionValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Price:", true), gbc);
        gbc.gridx = 1; priceValueLabel.setText("£" + meal.getPrice().toPlainString());
        priceValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(priceValueLabel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Meal Type:", true), gbc);
        gbc.gridx = 1; typeValueLabel.setText(meal.getType().getDisplayName());
        typeValueLabel.setFont(DesignConstants.INFO_VALUE_FONT);
        infoPanel.add(typeValueLabel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(createLabel("Status:", true), gbc);
        gbc.gridx = 1; 
        statusValueLabel.setText(meal.isDeleted() ? "Removed" : "Active");
        statusValueLabel.setForeground(meal.isDeleted() ? DesignConstants.STATUS_CANCELLED_RED : DesignConstants.STATUS_ACTIVE_GREEN);
        statusValueLabel.setFont(DesignConstants.STATUS_FONT);
        infoPanel.add(statusValueLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        // Use DesignConstants.BUTTON_TEXT_COLOR for closeBtn
        customizeButton(closeBtn, DesignConstants.CLOSE_BUTTON_BG, DesignConstants.CLOSE_BUTTON_HOVER, DesignConstants.BUTTON_BEVEL_PADDING_BORDER, DesignConstants.BUTTON_TEXT_COLOR);
        bottomPanel.add(closeBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            this.dispose();
        }
    }
}