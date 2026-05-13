package org.milana.weblab3.utils;

import org.junit.jupiter.api.Test;
import org.milana.weblab3.beans.Coordinates;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;


public class ValidationTest {
    @Test
    void testValidCoordinates() {
        Coordinates coordinates = new Coordinates(
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.valueOf(3)
        );

        assertTrue(Validation.validate(coordinates));
    }

    @Test
    void testInvalidX() {
        Coordinates coordinates = new Coordinates(
                BigDecimal.valueOf(100),
                BigDecimal.ZERO,
                BigDecimal.valueOf(3)
        );

        assertFalse(Validation.validate(coordinates));
    }

    @Test
    void testInvalidY() {
        Coordinates coordinates = new Coordinates(
                BigDecimal.ONE,
                BigDecimal.valueOf(-100),
                BigDecimal.valueOf(3)
        );

        assertFalse(Validation.validate(coordinates));
    }

    @Test
    void testInvalidR() {
        Coordinates coordinates = new Coordinates(
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.valueOf(100)
        );

        assertFalse(Validation.validate(coordinates));
    }
    @Test
    void testLeftBorderInclusive() {
        assertTrue(
                Validation.validateValue(
                        BigDecimal.valueOf(-4),
                        Bounds.X_BOUNDS
                )
        );
    }

    @Test
    void testOutsideBorder() {
        assertFalse(
                Validation.validateValue(
                        BigDecimal.valueOf(6),
                        Bounds.R_BOUNDS
                )
        );
    }
}
