package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.util.Scanner;

public class CustomerDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/customers.txt";
    private final String SEPARATOR = "::";


    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    // Ensure that the 'isDeleted' property is read from the file.
                    // If the line doesn't have this property (e.g., old data), default to false.
                    boolean isDeleted = false;
                    if (properties.length > 4) { // Check if the 'isDeleted' property exists in the line
                        isDeleted = Boolean.parseBoolean(properties[4]);
                    }

                    Customer customer = new Customer(id, name, phone, email);
                    customer.setDeleted(isDeleted); // Set the loaded 'isDeleted' status
                    fbs.addCustomer(customer);

                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer id on line " + line_idx, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // This catch block handles cases where a line might be malformed
                    // or doesn't have enough properties (e.g., missing email or isDeleted)
                    throw new FlightBookingSystemException("Malformed line at index " + line_idx + ": " + line, ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            // IMPORTANT CHANGE: Iterate over getAllCustomers() to include soft-deleted customers
            // This ensures that the 'isDeleted' status is persisted for all customers.
            for (Customer customer : fbs.getAllCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                out.print(customer.isDeleted()); // Store the 'isDeleted' status
                out.println(); // Move to the next line for the next customer
            }
        }
    }
}
