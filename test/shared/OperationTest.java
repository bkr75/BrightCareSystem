package shared;

import org.junit.Test;
import static org.junit.Assert.*;

public class OperationTest {

    // Individual constants are never null/empty
    // A typo like `public static final String LOGIN = "";` would compile
    // fine but silently break every login attempt at runtime - these
    // checks catch that class of mistake immediately.

    @Test
    public void testLoginOperationIsDefined() {
        assertNotNull(Operation.LOGIN);
        assertFalse(Operation.LOGIN.isEmpty());
    }

    @Test
    public void testCoreDoctorOperationsAreDefined() {
        assertEquals("UPDATE_DIAGNOSIS", Operation.UPDATE_DIAGNOSIS);
        assertEquals("ADD_CONSULTATION", Operation.ADD_CONSULTATION);
        assertEquals("COMPLETE_APPOINTMENT", Operation.COMPLETE_APPOINTMENT);
    }

    @Test
    public void testCorePatientOperationsAreDefined() {
        assertEquals("BOOK_APPOINTMENT", Operation.BOOK_APPOINTMENT);
        assertEquals("CANCEL_APPOINTMENT", Operation.CANCEL_APPOINTMENT);
    }

    @Test
    public void testCoreAdminReportOperationsAreDefined() {
        assertEquals("GET_APPOINTMENT_SUMMARY_REPORT", Operation.GET_APPOINTMENT_SUMMARY_REPORT);
        assertEquals("GET_DOCTOR_ACTIVITY_REPORT", Operation.GET_DOCTOR_ACTIVITY_REPORT);
        assertEquals("GET_PATIENT_ANALYTICS_REPORT", Operation.GET_PATIENT_ANALYTICS_REPORT);
    }

    // No two operations accidentally share the same value
    // ClinicRemoteImpl routes every request by matching request.getOperation()
    // against these constants in a switch statement. If two constants were
    // ever accidentally given the same string value, one operation would
    // silently steal the other's routing - this test guards against that.

    @Test
    public void testAllOperationValuesAreUnique() {

        String[] allOperations = {
            Operation.TEST_CONNECTION,
            Operation.LOGIN,
            Operation.REGISTER_PATIENT,
            Operation.BOOK_APPOINTMENT,
            Operation.VIEW_PATIENT,
            Operation.UPDATE_DIAGNOSIS,
            Operation.CANCEL_APPOINTMENT,
            Operation.VIEW_APPOINTMENT_HISTORY,
            Operation.UPDATE_PATIENT_INFO,
            Operation.CHECK_DOCTOR_AVAILABILITY,
            Operation.GET_APPOINTMENT_SUMMARY_REPORT,
            Operation.GET_DOCTOR_ACTIVITY_REPORT,
            Operation.GET_PATIENT_ANALYTICS_REPORT,
            Operation.GET_APPOINTMENT_LIST,
            Operation.VIEW_MEDICAL_HISTORY,
            Operation.GET_SCHEDULE,
            Operation.UPDATE_SCHEDULE,
            Operation.VIEW_CONSULTATION,
            Operation.ADD_CONSULTATION,
            Operation.COMPLETE_APPOINTMENT
        };

        java.util.Set<String> uniqueValues = new java.util.HashSet<>();

        for (String operation : allOperations) {
            uniqueValues.add(operation);
        }

        assertEquals("Found a duplicate Operation value - two operations "
                + "would collide in ClinicRemoteImpl's routing switch.",
                allOperations.length, uniqueValues.size());
    }
}