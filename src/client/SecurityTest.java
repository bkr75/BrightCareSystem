package client;

import security.Authorization;

public class SecurityTest {

    public static void main(String[] args) {

        System.out.println(
                Authorization.hasPermission("ADMIN", "BOOK_APPOINTMENT"));

        System.out.println(
                Authorization.hasPermission("DOCTOR", "BOOK_APPOINTMENT"));

        System.out.println(
                Authorization.hasPermission("RECEPTIONIST", "BOOK_APPOINTMENT"));

    }

}