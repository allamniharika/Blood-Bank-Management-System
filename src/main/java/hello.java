import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class hello {
    // Database URL, username, and password
       private static final String URL = System.getenv("DB_URL");
private static final String DB_USER = System.getenv("DB_USER");
private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        
        // SQL Insert statement
        String sql = "INSERT INTO em (username, email) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the values for the placeholders
            statement.setString(1, "Santosh");
            statement.setString(2, "santosh.com");

            // Execute the insert command
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }

        } catch (SQLException e) {
            System.out.println("An error occurred while inserting data.");
            e.printStackTrace();
        }
    }
}
