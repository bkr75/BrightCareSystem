/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class Consultation {


    private int consultationId;
    private int appointmentId;
    private String consultationNotes;



    // Constructor for creating new consultation

    public Consultation(int appointmentId,
                        String consultationNotes) {


        this.appointmentId = appointmentId;
        this.consultationNotes = consultationNotes;

    }





    // Constructor for retrieving consultation

    public Consultation(int consultationId,
                        int appointmentId,
                        String consultationNotes) {


        this.consultationId = consultationId;
        this.appointmentId = appointmentId;
        this.consultationNotes = consultationNotes;

    }





    public int getConsultationId() {

        return consultationId;

    }


    public int getAppointmentId() {

        return appointmentId;

    }


    public String getConsultationNotes() {

        return consultationNotes;

    }





    public void setConsultationId(int consultationId) {

        this.consultationId = consultationId;

    }



    public void setConsultationNotes(String consultationNotes) {

        this.consultationNotes = consultationNotes;

    }


}