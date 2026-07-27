package model;

import java.io.Serializable;

public class AppointmentSummaryReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private int month;
    private int year;
    private int totalAppointments;
    private int completedCount;
    private int cancelledCount;
    private int pendingCount;

    public AppointmentSummaryReport(int month, int year,
                                     int totalAppointments,
                                     int completedCount,
                                     int cancelledCount,
                                     int pendingCount) {

        this.month = month;
        this.year = year;
        this.totalAppointments = totalAppointments;
        this.completedCount = completedCount;
        this.cancelledCount = cancelledCount;
        this.pendingCount = pendingCount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getCancelledCount() {
        return cancelledCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }
}
