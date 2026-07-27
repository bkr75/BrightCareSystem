package model;

import java.io.Serializable;

public class DoctorActivityReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private int doctorId;
    private String doctorName;
    private String specialization;
    private int totalAppointments;

    public DoctorActivityReport(int doctorId, String doctorName,
                                 String specialization,
                                 int totalAppointments) {

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.totalAppointments = totalAppointments;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }
}
