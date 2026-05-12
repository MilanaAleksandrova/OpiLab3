package org.milana.weblab3.utils;

import org.milana.weblab3.beans.Coordinates;

import java.math.BigDecimal;

public class Validation {
    static public boolean validate(Coordinates coordinates) {
        return validateValue(coordinates.getX(), Bounds.X_BOUNDS) &&
                validateValue(coordinates.getY(), Bounds.Y_BOUNDS) &&
                validateValue(coordinates.getR(), Bounds.R_BOUNDS);
    }

    static public boolean validateValue(BigDecimal value, Bounds bounds) {
        if (value.compareTo(bounds.getLeft()) > 0 && value.compareTo(bounds.getRight()) < 0) return true;
        return bounds.isInclusive() && (value.compareTo(bounds.getLeft()) == 0) || (value.compareTo(bounds.getRight()) == 0);
    }
}
