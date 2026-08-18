package security;

import dao.UserDAO;
import model.User;

public class Authentication {

    private static final UserDAO userDAO = new UserDAO();

    // Returns the User (with role) if credentials are valid, otherwise null.
    public static User login(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        username = username.trim();
        password = password.trim();

        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }
}
