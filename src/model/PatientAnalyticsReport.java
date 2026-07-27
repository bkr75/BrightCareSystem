package model;

import java.io.Serializable;

public class PatientAnalyticsReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalPatients;
    private int totalAppointments;
    private double averageAppointmentsPerPatient;

    public PatientAnalyticsReport(int totalPatients,
                                   int totalAppointments,
                                   double averageAppointmentsPerPatient) {

        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.averageAppointmentsPerPatient = averageAppointmentsPerPatient;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public double getAverageAppointmentsPerPatient() {
        return averageAppointmentsPerPatient;
    }
}
