/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

public class Patient implements Serializable {

    private int patientId;
    private String firstName;
    private String lastName;
    private String icPassport;
    private String contactNumber;
    private String medicalRecordId;


    // Constructor for creating new patient
    public Patient(String firstName, String lastName,
                   String icPassport, String contactNumber,
                   String medicalRecordId) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.icPassport = icPassport;
        this.contactNumber = contactNumber;
        this.medicalRecordId = medicalRecordId;
    }


    // Constructor for retrieving patient from database
    public Patient(int patientId,
                   String firstName,
                   String lastName,
                   String icPassport,
                   String contactNumber,
                   String medicalRecordId) {

        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.icPassport = icPassport;
        this.contactNumber = contactNumber;
        this.medicalRecordId = medicalRecordId;
    }


    // Getters

    public int getPatientId() {
        return patientId;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getIcPassport() {
        return icPassport;
    }


    public String getContactNumber() {
        return contactNumber;
    }


    public String getMedicalRecordId() {
        return medicalRecordId;
    }


    // Setters

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }


    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}