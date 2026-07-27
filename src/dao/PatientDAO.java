/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.DBConnection;
import model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class PatientDAO {


    // CREATE
    // Register new patient

    public boolean registerPatient(Patient patient) {


        String sql =
        "INSERT INTO PATIENT "
        + "(FIRST_NAME, LAST_NAME, IC_PASSPORT, CONTACT_NUMBER, MEDICAL_RECORD_ID) "
        + "VALUES (?, ?, ?, ?, ?)";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps =
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            ps.setString(3, patient.getIcPassport());
            ps.setString(4, patient.getContactNumber());
            ps.setString(5, patient.getMedicalRecordId());


            ps.executeUpdate();


            // Retrieve generated patient ID

            ResultSet rs = ps.getGeneratedKeys();

            if(rs.next()) {

                patient.setPatientId(
                    rs.getInt(1)
                );
            }


            return true;


        } catch(Exception e) {

            e.printStackTrace();
            return false;
        }
    }




    // READ
    // Retrieve patient using ID

    public Patient getPatientById(int patientId) {


        String sql =
        "SELECT * FROM PATIENT WHERE PATIENT_ID=?";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps =
            conn.prepareStatement(sql)) {


            ps.setInt(1, patientId);


            ResultSet rs = ps.executeQuery();


            if(rs.next()) {


                return new Patient(

                    rs.getInt("PATIENT_ID"),

                    rs.getString("FIRST_NAME"),

                    rs.getString("LAST_NAME"),

                    rs.getString("IC_PASSPORT"),

                    rs.getString("CONTACT_NUMBER"),

                    rs.getString("MEDICAL_RECORD_ID")

                );
            }


        } catch(Exception e) {

            e.printStackTrace();
        }


        return null;
    }




    // UPDATE
    // Update patient contact information

    public boolean updatePatient(Patient patient) {


        String sql =
        "UPDATE PATIENT SET CONTACT_NUMBER=? "
        + "WHERE PATIENT_ID=?";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps =
            conn.prepareStatement(sql)) {


            ps.setString(1, patient.getContactNumber());

            ps.setInt(2, patient.getPatientId());


            int rows =
            ps.executeUpdate();


            return rows > 0;


        } catch(Exception e) {

            e.printStackTrace();
            return false;
        }
    }


    // READ
    // Count total registered patients (used for reporting)

    public int getPatientCount() {

        String sql = "SELECT COUNT(*) AS TOTAL FROM PATIENT";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch(Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

}

