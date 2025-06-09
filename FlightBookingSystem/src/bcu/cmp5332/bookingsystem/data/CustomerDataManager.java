package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.MealType;

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

                if (properties.length < 7) {
                    throw new FlightBookingSystemException("Malformed customer line at " + line_idx + ": " + line + " (Too few fields)");
                }

                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    int age = Integer.parseInt(properties[4]);
                    String gender = properties[5];
                    MealType preferredMealType = MealType.valueOf(properties[6].toUpperCase());

                    boolean isDeleted = false;
                    if (properties.length > 7) {
                        isDeleted = Boolean.parseBoolean(properties[7]);
                    }

                    Customer customer = new Customer(id, name, phone, email, age, gender, preferredMealType);
                    customer.setDeleted(isDeleted);
                    fbs.addCustomer(customer);

                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer ID/age on line " + line_idx + ": " + ex.getMessage(), ex);
                } catch (IllegalArgumentException ex) {
                    throw new FlightBookingSystemException("Invalid preferred meal type on line " + line_idx + ": " + ex.getMessage(), ex);
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
                out.print(customer.getAge() + SEPARATOR);
                out.print(customer.getGender() + SEPARATOR);
                out.print(customer.getPreferredMealType().name() + SEPARATOR);
                out.print(customer.isDeleted());
                out.println();
            }
        }
    }
}