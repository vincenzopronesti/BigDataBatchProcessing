package utils;


import java.io.Serializable;

public class ItemQuery1 implements Serializable {
    private Integer week;
    private Integer day;  // 1 for Monday, 2 for Tuesday and so on
    private StatQuery1 stats;

    public ItemQuery1() {

    }

    public ItemQuery1(Integer week, Integer day, StatQuery1 stats) {
        this.week = week;
        this.day = day;
        this.stats = stats;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public StatQuery1 getStats() {
        return stats;
    }

    public void setStats(StatQuery1 stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "ItemQuery1{" +
                "week=" + week +
                ", day=" + day +
                ", stats=" + stats +
                '}';
    }
}
