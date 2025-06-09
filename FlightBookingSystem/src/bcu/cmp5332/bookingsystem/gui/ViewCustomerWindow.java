package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ViewCustomerWindow implements ActionListener {

    private MainWindow mainWindow;
    private FlightBookingSystem fbs;
    private JPanel mainPanel;
    
    private JTable customersTable;
    private DefaultTableModel customersTableModel;
    private JLabel titleLabel;
    private JPopupMenu customerPopupMenu;
    private JMenuItem viewCustomerDetailsMenuItem;
    private JMenuItem removeCustomerMenuItem;
    
    private boolean showAllCustomers = false;

    public ViewCustomerWindow(MainWindow mainWindow, FlightBookingSystem fbs) {
        this.mainWindow = mainWindow;
        this.fbs = fbs;
        initializeComponents();
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(DesignConstants.MAIN_PANEL_H_GAP, DesignConstants.MAIN_PANEL_V_GAP));
        mainPanel.setBackground(DesignConstants.LIGHT_GRAY_BG);
        mainPanel.setBorder(DesignConstants.MAIN_PANEL_BORDER);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(DesignConstants.PRIMARY_BLUE);
        headerPanel.setBorder(DesignConstants.HEADER_PANEL_BORDER);
        
        titleLabel = new JLabel("Customers");
        titleLabel.setFont(DesignConstants.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = new String[]{
            "ID", "Name", "Phone", "Email", "Age", "Gender", "Preferred Meal", "Status"
        };
        
        customersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customersTable = new JTable(customersTableModel);
        setupTable();
        setupPopupMenu();
        
        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(DesignConstants.SCROLL_PANE_BORDER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTable() {
        customersTable.setFont(DesignConstants.TABLE_ROW_FONT);
        customersTable.getTableHeader().setFont(DesignConstants.TABLE_HEADER_FONT);
        customersTable.setRowHeight(DesignConstants.TABLE_ROW_HEIGHT);
        customersTable.setFillsViewportHeight(true);
        
        customersTable.setShowGrid(true);
        customersTable.setGridColor(DesignConstants.TABLE_GRID_COLOR);
        customersTable.setSelectionBackground(DesignConstants.TABLE_SELECTION_BACKGROUND);
        
        customersTable.getTableHeader().setBackground(DesignConstants.PRIMARY_BLUE.brighter());
        customersTable.getTableHeader().setForeground(DesignConstants.TEXT_DARK);
        customersTable.getTableHeader().setReorderingAllowed(false);
        
        customersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        customersTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        customersTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        customersTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        customersTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        customersTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        customersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        customersTable.getColumnModel().getColumn(7).setPreferredWidth(70);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        customersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        customersTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        customersTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        customersTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
    }

    private void setupPopupMenu() {
        customerPopupMenu = new JPopupMenu();
        viewCustomerDetailsMenuItem = new JMenuItem("View Details");
        removeCustomerMenuItem = new JMenuItem("Delete Customer");

        viewCustomerDetailsMenuItem.addActionListener(this);
        removeCustomerMenuItem.addActionListener(this);

        customerPopupMenu.add(viewCustomerDetailsMenuItem);
        customerPopupMenu.add(removeCustomerMenuItem);

        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = customersTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    customersTable.setRowSelectionInterval(row, row);
                }
                showCustomerPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showCustomerPopup(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && customersTable.getSelectedRow() != -1) {
                    if (customersTableModel.getRowCount() > 0 && customersTableModel.getValueAt(customersTable.getSelectedRow(), 0) instanceof Integer) {
                        viewSelectedCustomerDetails();
                    }
                }
            }

            private void showCustomerPopup(MouseEvent e) {
                if (e.isPopupTrigger() && customersTable.getSelectedRow() != -1) {
                    String currentStatus = (String) customersTableModel.getValueAt(customersTable.getSelectedRow(), 7);
                    removeCustomerMenuItem.setEnabled("Active".equals(currentStatus));
                    customerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public JPanel getPanel() {
        return this.mainPanel;
    }

    public void displayCustomers(boolean showAll) {
        this.showAllCustomers = showAll;
        
        customersTableModel.setRowCount(0);

        List<Customer> customersList;
        String tableTitle;

        if (showAllCustomers) {
            customersList = fbs.getAllCustomers();
            tableTitle = "All Customers (Including Deleted)";
        } else {
            customersList = fbs.getCustomers();
            tableTitle = "Active Customers";
        }

        if (customersList.isEmpty()) {
            customersTable.setFont(DesignConstants.TABLE_EMPTY_MESSAGE_FONT);
            customersTable.setForeground(DesignConstants.TEXT_DARK.darker());
            customersTable.clearSelection();
            customersTableModel.addRow(new Object[]{"", "", "", "No customers to display.", "", "", "", ""});
            titleLabel.setText(tableTitle);
            return;
        }
        
        customersTable.setFont(DesignConstants.TABLE_ROW_FONT);
        customersTable.setForeground(DesignConstants.TEXT_DARK);

        for (Customer customer : customersList) {
            String status = customer.isDeleted() ? "Removed" : "Active";
            customersTableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender(),
                customer.getPreferredMealType().getDisplayName(),
                status
            });
        }
        
        titleLabel.setText(tableTitle);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == viewCustomerDetailsMenuItem) {
            viewSelectedCustomerDetails();
        } else if (ae.getSource() == removeCustomerMenuItem) {
            removeSelectedCustomer();
        }
    }

    private void viewSelectedCustomerDetails() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainWindow, "Please select a customer to view details.", "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (customersTableModel.getRowCount() > 0 && !(customersTableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
            JOptionPane.showMessageDialog(mainWindow, "No customer details to display for this row.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int customerId = (int) customersTableModel.getValueAt(selectedRow, 0);
        try {
            Customer selectedCustomer = fbs.getCustomerByID(customerId);
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(mainWindow, "Customer details could not be retrieved.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new CustomerDetailsWindow(mainWindow, selectedCustomer);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(mainWindow, "Error viewing customer details: " + ex.getMessage(), "Customer Details Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainWindow, "An unexpected error occurred: " + ex.getMessage(), "Customer Details Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void removeSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainWindow, "No customer selected for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (customersTableModel.getRowCount() > 0 && !(customersTableModel.getValueAt(selectedRow, 0) instanceof Integer)) {
            JOptionPane.showMessageDialog(mainWindow, "Cannot delete 'No customers to display.' row.", "Invalid Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String status = (String) customersTableModel.getValueAt(selectedRow, 7);
        if (status.equals("Removed")) {
            JOptionPane.showMessageDialog(mainWindow, "This customer has already been removed.", "Already Removed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int customerIdToDelete = (int) customersTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) customersTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(mainWindow,
            "Are you sure you want to delete customer " + customerName + " (ID: " + customerIdToDelete + ")?\n" +
            "This will mark the customer as removed and they will no longer be visible in active lists.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean removed = fbs.removeCustomerById(customerIdToDelete);

                if (removed) {
                    FlightBookingSystemData.store(fbs);
                    
                    JOptionPane.showMessageDialog(mainWindow,
                        "Customer " + customerName + " (ID: " + customerIdToDelete + ") has been successfully marked as removed.",
                        "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                    
                    displayCustomers(showAllCustomers);
                } else {
                    JOptionPane.showMessageDialog(mainWindow,
                        "Customer with ID " + customerIdToDelete + " was not found or could not be removed.",
                        "Deletion Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FlightBookingSystemException | IOException ex) {
                JOptionPane.showMessageDialog(mainWindow, "Error deleting customer: " + ex.getMessage(), "Deletion Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow, "An unexpected error occurred during deletion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

	public void refreshView() {
		displayCustomers(showAllCustomers);
	}

	public void cleanup() {
        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
        }
        mainPanel = null;
        customersTable = null;
        customersTableModel = null;
        fbs = null;
        mainWindow = null;
        System.out.println("ViewCustomerWindow cleaned up.");
	}

	public boolean isVisible() {
		return mainPanel != null && mainPanel.isShowing();
	}
}