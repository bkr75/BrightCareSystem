/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;


public class Doctor implements Serializable {


    private int doctorId;
    private String doctorName;
    private String specialization;



    // Constructor for creating new doctor

    public Doctor(String doctorName, String specialization) {

        this.doctorName = doctorName;
        this.specialization = specialization;

    }



    // Constructor for retrieving doctor from database

    public Doctor(int doctorId,
                  String doctorName,
                  String specialization) {


        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;

    }



    // Getters

    public int getDoctorId() {

        return doctorId;

    }


    public String getDoctorName() {

        return doctorName;

    }


    public String getSpecialization() {

        return specialization;

    }



    // Setter

    public void setDoctorId(int doctorId) {

        this.doctorId = doctorId;

    }


}