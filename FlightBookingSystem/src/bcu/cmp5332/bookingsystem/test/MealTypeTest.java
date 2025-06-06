package bcu.cmp5332.bookingsystem.test;

import org.junit.jupiter.api.Test;

import bcu.cmp5332.bookingsystem.model.MealType;

import static org.junit.jupiter.api.Assertions.*;

class MealTypeTest {

    @Test
    void testDisplayName() {
        assertEquals("Vegetarian", MealType.VEG.getDisplayName());
        assertEquals("Non-Vegetarian", MealType.NON_VEG.getDisplayName());
        assertEquals("Vegan", MealType.VEGAN.getDisplayName());
        assertEquals("Gluten-Free", MealType.GLUTEN_FREE.getDisplayName());
        assertEquals("None", MealType.NONE.getDisplayName());
    }
}
