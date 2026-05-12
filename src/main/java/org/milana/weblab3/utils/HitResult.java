package org.milana.weblab3.utils;

import jakarta.persistence.*;
import lombok.*;
import org.milana.weblab3.beans.Coordinates;

import java.io.Serializable;
import java.math.BigDecimal;


@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hitResults")
public class HitResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    private Long id;
    private String sessionId;

    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal r;
    private String currentTime;
    private boolean result;

    private boolean removed = false;

    public HitResult(String sessionId,
                     Coordinates coordinates, String currentTime, boolean result) {
        this.sessionId = sessionId;
        this.x = coordinates.getX();
        this.y = coordinates.getY();
        this.r = coordinates.getR();
        this.currentTime = currentTime;
        this.result = result;
    }
}
