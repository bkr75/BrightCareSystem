package dao;

import database.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // READ
    // Find user by username (used for login)
    public User getUserByUsername(String username) {

        String sql = "SELECT * FROM USERS WHERE USERNAME=?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getInt("USER_ID"),
                        rs.getString("USERNAME"),
                        rs.getString("PASSWORD"),
                        rs.getString("ROLE")
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}
