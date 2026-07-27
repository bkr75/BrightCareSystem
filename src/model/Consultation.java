/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;


public class Consultation implements Serializable {


    private int consultationId;
    private int appointmentId;
    private String consultationNotes;
    private int version;



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



    // Constructor for retrieving consultation with its optimistic-locking version

    public Consultation(int consultationId,
                        int appointmentId,
                        String consultationNotes,
                        int version) {


        this.consultationId = consultationId;
        this.appointmentId = appointmentId;
        this.consultationNotes = consultationNotes;
        this.version = version;

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


    public int getVersion() {

        return version;

    }





    public void setConsultationId(int consultationId) {

        this.consultationId = consultationId;

    }



    public void setConsultationNotes(String consultationNotes) {

        this.consultationNotes = consultationNotes;

    }


    public void setVersion(int version) {

        this.version = version;

    }


}