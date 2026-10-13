package sample;

import java.sql.Connection;
import java.sql.DriverManager;

public class Jdbc {

    private static final String URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
            URL,
            DB_USER,
            DB_PASSWORD
        );
    }
}