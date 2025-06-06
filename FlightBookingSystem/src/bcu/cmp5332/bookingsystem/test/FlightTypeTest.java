package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.FlightType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlightTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(FlightType.BUDGET, FlightType.valueOf("BUDGET"));
        assertEquals(FlightType.COMMERCIAL, FlightType.valueOf("COMMERCIAL"));
    }

    @Test
    void testEnumCount() {
        assertEquals(2, FlightType.values().length);
    }
}
