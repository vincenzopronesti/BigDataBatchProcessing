package utils;

import java.io.Serializable;

public class StatQuery2 implements Serializable {
    private Double avg;
    private Double standardDev;
    private Double min;
    private Double max;

    public StatQuery2() {

    }

    public StatQuery2(Double avg, Double standardDev, Double min, Double max) {
        this.avg = avg;
        this.standardDev = standardDev;
        this.min = min;
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getStandardDev() {
        return standardDev;
    }

    public void setStandardDev(Double standardDev) {
        this.standardDev = standardDev;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "StatQuery2{" +
                "avg=" + avg +
                ", standardDev=" + standardDev +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
