package service;

import org.junit.Test;
import shared.Response;

import static org.junit.Assert.*;

public class DoctorServiceTest {

    @Test
    public void testGetAppointmentList_returnsSuccessResponse() {

        DoctorService service = new DoctorService();
        Response response = service.getAppointmentList(1);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetSchedule_returnsSuccessResponse() {

        DoctorService service = new DoctorService();
        Response response = service.getSchedule(1);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    public void testGetPatientHistory_returnsSuccessResponse() {

        DoctorService service = new DoctorService();
        Response response = service.getPatientHistory(1);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateSchedule_withNullSchedule_returnsFailure() {

        DoctorService service = new DoctorService();
        Response response = service.updateSchedule(null);

        assertFalse(response.isSuccess());
    }
}
