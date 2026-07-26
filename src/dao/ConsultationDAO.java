/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import database.DBConnection;
import model.Consultation;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class ConsultationDAO {



    // CREATE
    // Doctor adds consultation notes


    public boolean addConsultation(Consultation consultation) {


        String sql =
        "INSERT INTO CONSULTATION "
        + "(APPOINTMENT_ID, CONSULTATION_NOTES) "
        + "VALUES (?, ?)";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {



            ps.setInt(
                1,
                consultation.getAppointmentId()
            );


            ps.setString(
                2,
                consultation.getConsultationNotes()
            );



            ps.executeUpdate();



            ResultSet rs =
            ps.getGeneratedKeys();



            if(rs.next()) {


                consultation.setConsultationId(
                    rs.getInt(1)
                );

            }



            return true;



        }catch(Exception e){


            e.printStackTrace();

            return false;

        }

    }







    // READ
    // Retrieve consultation notes


    public Consultation getConsultationById(int consultationId) {



        String sql =
        "SELECT * FROM CONSULTATION "
        + "WHERE CONSULTATION_ID=?";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setInt(
                1,
                consultationId
            );



            ResultSet rs =
            ps.executeQuery();



            if(rs.next()) {


                return new Consultation(


                    rs.getInt("CONSULTATION_ID"),

                    rs.getInt("APPOINTMENT_ID"),

                    rs.getString(
                        "CONSULTATION_NOTES"
                    )


                );


            }



        }catch(Exception e){


            e.printStackTrace();

        }



        return null;

    }







    // UPDATE
    // Doctor updates consultation notes


    public boolean updateConsultation(Consultation consultation) {



        String sql =
        "UPDATE CONSULTATION "
        + "SET CONSULTATION_NOTES=? "
        + "WHERE CONSULTATION_ID=?";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)){



            ps.setString(
                1,
                consultation.getConsultationNotes()
            );


            ps.setInt(
                2,
                consultation.getConsultationId()
            );



            int rows =
            ps.executeUpdate();



            return rows > 0;



        }catch(Exception e){


            e.printStackTrace();

            return false;

        }


    }



}