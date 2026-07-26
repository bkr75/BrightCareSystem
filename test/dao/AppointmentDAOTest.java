/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dao;


import model.Appointment;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;



public class AppointmentDAOTest {



    @Test
    public void testBookAppointment() {


        AppointmentDAO dao =
        new AppointmentDAO();



        Appointment appointment =
        new Appointment(

            1,              // Patient ID

            1,              // Doctor ID

            1,              // Schedule ID

            Date.valueOf("2026-07-26"),

            "BOOKED"

        );



        boolean result =
        dao.bookAppointment(appointment);



        assertTrue(result);



        // check Derby generated ID

        assertTrue(
            appointment.getAppointmentId() > 0
        );


    }






    @Test
    public void testGetAppointmentById() {



        AppointmentDAO dao =
        new AppointmentDAO();



        Appointment appointment =
        new Appointment(

            1,

            1,

            3,

            Date.valueOf("2026-07-25"),

            "BOOKED"

        );



        dao.bookAppointment(appointment);



        Appointment result =
        dao.getAppointmentById(
            appointment.getAppointmentId()
        );



        assertNotNull(result);



        assertEquals(

            1,

            result.getPatientId()

        );



        assertEquals(

            1,

            result.getDoctorId()

        );


    }






    @Test
    public void testCancelAppointment() {



        AppointmentDAO dao =
        new AppointmentDAO();



        Appointment appointment =
        new Appointment(

            1,

            1,

            1,

            Date.valueOf("2026-07-27"),

            "BOOKED"

        );



        dao.bookAppointment(appointment);



        boolean result =
        dao.cancelAppointment(
            appointment.getAppointmentId()
        );



        assertTrue(result);



        Appointment cancelled =
        dao.getAppointmentById(
            appointment.getAppointmentId()
        );



        assertEquals(

            "CANCELLED",

            cancelled.getStatus()

        );


    }

    
    @Test
    public void testGetAppointmentsByPatient() {


        AppointmentDAO dao =
        new AppointmentDAO();



        Appointment appointment =
        new Appointment(

            1,          // patient ID
            1,          // doctor ID
            1,          // schedule ID
            java.sql.Date.valueOf("2026-07-26"),
            "BOOKED"

        );



        dao.bookAppointment(appointment);



        java.util.List<Appointment> appointments =
        dao.getAppointmentsByPatient(1);



        assertNotNull(appointments);



        assertTrue(
            appointments.size() > 0
        );



        assertEquals(
            1,
            appointments.get(0).getPatientId()
        );


    }

    
    @Test
    public void testGetAppointmentsByDoctor() {


        AppointmentDAO dao =
        new AppointmentDAO();



        Appointment appointment =
        new Appointment(

            1,          // patient ID
            1,          // doctor ID
            1,          // schedule ID
            java.sql.Date.valueOf("2026-07-26"),
            "BOOKED"

        );



        dao.bookAppointment(appointment);



        java.util.List<Appointment> appointments =
        dao.getAppointmentsByDoctor(1);



        assertNotNull(appointments);



        assertTrue(
            appointments.size() > 0
        );



        assertEquals(
            1,
            appointments.get(0).getDoctorId()
        );


    }
    
}