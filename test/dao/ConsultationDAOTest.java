/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

package dao;


import model.Consultation;
import org.junit.Test;

import static org.junit.Assert.*;



public class ConsultationDAOTest {



    @Test
    public void testAddConsultation() {



        ConsultationDAO dao =
        new ConsultationDAO();



        Consultation consultation =
        new Consultation(

            1,

            "Patient has headache symptoms"

        );



        boolean result =
        dao.addConsultation(
            consultation
        );



        assertTrue(result);



        assertTrue(
            consultation.getConsultationId() > 0
        );


    }






    @Test
    public void testGetConsultationById() {



        ConsultationDAO dao =
        new ConsultationDAO();



        Consultation consultation =
        new Consultation(

            1,

            "Follow up appointment"

        );



        dao.addConsultation(
            consultation
        );



        Consultation result =
        dao.getConsultationById(
            consultation.getConsultationId()
        );



        assertNotNull(result);



        assertEquals(

            "Follow up appointment",

            result.getConsultationNotes()

        );


    }







    @Test
    public void testUpdateConsultation() {



        ConsultationDAO dao =
        new ConsultationDAO();



        Consultation consultation =
        new Consultation(

            1,

            "Initial notes"

        );



        dao.addConsultation(
            consultation
        );



        consultation.setConsultationNotes(
            "Updated consultation notes"
        );



        boolean result =
        dao.updateConsultation(
            consultation
        );



        assertTrue(result);



        Consultation updated =
        dao.getConsultationById(
            consultation.getConsultationId()
        );



        assertEquals(

            "Updated consultation notes",

            updated.getConsultationNotes()

        );


    }



}