package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class CustomerDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/customers.txt";

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

                    Customer customer = new Customer(id, name, phone);
                    fbs.addCustomer(customer);

                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer id on line " + line_idx, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new FlightBookingSystemException("Malformed line at index " + line_idx + ": " + line, ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(LocalDateTime.now().withSecond(0).withNano(0) + SEPARATOR);  // Timestamp
                out.println(); // end of line
            }
        }
    }

}
