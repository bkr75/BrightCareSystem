package security;

import org.junit.Test;
import static org.junit.Assert.*;

public class AuthorizationTest {

    // ADMIN: should be allowed to do everything 

    @Test
    public void testAdminCanDoAnyOperation() {

        boolean result = Authorization.hasPermission("ADMIN", "REGISTER_PATIENT");

        assertTrue(result);
    }

    @Test
    public void testAdminCanGenerateReports() {

        boolean result = Authorization.hasPermission("ADMIN", "GET_DOCTOR_ACTIVITY_REPORT");

        assertTrue(result);
    }

    // DOCTOR: allowed for clinical operations only

    @Test
    public void testDoctorCanViewMedicalHistory() {

        boolean result = Authorization.hasPermission("DOCTOR", "VIEW_MEDICAL_HISTORY");

        assertTrue(result);
    }

    @Test
    public void testDoctorCanCompleteAppointment() {

        boolean result = Authorization.hasPermission("DOCTOR", "COMPLETE_APPOINTMENT");

        assertTrue(result);
    }

    @Test
    public void testDoctorCannotRegisterPatient() {

        // Registration is the Receptionist's job, not the Doctor's.
        boolean result = Authorization.hasPermission("DOCTOR", "REGISTER_PATIENT");

        assertFalse(result);
    }

    // RECEPTIONIST: allowed for registration/booking only

    @Test
    public void testReceptionistCanRegisterPatient() {

        boolean result = Authorization.hasPermission("RECEPTIONIST", "REGISTER_PATIENT");

        assertTrue(result);
    }

    @Test
    public void testReceptionistCannotUpdateDiagnosis() {

        // Updating a diagnosis is a clinical action reserved for doctors.
        boolean result = Authorization.hasPermission("RECEPTIONIST", "UPDATE_DIAGNOSIS");

        assertFalse(result);
    }

    //PATIENT: allowed for self-service operations only 

    @Test
    public void testPatientCanBookAppointment() {

        boolean result = Authorization.hasPermission("PATIENT", "BOOK_APPOINTMENT");

        assertTrue(result);
    }

    @Test
    public void testPatientCannotRegisterPatient() {

        // A patient cannot register other patients - that's the receptionist's role.
        boolean result = Authorization.hasPermission("PATIENT", "REGISTER_PATIENT");

        assertFalse(result);
    }

    //  Edge cases: invalid or missing input 

    @Test
    public void testUnknownRoleIsDenied() {

        // A role that doesn't exist in the system must never be granted access.
        boolean result = Authorization.hasPermission("HACKER", "BOOK_APPOINTMENT");

        assertFalse(result);
    }

    @Test
    public void testNullRoleIsDenied() {

        boolean result = Authorization.hasPermission(null, "BOOK_APPOINTMENT");

        assertFalse(result);
    }

    @Test
    public void testNullOperationIsDenied() {

        boolean result = Authorization.hasPermission("PATIENT", null);

        assertFalse(result);
    }
}