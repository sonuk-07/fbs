package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class MealTest {

    @Test
    void testConstructorAndGetters() {
        Meal meal = new Meal(1, "Paneer Tikka", "Spicy Indian dish", new BigDecimal("12.50"), MealType.VEG);

        assertEquals(1, meal.getId());
        assertEquals("Paneer Tikka", meal.getName());
        assertEquals("Spicy Indian dish", meal.getDescription());
        assertEquals(new BigDecimal("12.50"), meal.getPrice());
        assertEquals(MealType.VEG, meal.getType());
        assertFalse(meal.isDeleted());
    }

    @Test
    void testSetters() {
        Meal meal = new Meal(1, "Temp", "Temp", new BigDecimal("0.00"), MealType.NONE);

        meal.setId(2);
        meal.setName("Chicken Curry");
        meal.setDescription("Delicious chicken curry");
        meal.setPrice(new BigDecimal("10.00"));
        meal.setType(MealType.NON_VEG);
        meal.setDeleted(true);

        assertEquals(2, meal.getId());
        assertEquals("Chicken Curry", meal.getName());
        assertEquals("Delicious chicken curry", meal.getDescription());
        assertEquals(new BigDecimal("10.00"), meal.getPrice());
        assertEquals(MealType.NON_VEG, meal.getType());
        assertTrue(meal.isDeleted());
    }

    @Test
    void testGetDetailsShort() {
        Meal meal = new Meal(1, "Vegan Bowl", "Healthy vegan meal", new BigDecimal("8.00"), MealType.VEGAN);
        assertEquals("1: Vegan Bowl (Vegan) (£8.00)", meal.getDetailsShort());
    }

    @Test
    void testGetDetailsLong() {
        Meal meal = new Meal(1, "Gluten-Free Pasta", "Italian pasta without gluten", new BigDecimal("9.50"), MealType.GLUTEN_FREE);
        String details = meal.getDetailsLong();

        assertTrue(details.contains("Meal ID: 1"));
        assertTrue(details.contains("Name: Gluten-Free Pasta"));
        assertTrue(details.contains("Description: Italian pasta without gluten"));
        assertTrue(details.contains("Price: £9.50"));
        assertTrue(details.contains("Type: Gluten-Free"));
        assertFalse(details.contains("DELETED"));

        meal.setDeleted(true);
        assertTrue(meal.getDetailsLong().contains("(DELETED)"));
    }
}
