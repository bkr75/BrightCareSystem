package security;

public class Authentication {

    public static boolean login(String username, String password) {

        if (username == null || password == null) {
            return false;
        }

        username = username.trim();
        password = password.trim();

        return username.equals("admin") && password.equals("1234");
    }

}