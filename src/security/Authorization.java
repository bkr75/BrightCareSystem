package security;

public class Authorization {

    public static boolean hasPermission(String role, String operation) {

        if (role == null || operation == null) {
            return false;
        }

        switch (role) {

            case "ADMIN":
                return true;

            case "DOCTOR":
                return operation.equals("VIEW_PATIENT")
                        || operation.equals("UPDATE_DIAGNOSIS")
                        || operation.equals("GET_APPOINTMENT_LIST")
                        || operation.equals("VIEW_MEDICAL_HISTORY")
                        || operation.equals("GET_SCHEDULE")
                        || operation.equals("UPDATE_SCHEDULE")
                        || operation.equals("VIEW_CONSULTATION");

            case "RECEPTIONIST":
                
                return operation.equals("REGISTER_PATIENT")
                        || operation.equals("BOOK_APPOINTMENT");
                case "PATIENT":
    return operation.equals("BOOK_APPOINTMENT")
            || operation.equals("CANCEL_APPOINTMENT")
            || operation.equals("VIEW_APPOINTMENT_HISTORY")
            || operation.equals("UPDATE_PATIENT_INFO")
            || operation.equals("CHECK_DOCTOR_AVAILABILITY");

            default:
                return false;
        }
    }
}