package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Meal; // Your Meal model
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ViewAllMealsWindow extends JFrame {

    private MainWindow mw;
    private JTable mealsTable;

    public ViewAllMealsWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("All Meals");
        setSize(800, 500);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());

        List<Meal> mealsList = mw.getFlightBookingSystem().getMeals(); // Assuming getMeals() in FBS

        String[] columns = new String[]{"ID", "Name", "Price", "Type", "Status"};

        Object[][] data = new Object[mealsList.size()][5];
        for (int i = 0; i < mealsList.size(); i++) {
            Meal meal = mealsList.get(i);
            data[i][0] = meal.getId();
            data[i][1] = meal.getName();
            data[i][2] = meal.getPrice();
            data[i][3] = meal.getType();
            data[i][4] = meal.isDeleted() ? "Removed" : "Active";
        }

        mealsTable = new JTable(data, columns);
        mealsTable.setFillsViewportHeight(true);
        mealsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        mealsTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        mealsTable.setRowHeight(20);
        mealsTable.setDefaultEditor(Object.class, null); // Disable editing

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        mealsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        mealsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Price

        add(new JScrollPane(mealsTable), BorderLayout.CENTER);

        // Optional: Add a button to view meal details if you implement ShowMeal.java
        // JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // JButton viewDetailsBtn = new JButton("View Details");
        // viewDetailsBtn.addActionListener(e -> {
        //     int selectedRow = mealsTable.getSelectedRow();
        //     if (selectedRow != -1) {
        //         int mealId = (int) mealsTable.getValueAt(selectedRow, 0);
        //         // new ShowMealDetailsWindow(mw, mealId); // You would create this
        //     } else {
        //         JOptionPane.showMessageDialog(this, "Please select a meal to view details.", "No Meal Selected", JOptionPane.WARNING_MESSAGE);
        //     }
        // });
        // buttonPanel.add(viewDetailsBtn);
        // add(buttonPanel, BorderLayout.SOUTH);


        setVisible(true);
    }
}