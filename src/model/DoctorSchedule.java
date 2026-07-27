/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package model;

import java.sql.Date;
import java.sql.Time;
import java.io.Serializable;


public class DoctorSchedule implements Serializable {


    private int scheduleId;
    private int doctorId;
    private Date availableDate;
    private Time availableTime;
    private String status;



    // Constructor for creating new schedule

    public DoctorSchedule(int doctorId,
                          Date availableDate,
                          Time availableTime,
                          String status) {


        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.availableTime = availableTime;
        this.status = status;

    }



    // Constructor for retrieving schedule

    public DoctorSchedule(int scheduleId,
                          int doctorId,
                          Date availableDate,
                          Time availableTime,
                          String status) {


        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.availableTime = availableTime;
        this.status = status;

    }



    // Getters

    public int getScheduleId() {

        return scheduleId;

    }


    public int getDoctorId() {

        return doctorId;

    }


    public Date getAvailableDate() {

        return availableDate;

    }


    public Time getAvailableTime() {

        return availableTime;

    }


    public String getStatus() {

        return status;

    }



    // Setter

    public void setScheduleId(int scheduleId) {

        this.scheduleId = scheduleId;

    }


}
