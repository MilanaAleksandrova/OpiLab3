package org.milana.weblab3.beans;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates implements Serializable {
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal r = BigDecimal.ONE;
}
