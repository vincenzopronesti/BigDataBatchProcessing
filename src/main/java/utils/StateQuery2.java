package utils;

import java.io.Serializable;
import java.util.Arrays;

public class StateQuery2 implements Serializable {
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer[] cases;

    public StateQuery2() {

    }

    public StateQuery2(String name, Double latitude, Double longitude, Integer[] cases) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cases = cases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getCases() {
        return cases;
    }

    public void setCases(Integer[] cases) {
        this.cases = cases;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "StateQuery2{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", cases=" + Arrays.toString(cases) +
                '}';
    }
}
