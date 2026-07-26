/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import database.DBConnection;
import model.DoctorSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



public class DoctorScheduleDAO {



    // CREATE
    // Add doctor availability schedule

    public boolean addSchedule(DoctorSchedule schedule) {


        String sql =
        "INSERT INTO DOCTOR_SCHEDULE "
        + "(DOCTOR_ID, AVAILABLE_DATE, AVAILABLE_TIME, STATUS) "
        + "VALUES (?, ?, ?, ?)";



        try(Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {



            ps.setInt(1, schedule.getDoctorId());

            ps.setDate(2, schedule.getAvailableDate());

            ps.setTime(3, schedule.getAvailableTime());

            ps.setString(4, schedule.getStatus());



            ps.executeUpdate();



            ResultSet rs =
            ps.getGeneratedKeys();



            if(rs.next()) {


                schedule.setScheduleId(
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
    // Retrieve schedule by doctor ID

    public DoctorSchedule getScheduleById(int scheduleId) {


        String sql =
        "SELECT * FROM DOCTOR_SCHEDULE "
        + "WHERE SCHEDULE_ID=?";



        try(Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setInt(1, scheduleId);



            ResultSet rs =
            ps.executeQuery();



            if(rs.next()) {


                return new DoctorSchedule(


                    rs.getInt("SCHEDULE_ID"),

                    rs.getInt("DOCTOR_ID"),

                    rs.getDate("AVAILABLE_DATE"),

                    rs.getTime("AVAILABLE_TIME"),

                    rs.getString("STATUS")


                );

            }



        } catch(Exception e) {


            e.printStackTrace();

        }



        return null;


    }







    // UPDATE
    // Change schedule status
    // AVAILABLE -> BOOKED
    // BOOKED -> AVAILABLE


    public boolean updateScheduleStatus(int scheduleId,
                                        String status) {



        String sql =
        "UPDATE DOCTOR_SCHEDULE "
        + "SET STATUS=? "
        + "WHERE SCHEDULE_ID=?";



        try(Connection conn =
            DBConnection.getConnection();

            PreparedStatement ps =
            conn.prepareStatement(sql)) {



            ps.setString(1, status);

            ps.setInt(2, scheduleId);



            int rows =
            ps.executeUpdate();



            return rows > 0;



        } catch(Exception e) {


            e.printStackTrace();

            return false;

        }


    }

    
    // Retrieve available schedules

    public java.util.List<DoctorSchedule> getAvailableSchedules(int doctorId) {



        java.util.List<DoctorSchedule> schedules =
                new java.util.ArrayList<>();



        String sql =
        "SELECT * FROM DOCTOR_SCHEDULE "
        + "WHERE DOCTOR_ID=? "
        + "AND STATUS='AVAILABLE'";



        try(Connection conn =
            DBConnection.getConnection();


            PreparedStatement ps =
            conn.prepareStatement(sql)){



            ps.setInt(1, doctorId);



            ResultSet rs =
            ps.executeQuery();



            while(rs.next()) {



                DoctorSchedule schedule =
                new DoctorSchedule(

                    rs.getInt("SCHEDULE_ID"),

                    rs.getInt("DOCTOR_ID"),

                    rs.getDate("AVAILABLE_DATE"),

                    rs.getTime("AVAILABLE_TIME"),

                    rs.getString("STATUS")

                );



                schedules.add(schedule);

            }



        }catch(Exception e){


            e.printStackTrace();

        }



        return schedules;

    }


}