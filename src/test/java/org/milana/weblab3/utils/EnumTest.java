package org.milana.weblab3.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BoundsTest {

    @Test
    void testXBounds() {
        assertEquals(BigDecimal.valueOf(-4), Bounds.X_BOUNDS.getLeft());
        assertEquals(BigDecimal.valueOf(4), Bounds.X_BOUNDS.getRight());
    }

    @Test
    void testInclusive() {
        assertTrue(Bounds.R_BOUNDS.isInclusive());
    }
}