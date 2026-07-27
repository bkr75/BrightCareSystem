/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import database.DBConnection;
import model.MedicalRecord;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class MedicalRecordDAO {



    // CREATE
    // Add medical history entry


    public boolean addMedicalRecord(MedicalRecord record) {



        String sql =
        "INSERT INTO MEDICAL_RECORD "
        + "(PATIENT_ID, DIAGNOSIS) "
        + "VALUES (?, ?)";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {



            ps.setInt(1, record.getPatientId());

            ps.setString(2, record.getDiagnosis());



            ps.executeUpdate();



            ResultSet rs =
            ps.getGeneratedKeys();



            if(rs.next()) {


                record.setRecordId(
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
    // Retrieve medical record by ID


    public MedicalRecord getMedicalRecordById(int recordId) {



        String sql =
        "SELECT * FROM MEDICAL_RECORD "
        + "WHERE RECORD_ID=?";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setInt(1, recordId);



            ResultSet rs =
            ps.executeQuery();



            if(rs.next()) {


                return new MedicalRecord(


                    rs.getInt("RECORD_ID"),

                    rs.getInt("PATIENT_ID"),

                    rs.getString("DIAGNOSIS")


                );

            }



        } catch(Exception e) {


            e.printStackTrace();

        }



        return null;


    }







    // UPDATE
    // Update diagnosis


    public boolean updateMedicalRecord(MedicalRecord record) {



        String sql =
        "UPDATE MEDICAL_RECORD "
        + "SET DIAGNOSIS=? "
        + "WHERE RECORD_ID=?";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setString(1,
                    record.getDiagnosis());


            ps.setInt(2,
                    record.getRecordId());



            int rows =
            ps.executeUpdate();



            return rows > 0;



        } catch(Exception e) {


            e.printStackTrace();

            return false;

        }


    }




    // READ
    // Retrieve all medical records for a patient


    public java.util.List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) {



        java.util.List<MedicalRecord> records =
                new java.util.ArrayList<>();



        String sql =
        "SELECT * FROM MEDICAL_RECORD "
        + "WHERE PATIENT_ID=?";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setInt(1, patientId);



            ResultSet rs =
            ps.executeQuery();



            while(rs.next()) {



                MedicalRecord record =
                new MedicalRecord(

                    rs.getInt("RECORD_ID"),

                    rs.getInt("PATIENT_ID"),

                    rs.getString("DIAGNOSIS")

                );



                records.add(record);

            }



        } catch(Exception e) {


            e.printStackTrace();

        }



        return records;


    }



}
