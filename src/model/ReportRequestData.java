package model;

import java.io.Serializable;

public class ReportRequestData implements Serializable {

    private static final long serialVersionUID = 1L;

    private int month;
    private int year;

    public ReportRequestData(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
