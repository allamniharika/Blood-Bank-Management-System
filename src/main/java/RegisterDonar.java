import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/RegisterDonar")
public class RegisterDonar extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Database configuration from environment variables
    private static final String URL =
            System.getenv("DB_URL");

    private static final String DB_USER =
            System.getenv("DB_USER");

    private static final String DB_PASSWORD =
            System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String weight = request.getParameter("weight");
        String bloodGroup = request.getParameter("bloodGroup");
        String age = request.getParameter("age");
        String date = request.getParameter("date");
        String gender = request.getParameter("gender");
        String diseases = request.getParameter("diseases");
        String emergencyContact =
                request.getParameter("emergencyContact");

        String query =
                "INSERT INTO blooddonar " +
                "(Name, Email, Phone, Address, Weight, BloodGroup, " +
                "Age, Date, Gender, Diseases, EmergencyContact) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PrintWriter out = response.getWriter()) {

            // Validate database configuration
            if (URL == null || URL.isBlank()
                    || DB_USER == null || DB_USER.isBlank()
                    || DB_PASSWORD == null || DB_PASSWORD.isBlank()) {

                throw new SQLException(
                        "Database configuration is missing. " +
                        "Check DB_URL, DB_USER and DB_PASSWORD."
                );
            }

            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect and insert donor record
            try (Connection connection =
                         DriverManager.getConnection(
                                 URL,
                                 DB_USER,
                                 DB_PASSWORD
                         );

                 PreparedStatement stmt =
                         connection.prepareStatement(query)) {

                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setString(4, address);
                stmt.setString(5, weight);
                stmt.setString(6, bloodGroup);
                stmt.setString(7, age);
                stmt.setString(8, date);
                stmt.setString(9, gender);
                stmt.setString(10, diseases);
                stmt.setString(11, emergencyContact);

                int result = stmt.executeUpdate();

                if (result > 0) {
                    response.sendRedirect("success.html");
                } else {
                    showError(
                            out,
                            "Registration failed. No data was inserted."
                    );
                }
            }

        } catch (ClassNotFoundException e) {

            getServletContext().log("MySQL JDBC driver not found.", e);

            showError(
                    response,
                    "MySQL JDBC driver was not found. " +
                    "Check the MySQL Connector/J JAR."
            );

        } catch (SQLException e) {

            getServletContext().log("Donor registration failed.", e);

            showError(
                    response,
                    "Registration failed. Check the database " +
                    "configuration and required fields."
            );

        } catch (Exception e) {

            getServletContext().log("Unexpected registration error.", e);

            showError(
                    response,
                    "An unexpected error occurred during registration."
            );
        }
    }

    private void showError(
            HttpServletResponse response,
            String message) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            showError(out, message);
        }
    }

    private void showError(
            PrintWriter out,
            String message) {

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");

        out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
        );

        out.println("<title>Registration Failed</title>");

        out.println(
                "<link href='https://cdn.jsdelivr.net/npm/" +
                "bootstrap@5.3.3/dist/css/bootstrap.min.css' " +
                "rel='stylesheet'>"
        );

        out.println("</head>");
        out.println("<body style='background-color:#f8d7da;'>");

        out.println(
                "<div class='container d-flex " +
                "justify-content-center align-items-center' " +
                "style='min-height:100vh;'>"
        );

        out.println(
                "<div class='card shadow p-4' " +
                "style='max-width:500px;width:100%;'>"
        );

        out.println("<div class='card-body text-center'>");

        out.println(
                "<h3 class='text-danger mb-3'>" +
                "Registration Failed</h3>"
        );

        out.println(
                "<p class='text-dark'>" +
                escapeHtml(message) +
                "</p>"
        );

        out.println(
                "<a href='donar.html' " +
                "class='btn btn-primary me-2'>Try Again</a>"
        );

        out.println(
                "<a href='main.html' " +
                "class='btn btn-secondary'>Home</a>"
        );

        out.println("</div></div></div>");
        out.println("</body></html>");
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}