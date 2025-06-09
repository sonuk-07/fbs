package bcu.cmp5332.bookingsystem.gui;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ViewMealWindow extends JFrame implements ActionListener {

    private JTable mealsTable;
    private DefaultTableModel tableModel;
    private FlightBookingSystem fbs;
    private JPanel mainPanel;
    private MainWindow mw;
    private JLabel titleLabel;

    private JButton addMealBtn = new JButton("Add Meal");
    private JButton deleteMealBtn = new JButton("Delete Meal");
    private JButton showDetailsBtn = new JButton("Show Details");

    public ViewMealWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.PRIMARY_BLUE);
        headerPanel.setBorder(DesignConstants.HEADER_PANEL_BORDER);

        titleLabel = new JLabel("All Meals");
        titleLabel.setFont(DesignConstants.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Price (£)", "Meal Type", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int intcolumn) {
                return false;
            }
        };
        mealsTable = new JTable(tableModel);
        mealsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setupTableAppearance();

        JScrollPane scrollPane = new JScrollPane(mealsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(DesignConstants.SCROLL_PANE_BORDER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, DesignConstants.BUTTON_PANEL_H_GAP, DesignConstants.BUTTON_PANEL_V_GAP));
        buttonPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.BUTTON_PANEL_BOTTOM_PADDING, 0));

        customizeButton(addMealBtn);
        customizeButton(deleteMealBtn);
        customizeButton(showDetailsBtn);

        buttonPanel.add(addMealBtn);
        buttonPanel.add(deleteMealBtn);
        buttonPanel.add(showDetailsBtn);

        addMealBtn.addActionListener(this);
        deleteMealBtn.addActionListener(this);
        showDetailsBtn.addActionListener(this);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mealsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = mealsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        if (tableModel.getRowCount() > 0 && tableModel.getValueAt(selectedRow, 0) instanceof Integer) {
                            showSelectedMealDetails();
                        }
                    }
                }
            }
        });
    }

    private void setupTableAppearance() {
        mealsTable.setFont(DesignConstants.TABLE_ROW_FONT);
        mealsTable.getTableHeader().setFont(DesignConstants.TABLE_HEADER_FONT);
        mealsTable.setRowHeight(DesignConstants.TABLE_ROW_HEIGHT);
        mealsTable.setFillsViewportHeight(true);
        mealsTable.setForeground(DesignConstants.TEXT_DARK);

        mealsTable.setShowGrid(true);
        mealsTable.setGridColor(DesignConstants.TABLE_GRID_COLOR);
        mealsTable.setSelectionBackground(DesignConstants.TABLE_SELECTION_BACKGROUND);
        mealsTable.setSelectionForeground(Color.BLACK); // Set selected row foreground to black by default

        mealsTable.getTableHeader().setBackground(DesignConstants.PRIMARY_BLUE.brighter());
        mealsTable.getTableHeader().setForeground(DesignConstants.TEXT_DARK);
        mealsTable.getTableHeader().setReorderingAllowed(false);

        mealsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        mealsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        mealsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        mealsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        mealsTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Custom Cell Renderer for selected row styling
        DefaultTableCellRenderer boldBlackRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setForeground(Color.BLACK); // Make text black
                    c.setFont(c.getFont().deriveFont(Font.BOLD)); // Make text bold
                } else {
                    c.setForeground(DesignConstants.TEXT_DARK); // Default text color for non-selected rows
                    c.setFont(DesignConstants.TABLE_ROW_FONT); // Default font for non-selected rows
                }

                // Apply center alignment where needed (from previous code)
                if (column == 0 || column == 2 || column == 4) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                } else {
                    ((JLabel) c).setHorizontalAlignment(JLabel.LEFT); // Default left alignment for others
                }
                return c;
            }
        };

        // Apply the custom renderer to all columns
        for (int i = 0; i < mealsTable.getColumnCount(); i++) {
            mealsTable.getColumnModel().getColumn(i).setCellRenderer(boldBlackRenderer);
        }
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

    public JPanel getPanel() {
        return mainPanel;
    }

    public void displayMeals() {
        tableModel.setRowCount(0);

        List<Meal> meals = fbs.getAllMeals();

        if (meals.isEmpty()) {
            mealsTable.setFont(DesignConstants.TABLE_EMPTY_MESSAGE_FONT);
            mealsTable.setForeground(DesignConstants.TEXT_DARK.darker());
            mealsTable.clearSelection();
            tableModel.addRow(new Object[]{"", "", "", "No meals to display.", ""});
            return;
        }

        mealsTable.setFont(DesignConstants.TABLE_ROW_FONT);
        mealsTable.setForeground(DesignConstants.TEXT_DARK);

        for (Meal meal : meals) {
            Object[] rowData = {
                meal.getId(),
                meal.getName(),
                meal.getPrice(),
                meal.getType().getDisplayName(),
                meal.isDeleted() ? "Unavailable" : "Available"
            };
            tableModel.addRow(rowData);
        }
    }

    public void refreshView() {
        displayMeals();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addMealBtn) {
            new AddMealWindow(mw);
        } else if (ae.getSource() == deleteMealBtn) {
            deleteSelectedMeal();
        } else if (ae.getSource() == showDetailsBtn) {
            showSelectedMealDetails();
        }
    }

    private void deleteSelectedMeal() {
        int selectedRow = mealsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a meal to delete.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tableModel.getRowCount() > 0 && !(tableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
            JOptionPane.showMessageDialog(mainPanel, "Cannot delete 'No meals to display.' row.", "Invalid Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 4);
        if (status.equals("Removed")) {
            JOptionPane.showMessageDialog(mainPanel, "This meal has already been removed.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainPanel,
                                                     "Are you sure you want to delete this meal? This will mark it as 'Removed'.",
                                                     "Confirm Delete",
                                                     JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int mealId = (int) mealsTable.getValueAt(selectedRow, 0);
            try {
                Meal mealToDelete = fbs.getMealByIDIncludingDeleted(mealId);
                if (mealToDelete != null) {
                    mealToDelete.setDeleted(true);
                    FlightBookingSystemData.store(fbs);
                    JOptionPane.showMessageDialog(mainPanel, "Meal removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshView();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Meal not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error deleting meal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showSelectedMealDetails() {
        int selectedRow = mealsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Please select a meal to view details.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tableModel.getRowCount() > 0 && !(tableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
            JOptionPane.showMessageDialog(mainPanel, "No meal details to display for this row.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int mealId = (int) mealsTable.getValueAt(selectedRow, 0);
        try {
            Meal meal = fbs.getMealByIDIncludingDeleted(mealId);
            if (meal != null) {
                new MealDetailsWindow(meal);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Meal not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainPanel, "Error viewing meal details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void cleanup() {
        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
        }
        mainPanel = null;
        mealsTable = null;
        tableModel = null;
        fbs = null;
        mw = null;
        addMealBtn = null;
        deleteMealBtn = null;
        showDetailsBtn = null;
        System.out.println("ViewMealWindow cleaned up.");
    }
}