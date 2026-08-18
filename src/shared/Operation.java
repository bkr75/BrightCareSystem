package shared;

public class Operation {

    public static final String TEST_CONNECTION = "TEST_CONNECTION";
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER_PATIENT = "REGISTER_PATIENT";
    public static final String BOOK_APPOINTMENT = "BOOK_APPOINTMENT";
    public static final String VIEW_PATIENT = "VIEW_PATIENT";
    public static final String UPDATE_DIAGNOSIS = "UPDATE_DIAGNOSIS";
    // patient 
    public static final String CANCEL_APPOINTMENT = "CANCEL_APPOINTMENT";
    public static final String VIEW_APPOINTMENT_HISTORY = "VIEW_APPOINTMENT_HISTORY";
    public static final String UPDATE_PATIENT_INFO = "UPDATE_PATIENT_INFO";
    public static final String CHECK_DOCTOR_AVAILABILITY = "CHECK_DOCTOR_AVAILABILITY";

    // Admin / reporting operations
    public static final String GET_APPOINTMENT_SUMMARY_REPORT = "GET_APPOINTMENT_SUMMARY_REPORT";
    public static final String GET_DOCTOR_ACTIVITY_REPORT = "GET_DOCTOR_ACTIVITY_REPORT";
    public static final String GET_PATIENT_ANALYTICS_REPORT = "GET_PATIENT_ANALYTICS_REPORT";

    // Doctor module operations
    public static final String GET_APPOINTMENT_LIST = "GET_APPOINTMENT_LIST";
    public static final String VIEW_MEDICAL_HISTORY = "VIEW_MEDICAL_HISTORY";
    public static final String GET_SCHEDULE = "GET_SCHEDULE";
    public static final String UPDATE_SCHEDULE = "UPDATE_SCHEDULE";
    public static final String VIEW_CONSULTATION = "VIEW_CONSULTATION";
    public static final String ADD_CONSULTATION = "ADD_CONSULTATION";
    public static final String COMPLETE_APPOINTMENT = "COMPLETE_APPOINTMENT";

    private Operation() {
        // Prevent object creation
    }
}
