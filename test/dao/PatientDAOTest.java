/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package dao;


import model.Patient;
import org.junit.Test;
import static org.junit.Assert.*;


public class PatientDAOTest {


    @Test
    public void testRegisterPatient() {


        PatientDAO dao = new PatientDAO();


        Patient patient =
        new Patient(

            "John",

            "Smith",

            "IC" + System.currentTimeMillis(),

            "0123456789",

            "MR" + System.currentTimeMillis()

        );


        boolean result =
        dao.registerPatient(patient);


        assertTrue(result);


    }





    @Test
    public void testGetPatientById() {


        PatientDAO dao = new PatientDAO();


        Patient patient =
        new Patient(

            "Sarah",

            "Lee",

            "IC" + System.currentTimeMillis(),

            "0199999999",

            "MR" + System.currentTimeMillis()

        );


        dao.registerPatient(patient);



        Patient result =
        dao.getPatientById(
            patient.getPatientId()
        );



        assertNotNull(result);


        assertEquals(
            "Sarah",
            result.getFirstName()
        );

    }





    @Test
    public void testUpdatePatient() {


        PatientDAO dao = new PatientDAO();


        Patient patient =
        new Patient(

            "Adam",

            "Tan",

            "IC" + System.currentTimeMillis(),

            "0111111111",

            "MR" + System.currentTimeMillis()

        );


        dao.registerPatient(patient);



        patient.setContactNumber(
            "0222267222"
        );



        boolean result =
        dao.updatePatient(patient);



        assertTrue(result);

    }

}

