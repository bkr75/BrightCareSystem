/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

        } catch(Exception e){

            e.printStackTrace();
        }
    }
}
