package service;

import model.LoginData;
import model.User;
import security.Authentication;
import shared.Response;

public class LoginService {

    public Response login(LoginData loginData) {

        if (loginData == null) {
            return new Response(false,
                    "Login data is missing.",
                    null);
        }

        if (loginData.getUsername() == null
                || loginData.getPassword() == null) {

            return new Response(false,
                    "Username or password is missing.",
                    null);
        }

        User user = Authentication.login(
                loginData.getUsername(),
                loginData.getPassword());

        if (user != null) {
            // data now carries the role, so the client knows which
            // menu/module to open, and ClinicRemoteImpl can trust it.
            return new Response(true,
                    "Login successful.",
                    user.getRole());
        }

        return new Response(false,
                "Invalid username or password.",
                null);
    }
}
