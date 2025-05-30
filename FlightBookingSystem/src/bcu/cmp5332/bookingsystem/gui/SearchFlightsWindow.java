package bcu.cmp5332.bookingsystem.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchFlightsWindow extends JFrame implements ActionListener {
    private MainWindow mw;
    private JTextField searchTermField;
    private JButton searchButton;

    public SearchFlightsWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("Search Flights");
        setSize(400, 200);
        setLocationRelativeTo(mw);
        setLayout(new FlowLayout());

        add(new JLabel("Search Term:"));
        searchTermField = new JTextField(20);
        add(searchTermField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        add(searchButton);

        // This is where search results would be displayed, e.g., in a JTable below
        // JTable searchResultsTable;
        // add(new JScrollPane(searchResultsTable));

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String searchTerm = searchTermField.getText();
            JOptionPane.showMessageDialog(this, "Searching for flights with term: " + searchTerm, "Search Initiated", JOptionPane.INFORMATION_MESSAGE);
            // TODO: Implement actual search logic using a SearchFlights command and display results
        }
    }
}
