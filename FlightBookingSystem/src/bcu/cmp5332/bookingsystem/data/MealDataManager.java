package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;

public class MealDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/meals.txt";
    private final String SEPARATOR = "::";

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);

                if (properties.length < 5) {
                    throw new FlightBookingSystemException("Malformed meal line at " + line_idx + ": " + line + " (Too few fields for basic meal properties)");
                }

                try {
                    int id = Integer.parseInt(properties[0].trim());
                    String name = properties[1].trim();
                    String description = properties[2].trim();
                    BigDecimal price = new BigDecimal(properties[3].trim());
                    MealType type = MealType.valueOf(properties[4].trim().toUpperCase());

                    boolean isDeleted = false;
                    if (properties.length > 5) {
                        isDeleted = Boolean.parseBoolean(properties[5].trim());
                    }

                    Meal meal = new Meal(id, name, description, price, type);
                    meal.setDeleted(isDeleted);
                    fbs.addMeal(meal);

                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse meal ID/price on line " + line_idx + ": " + ex.getMessage(), ex);
                } catch (IllegalArgumentException ex) {
                    throw new FlightBookingSystemException("Invalid meal type on line " + line_idx + ": " + ex.getMessage(), ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new FlightBookingSystemException("Malformed line at index " + line_idx + " in meal data (missing fields): " + line, ex);
                }
                line_idx++;
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Meal meal : fbs.getAllMeals()) {
                out.print(meal.getId() + SEPARATOR);
                out.print(meal.getName() + SEPARATOR);
                out.print(meal.getDescription() + SEPARATOR);
                out.print(meal.getPrice().toPlainString() + SEPARATOR);
                out.print(meal.getType().name() + SEPARATOR);
                out.print(meal.isDeleted());
                out.println();
            }
        }
    }
}