package org.milana.weblab3.utils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AreaCheckTest {
    private final AreaCheck areaCheck = new AreaCheck();

    @Test
    public void testCircleHit() {
        boolean result = areaCheck.isCircleHit(
                BigDecimal.ONE,
                BigDecimal.valueOf(-1),
                BigDecimal.valueOf(4)
        );
        assertTrue(result);
    }
    @Test
    void testRectangleHit() {
        boolean result = areaCheck.isRectangleHit(
                BigDecimal.valueOf(-1),
                BigDecimal.ONE,
                BigDecimal.valueOf(3)
        );

        assertTrue(result);
    }

    @Test
    void testTriangleHit() {
        boolean result = areaCheck.isTriangleHit(
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.valueOf(5)
        );

        assertTrue(result);
    }

    @Test
    void testMiss() {
        boolean result = areaCheck.isHit(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.ONE
        );

        assertFalse(result);
    }
    @Test
    void testPointOnCircleBorder() {
        boolean result = areaCheck.isCircleHit(
                BigDecimal.ZERO,
                BigDecimal.valueOf(-2),
                BigDecimal.valueOf(4)
        );

        assertTrue(result);
    }
}
