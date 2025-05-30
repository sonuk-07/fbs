package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.RemoveMeal; // Your command
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Meal; // Your Meal model

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveMealWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTable mealTable;
    private JButton removeBtn = new JButton("Remove Selected Meal");
    private JButton cancelBtn = new JButton("Cancel");

    public RemoveMealWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Remove Meal");
        setSize(700, 400);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Select Meal to Remove"));

        displayMealsInTable();
        if (mealTable != null) {
            tablePanel.add(new JScrollPane(mealTable), BorderLayout.CENTER);
        } else {
            tablePanel.add(new JLabel("No meals to display or an error occurred.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void displayMealsInTable() {
        List<Meal> mealsList = mw.getFlightBookingSystem().getMeals(); // Assuming getMeals()
        List<Meal> activeMeals = mealsList.stream()
                                          .filter(m -> !m.isDeleted()) // Assuming Meal has isDeleted()
                                          .collect(Collectors.toList());

        String[] columns = new String[]{"ID", "Name", "Price", "Type", "Status"};

        Object[][] data = new Object[activeMeals.size()][5];
        for (int i = 0; i < activeMeals.size(); i++) {
            Meal meal = activeMeals.get(i);
            data[i][0] = meal.getId(); // Assuming Meal has getId()
            data[i][1] = meal.getName(); // Assuming Meal has getName()
            data[i][2] = meal.getPrice(); // Assuming Meal has getPrice()
            data[i][3] = meal.getType(); // Assuming Meal has getMealType()
            data[i][4] = meal.isDeleted() ? "Removed" : "Active";
        }

        mealTable = new JTable(data, columns);
        mealTable.setFillsViewportHeight(true);
        mealTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        mealTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        mealTable.setRowHeight(20);
        mealTable.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        mealTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        mealTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Price
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removeBtn) {
            removeMeal();
        } else if (e.getSource() == cancelBtn) {
            dispose();
        }
    }

    private void removeMeal() {
        int selectedRow = mealTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meal from the table to remove.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mealIdToRemove = (int) mealTable.getValueAt(selectedRow, 0); // Get ID from first column
        String mealNameDisplay = mealTable.getValueAt(selectedRow, 1).toString(); // Get Name for display

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove Meal '" + mealNameDisplay + "' (ID: " + mealIdToRemove + ")?\n" +
            "This action cannot be undone.",
            "Confirm Meal Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Command removeMealCommand = new RemoveMeal(mealIdToRemove); // Your RemoveMeal command
                removeMealCommand.execute(mw.getFlightBookingSystem(), null);

                JOptionPane.showMessageDialog(this, "Meal '" + mealNameDisplay + "' removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // Optionally, refresh the view meals window or open it again
                // new ViewAllMealsWindow(mw);
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Removing Meal", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}