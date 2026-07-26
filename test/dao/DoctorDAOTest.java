/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

package dao;


import model.Doctor;
import org.junit.Test;
import static org.junit.Assert.*;



public class DoctorDAOTest {



    @Test
    public void testAddDoctor() {


        DoctorDAO dao = new DoctorDAO();


        Doctor doctor =
        new Doctor(

            "Dr Ahmed",

            "Cardiology"

        );



        boolean result =
        dao.addDoctor(doctor);



        assertTrue(result);



    }





    @Test
    public void testGetDoctorById() {


        DoctorDAO dao =
        new DoctorDAO();



        Doctor doctor =
        new Doctor(

            "Dr Sarah",

            "Dermatology"

        );



        dao.addDoctor(doctor);



        Doctor result =
        dao.getDoctorById(
            doctor.getDoctorId()
        );



        assertNotNull(result);



        assertEquals(

            "Dr Sarah",

            result.getDoctorName()

        );


    }





    @Test
    public void testUpdateDoctor() {



        DoctorDAO dao =
        new DoctorDAO();



        Doctor doctor =
        new Doctor(

            "Dr John",

            "General Medicine"

        );



        dao.addDoctor(doctor);



        doctor =
        new Doctor(

            doctor.getDoctorId(),

            "Dr John",

            "Neurology"

        );



        boolean result =
        dao.updateDoctor(doctor);



        assertTrue(result);


    }


}