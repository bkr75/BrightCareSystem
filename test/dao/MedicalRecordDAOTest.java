/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dao;


import model.MedicalRecord;
import org.junit.Test;

import static org.junit.Assert.*;



public class MedicalRecordDAOTest {



    @Test
    public void testAddMedicalRecord() {



        MedicalRecordDAO dao =
        new MedicalRecordDAO();



        MedicalRecord record =
        new MedicalRecord(

            1,

            "Migraine"

        );



        boolean result =
        dao.addMedicalRecord(record);



        assertTrue(result);



        assertTrue(
            record.getRecordId() > 0
        );


    }






    @Test
    public void testGetMedicalRecordById() {



        MedicalRecordDAO dao =
        new MedicalRecordDAO();



        MedicalRecord record =
        new MedicalRecord(

            1,

            "Fever"

        );



        dao.addMedicalRecord(record);



        MedicalRecord result =
        dao.getMedicalRecordById(
            record.getRecordId()
        );



        assertNotNull(result);



        assertEquals(

            "Fever",

            result.getDiagnosis()

        );


    }







    @Test
    public void testUpdateMedicalRecord() {



        MedicalRecordDAO dao =
        new MedicalRecordDAO();



        MedicalRecord record =
        new MedicalRecord(

            1,

            "Cold"

        );



        dao.addMedicalRecord(record);



        record.setDiagnosis(
            "Severe Cold"
        );



        boolean result =
        dao.updateMedicalRecord(record);



        assertTrue(result);



    }



}
