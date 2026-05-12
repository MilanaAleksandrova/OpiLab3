package org.milana.weblab3.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.milana.weblab3.utils.HitResult;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
@Named("client")
@SessionScoped
public class ClientBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private final LinkedList<HitResult> currentHits = new LinkedList<>();

    private Coordinates coordinates = new Coordinates();

    private final java.util.List<BigDecimal> xValues = java.util.List.of(
            BigDecimal.valueOf(-4),
            BigDecimal.valueOf(-3),
            BigDecimal.valueOf(-2),
            BigDecimal.valueOf(-1),
            BigDecimal.valueOf(0),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4)
    );

    private final java.util.List<BigDecimal> rValues = java.util.List.of(
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(5)
    );

    @Inject
    private ServiceBean service;

    @PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            sessionId = facesContext.getExternalContext().getSessionId(true);
        } else {
            sessionId = UUID.randomUUID().toString();
        }

        try {
            LinkedList<HitResult> hits = service != null ? service.getUserHits(sessionId) : new LinkedList<>();
            if (hits != null) {
                currentHits.addAll(hits);
            }
        } catch (Exception e) {
            System.err.println("MEOW error on init client bean: " + e.getMessage());
        }
    }

    public void makeUserRequest() {
        makeRequest(this.coordinates);
    }

    public void makeRemoteRequest() {
        Function<String, BigDecimal> getParam = (name) -> {
            return new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name));
        };

        try {
            Coordinates coordinates = new Coordinates(getParam.apply("x"), getParam.apply("y"), getParam.apply("r"));
            HitResult hitResult = service.processRequest(this.sessionId, coordinates, true);
            if (hitResult != null) {
                this.currentHits.addFirst(hitResult);
            }
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("MEOW ERROR REQUEST PARAMS PARSE");
        }
    }

    public void makeRequest(Coordinates coordinates) {
        HitResult hitResult = service.processRequest(this.sessionId, coordinates);

        if (hitResult != null) {
            this.currentHits.addFirst(hitResult);
        }
    }

    public void clearHits() {
        currentHits.clear();
        service.clearUserHits(this.sessionId);
    }

    public void selectRadius(BigDecimal value) {
        if (value != null) {
            coordinates.setR(value);
        }
    }

    public String getRadiusAsString() {
        return formatDecimalDefault(coordinates.getR(), "1");
    }

    public String getResultsAsJson() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < currentHits.size(); i++) {
            HitResult hit = currentHits.get(i);
            builder.append("{")
                    .append("\"x\":").append(formatDecimal(hit.getX())).append(',')
                    .append("\"y\":").append(formatDecimal(hit.getY())).append(',')
                    .append("\"r\":").append(formatDecimal(hit.getR())).append(',')
                    .append("\"currentTime\":\"").append(escape(hit.getCurrentTime())).append("\",")
                    .append("\"result\":").append(hit.isResult())
                    .append("}");
            if (i < currentHits.size() - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public java.util.List<BigDecimal> getxValues() {
        return xValues;
    }

    public java.util.List<BigDecimal> getrValues() {
        return rValues;
    }

    private String formatDecimal(BigDecimal value) {
        return formatDecimalDefault(value, "null");
    }

    private String formatDecimalDefault(BigDecimal value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
