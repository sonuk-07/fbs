package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Meal; // Your Meal model
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class SearchMealsWindow extends JFrame implements ActionListener {
    private MainWindow mw;
    private JTextField searchTermField;
    private JTable searchResultsTable;
    private JButton searchButton = new JButton("Search");
    private JButton closeButton = new JButton("Close");

    public SearchMealsWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Search Meals");
        setSize(700, 500);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());

        // Search Panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Search Term (Name/Type):"));
        searchTermField = new JTextField(20);
        searchPanel.add(searchTermField);
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Results Table in the center
        searchResultsTable = new JTable(); // Initialize empty table
        searchResultsTable.setFillsViewportHeight(true);
        searchResultsTable.setDefaultEditor(Object.class, null); // Make non-editable
        add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);

        // Button Panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            performSearch();
        } else if (e.getSource() == closeButton) {
            dispose();
        }
    }

    private void performSearch() {
        String searchTerm = searchTermField.getText().trim().toLowerCase();
        List<Meal> allMeals = mw.getFlightBookingSystem().getMeals(); // Get all meals

        List<Meal> filteredMeals = allMeals.stream()
                .filter(meal -> meal.getName().toLowerCase().contains(searchTerm) ||
                                meal.getType().toString().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        updateTable(filteredMeals);
    }

    private void updateTable(List<Meal> meals) {
        String[] columns = new String[]{"ID", "Name", "Price", "Type", "Status"};
        Object[][] data = new Object[meals.size()][5];

        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            data[i][0] = meal.getId();
            data[i][1] = meal.getName();
            data[i][2] = meal.getPrice();
            data[i][3] = meal.getType();
            data[i][4] = meal.isDeleted() ? "Removed" : "Active";
        }

        searchResultsTable.setModel(new JTable(data, columns).getModel()); // Update table model

        // Apply rendering again as model changed
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        searchResultsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        searchResultsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Price

        searchResultsTable.revalidate();
        searchResultsTable.repaint();
    }
}