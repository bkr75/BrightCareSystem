package security;

public class Validation {

    public static boolean isEmpty(String value) {

        return value == null || value.trim().isEmpty();

    }

}
