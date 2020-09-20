package utils;

import java.io.Serializable;

public class StatQuery1 implements Serializable {
    private Double cured;
    private Double swabs;

    public StatQuery1() {

    }

    public StatQuery1(Double cured, Double swabs) {
        this.cured = cured;
        this.swabs = swabs;
    }

    public Double getCured() {
        return cured;
    }

    public void setCured(Double cured) {
        this.cured = cured;
    }

    public Double getSwabs() {
        return swabs;
    }

    public void setSwabs(Double swabs) {
        this.swabs = swabs;
    }

    @Override
    public String toString() {
        return "StatQuery1{" +
                "cured=" + cured +
                ", swabs=" + swabs +
                '}';
    }
}
