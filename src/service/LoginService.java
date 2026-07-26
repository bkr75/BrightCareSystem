package service;

import model.LoginData;
import security.Authentication;
import shared.Response;

public class LoginService {

    public Response login(LoginData loginData) {

        if (loginData == null) {
            return new Response(false,
                    "Login data is missing.",
                    null);
        }

        if (loginData.getUsername() == null ||
            loginData.getPassword() == null) {

            return new Response(false,
                    "Username or password is missing.",
                    null);
        }

        boolean success = Authentication.login(
                loginData.getUsername(),
                loginData.getPassword());

        if (success) {
            return new Response(true,
                    "Login successful.",
                    null);
        }

        return new Response(false,
                "Invalid username or password.",
                null);
    }
}