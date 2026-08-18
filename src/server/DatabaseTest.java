package server;

import database.DBConnection;
import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try {

            Connection conn = DBConnection.getConnection();

            System.out.println(
                    "Database connected successfully!"
            );

            conn.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
