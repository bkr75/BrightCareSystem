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
                        || operation.equals("UPDATE_DIAGNOSIS");

            case "RECEPTIONIST":
                return operation.equals("REGISTER_PATIENT")
                        || operation.equals("BOOK_APPOINTMENT");

            default:
                return false;
        }
    }
}