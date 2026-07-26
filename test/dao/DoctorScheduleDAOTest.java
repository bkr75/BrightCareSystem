/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dao;


import model.DoctorSchedule;
import org.junit.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.Assert.*;



public class DoctorScheduleDAOTest {



    @Test
    public void testAddSchedule() {


        DoctorScheduleDAO dao =
        new DoctorScheduleDAO();



        DoctorSchedule schedule =
        new DoctorSchedule(

            1,

            Date.valueOf("2026-07-25"),

            Time.valueOf("10:00:00"),

            "AVAILABLE"

        );



        boolean result =
        dao.addSchedule(schedule);



        assertTrue(result);



    }






    @Test
    public void testGetScheduleById() {


        DoctorScheduleDAO dao =
        new DoctorScheduleDAO();



        DoctorSchedule schedule =
        new DoctorSchedule(

            1,

            Date.valueOf("2026-07-26"),

            Time.valueOf("11:00:00"),

            "AVAILABLE"

        );



        dao.addSchedule(schedule);



        DoctorSchedule result =
        dao.getScheduleById(
            schedule.getScheduleId()
        );



        assertNotNull(result);



        assertEquals(

            "AVAILABLE",

            result.getStatus()

        );


    }






    @Test
    public void testUpdateScheduleStatus() {



        DoctorScheduleDAO dao =
        new DoctorScheduleDAO();



        DoctorSchedule schedule =
        new DoctorSchedule(

            1,

            Date.valueOf("2026-07-27"),

            Time.valueOf("12:00:00"),

            "AVAILABLE"

        );



        dao.addSchedule(schedule);



        boolean result =
        dao.updateScheduleStatus(

            schedule.getScheduleId(),

            "BOOKED"

        );



        assertTrue(result);


    }

    
    @Test
    public void testGetAvailableSchedules() {


        DoctorScheduleDAO dao =
        new DoctorScheduleDAO();



        java.util.List<DoctorSchedule> schedules =
        dao.getAvailableSchedules(1);



        assertNotNull(schedules);



        assertTrue(
            schedules.size() > 0
        );



        assertEquals(
            "AVAILABLE",
            schedules.get(0).getStatus()
        );


    }

}
