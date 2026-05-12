package org.milana.weblab3.utils;

import org.milana.weblab3.beans.Coordinates;

import java.math.BigDecimal;

public class AreaCheck {

    BigDecimal zero = BigDecimal.ZERO;

    public boolean isHit(Coordinates coordinates){
        return coordinates != null && isHit(coordinates.getX(), coordinates.getY(), coordinates.getR());
    }

    public boolean isHit(BigDecimal x, BigDecimal y, BigDecimal r){
        return isCircleHit(x,y,r) || isRectangleHit(x,y,r) || isTriangleHit(x,y,r);
    }

    public boolean isCircleHit(BigDecimal x, BigDecimal y, BigDecimal r){
            BigDecimal x2 = x.multiply(x);
            BigDecimal y2 = y.multiply(y);
            BigDecimal r2 = r.multiply(r).divide(BigDecimal.valueOf(4));
        return (x.compareTo(zero) >= 0 && y.compareTo(zero) <= 0) && (x2.add(y2).compareTo(r2) <= 0);
    }

    public boolean isRectangleHit(BigDecimal x, BigDecimal y, BigDecimal r){
        return (x.compareTo(zero) <= 0 && y.compareTo(zero) >= 0) && (x.compareTo(r.negate()) >= 0 && y.compareTo(r) <= 0);
    }

    public boolean isTriangleHit(BigDecimal x, BigDecimal y, BigDecimal r){
            BigDecimal twoX = x.multiply(BigDecimal.valueOf(-2));
            BigDecimal expr = twoX.add(r);
        return (x.compareTo(zero) >= 0 && y.compareTo(zero) >= 0) && y.compareTo(expr) <= 0;
    }
}
