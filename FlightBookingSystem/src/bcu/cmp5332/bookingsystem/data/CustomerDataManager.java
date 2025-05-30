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
                String[] properties = line.split(SEPARATOR, -1); // Use -1 to keep trailing empty strings

                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    // --- NEW: Parse Age and Gender ---
                    int age = Integer.parseInt(properties[4]); // Age is at index 4
                    String gender = properties[5];             // Gender is at index 5

                    // isDeleted is now at index 6 if all fields are present
                    boolean isDeleted = false;
                    if (properties.length > 6) { // Check if the 'isDeleted' property exists in the line (now at index 6)
                        isDeleted = Boolean.parseBoolean(properties[6]);
                    }

                    // --- UPDATED Customer CONSTRUCTOR CALL ---
                    Customer customer = new Customer(id, name, phone, email, age, gender);
                    customer.setDeleted(isDeleted);
                    fbs.addCustomer(customer);

                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer id/age on line " + line_idx + ": " + line, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new FlightBookingSystemException("Malformed line at index " + line_idx + " in customer data (missing fields): " + line, ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getAllCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                // --- NEW: Store Age and Gender ---
                out.print(customer.getAge() + SEPARATOR);
                out.print(customer.getGender() + SEPARATOR);
                out.print(customer.isDeleted()); // Store the 'isDeleted' status
                out.println(); // Move to the next line for the next customer
            }
        }
    }
}
