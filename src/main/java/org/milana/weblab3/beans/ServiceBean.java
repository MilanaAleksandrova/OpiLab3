package org.milana.weblab3.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.milana.weblab3.utils.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@Named("service")
@ApplicationScoped
public class ServiceBean implements Serializable {
    private final AreaCheck areaCheck;
    private final DataBaseController dbc;

    public ServiceBean() {
        areaCheck = new AreaCheck();
        dbc = new DataBaseController();
    }

    public LinkedList<HitResult> getUserHits(String sessionId) {
        if (sessionId == null) return new LinkedList<>();

        List<HitResult> hits =dbc.getUserHits(sessionId);

        return hits != null ? new LinkedList<>(hits) : new LinkedList<>();
    }

    public HitResult processRequest(String sessionId, Coordinates coordinates) {
        return processRequest(sessionId, coordinates, false);
    }

    public HitResult processRequest(String sessionId, Coordinates coordinates, boolean skipValidation) {
        if (!skipValidation && !Validation.validate(coordinates)) {
            System.out.println("ERROR MEOW");
            return null;
        }
        boolean isHit = areaCheck.isHit(coordinates);
        HitResult hitResult = new HitResult(sessionId, coordinates, getCurrentDate(), isHit);

        dbc.addHitResult(hitResult);

        return hitResult;
    }

    public void clearUserHits(String sessionId) {
        dbc.markUserHitsRemoved(sessionId);
    }

    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
