package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // Make sure this is imported for FlowLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ViewMealWindow extends JFrame implements ActionListener {

    private JTable mealsTable;
    private DefaultTableModel tableModel;
    private FlightBookingSystem fbs;
    private JPanel mainPanel;
    private MainWindow mw;

    private JButton addMealBtn = new JButton("Add Meal");
    private JButton deleteMealBtn = new JButton("Delete Meal");
    private JButton showDetailsBtn = new JButton("Show Details");

    // The 'showAllMeals' boolean is no longer needed as we always show all.
    // private boolean showAllMeals = false;

    public ViewMealWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        mainPanel = new JPanel(new BorderLayout());

        // Ensure this part is correct for JPanel and FlowLayout
        // FIX: The constructor JPanel(FlowLayout) is undefined -> JPanel(new FlowLayout(...))
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Confirmed correct usage

        String[] columnNames = {"ID", "Name", "Price (£)", "Meal Type", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        mealsTable = new JTable(tableModel);
        mealsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(mealsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Button Panel ---
        buttonPanel.add(addMealBtn);
        buttonPanel.add(deleteMealBtn);
        buttonPanel.add(showDetailsBtn);

        addMealBtn.addActionListener(this);
        deleteMealBtn.addActionListener(this);
        showDetailsBtn.addActionListener(this);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    // Modified: Removed the 'showAll' parameter
    public void displayMeals() {
        tableModel.setRowCount(0); // Clear existing data

        // Always get all meals, including deleted ones
        List<Meal> meals = fbs.getAllMeals(); // <--- Key Change

        if (meals.isEmpty()) {
            // Optional: Display a message if no meals exist at all
            // JOptionPane.showMessageDialog(this, "No meals in the system.", "No Meals", JOptionPane.INFORMATION_MESSAGE);
            return; // Exit if there are no meals to display
        }

        for (Meal meal : meals) {
            Object[] rowData = {
                meal.getId(),
                meal.getName(),
                meal.getPrice(),
                meal.getType().getDisplayName(),
                meal.isDeleted() ? "Removed" : "Active" // Display status accurately
            };
            tableModel.addRow(rowData);
        }
    }

    public void refreshView() {
        displayMeals(); // Call the updated displayMeals without parameters
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addMealBtn) {
            new AddMealWindow(mw); // Open Add Meal window
        } else if (ae.getSource() == deleteMealBtn) {
            deleteSelectedMeal();
        } else if (ae.getSource() == showDetailsBtn) {
            showSelectedMealDetails();
        }
    }

    private void deleteSelectedMeal() {
        int selectedRow = mealsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal to delete.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                                                    "Are you sure you want to delete this meal? This action cannot be undone.",
                                                    "Confirm Delete",
                                                    JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int mealId = (int) mealsTable.getValueAt(selectedRow, 0);
            try {
                Meal mealToDelete = fbs.getMealByIDIncludingDeleted(mealId); // Use getMealByIDIncludingDeleted
                if (mealToDelete != null) {
                    if (mealToDelete.isDeleted()) {
                        JOptionPane.showMessageDialog(this, "Meal is already removed.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    mealToDelete.setDeleted(true); // Mark as deleted
                    FlightBookingSystemData.store(fbs); // Save changes
                    JOptionPane.showMessageDialog(this, "Meal removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshView(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Meal not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting meal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSelectedMealDetails() {
        int selectedRow = mealsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal to view details.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mealId = (int) mealsTable.getValueAt(selectedRow, 0);
        try {
            // Changed to getMealByIDIncludingDeleted, as you'll be viewing all meals
            Meal meal = fbs.getMealByIDIncludingDeleted(mealId);
            if (meal != null) {
                new MealDetailsWindow(meal); // Open details window
            } else {
                JOptionPane.showMessageDialog(this, "Meal not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) { // Catching a broader exception type as getMealByIDIncludingDeleted doesn't throw FBSException
            JOptionPane.showMessageDialog(this, "Error viewing meal details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cleanup() {
        // No specific resources to clean up for this view
    }
}