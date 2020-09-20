package utils;

import exception.ContinentNotFound;

import java.awt.*;

public class ContinentFinder {
    private Integer[] LatNAm = {  90,  90,  78,  58,  15,  15,   1,    1,   51,   60,   60};
    private Integer[] LonNAm = {-169, -10, -10, -37, -30, -75, -82, -105, -180, -180, -169};

    private Integer[] LatNA2 = { 51,  51,  60};
    private Integer[] LonNA2 = {167, 180, 180};

    private Integer[] LatSAm = {   1,   1,  15,  15, -60,  -60};
    private Integer[] LonSAm = {-105, -82, -75, -30, -30, -105};

    private Integer[] LatEur = { 90, 90, 43, 43, 41, 41, 41, 40, 40, 39, 35, 33, 38,  35,  28,  15,  58,  78};
    private Integer[] LonEur = {-10, 78, 49, 30, 29, 29, 27, 27, 26, 25, 28, 28, 10, -10, -13, -30, -37, -10};

    private Integer[] LatAfr = { 15,  28,  35, 38, 33, 32, 30, 28, 11, 13, -60, -60};
    private Integer[] LonAfr = {-30, -13, -10, 10, 28, 35, 35, 34, 44, 52,  75, -30};

    private Integer[] LatOce = {-12, -10, -10, -10, -52, -52, -31};
    private Integer[] LonOce = {110, 140, 145, 180, 180, 143, 110};

    private Integer[] LatAsi = {90, 43, 43, 41, 41, 41, 40, 40, 39, 35, 33, 32, 30, 28, 11, 13, -60,  -60, -32, -12, -10,  33,  51,  60,  90};
    private Integer[] LonAsi = {78, 49, 30, 29, 29, 27, 27, 26, 25, 28, 28, 35, 35, 34, 44, 52,  75,  110, 110, 110, 140, 140, 167, 180, 180};

    private Integer[] LatAs2 = {  90,   90,  60 ,   60};
    private Integer[] LonAs2 = {-180, -169, -169, -180};

    private Integer[] LatAnt = { -60, -60, -90,  -90};
    private Integer[] LonAnt = {-180, 180, 180, -180};

    private Polygon northAmerica1;
    private Polygon northAmerica2;
    private Polygon southAmerica;
    private Polygon europe;
    private Polygon africa;
    private Polygon oceania;
    private Polygon asia1;
    private Polygon asia2;
    private Polygon antarctica;

    public ContinentFinder() {
        buildAmerica();
        buildEurope();
        buildAfrica();
        buildOceania();
        buildAsia();
        buildAntarctica();
    }

    private void buildAntarctica() {
        antarctica = new Polygon();
        for (int i = 0; i < LatAnt.length; i++)
            antarctica.addPoint(LonAnt[i], LatAnt[i]);
    }

    private void buildAsia() {
        asia1 = new Polygon();
        asia2 = new Polygon();
        for (int i = 0; i < LatAsi.length; i++)
            asia1.addPoint(LonAsi[i], LatAsi[i]);
        for (int i = 0; i < LatAs2.length; i++)
            asia2.addPoint(LonAs2[i], LatAs2[i]);
    }

    private void buildOceania() {
        oceania = new Polygon();
        for (int i = 0; i < LatOce.length; i++)
            oceania.addPoint(LonOce[i], LatOce[i]);
    }

    private void buildAfrica() {
        africa = new Polygon();
        for (int i = 0; i < LatAfr.length; i++)
            africa.addPoint(LonAfr[i], LatAfr[i]);
    }

    private void buildEurope() {
        europe = new Polygon();
        for (int i = 0; i < LatEur.length; i++)
            europe.addPoint(LonEur[i], LatEur[i]);
    }

    private void buildAmerica() {
        northAmerica1 = new Polygon();
        northAmerica2 = new Polygon();
        southAmerica = new Polygon();
        for (int i = 0; i < LatNAm.length; i++)
            northAmerica1.addPoint(LonNAm[i], LatNAm[i]);
        for (int i = 0; i < LatNA2.length; i++)
            northAmerica2.addPoint(LonNA2[i], LatNA2[i]);
        for (int i = 0; i < LatSAm.length; i++)
            southAmerica.addPoint(LonSAm[i], LatSAm[i]);
    }

    public Continent getContinent(double latitude, double longitude) throws ContinentNotFound {
        Point position = new Point((int) longitude, (int) latitude);
        if (africa.contains(position)) {
            return Continent.AFRICA;
        }
        if (northAmerica1.contains(position) || northAmerica2.contains(position) || southAmerica.contains(position)) {
            return Continent.AMERICA;
        }
        if (antarctica.contains(position)) {
            return Continent.ANTARCTICA;
        }
        if (asia1.contains(position) || asia2.contains(position)) {
            return Continent.ASIA;
        }
        if (europe.contains(position)) {
            return Continent.EUROPE;
        }
        if (oceania.contains(position)) {
            return Continent.OCEANIA;
        }
        throw new ContinentNotFound("Can't find continent for coordinates lat: " + latitude + ", long: " + longitude);
    }
}
