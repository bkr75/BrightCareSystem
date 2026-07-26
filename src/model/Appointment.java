/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;


public class Appointment {


    private int appointmentId;
    private int patientId;
    private int doctorId;
    private int scheduleId;
    private Date appointmentDate;
    private String status;



    // Constructor for creating new appointment

    public Appointment(int patientId,
                       int doctorId,
                       int scheduleId,
                       Date appointmentDate,
                       String status) {


        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduleId = scheduleId;
        this.appointmentDate = appointmentDate;
        this.status = status;

    }




    // Constructor for retrieving appointment

    public Appointment(int appointmentId,
                       int patientId,
                       int doctorId,
                       int scheduleId,
                       Date appointmentDate,
                       String status) {


        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduleId = scheduleId;
        this.appointmentDate = appointmentDate;
        this.status = status;

    }





    // Getters


    public int getAppointmentId() {

        return appointmentId;

    }


    public int getPatientId() {

        return patientId;

    }


    public int getDoctorId() {

        return doctorId;

    }


    public int getScheduleId() {

        return scheduleId;

    }


    public Date getAppointmentDate() {

        return appointmentDate;

    }


    public String getStatus() {

        return status;

    }





    // Setter

    public void setAppointmentId(int appointmentId) {

        this.appointmentId = appointmentId;

    }


    public void setStatus(String status) {

        this.status = status;

    }


}
