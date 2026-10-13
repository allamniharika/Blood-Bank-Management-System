import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {

    private static final long serialVersionUID = 1L;

   private static final String URL = System.getenv("DB_URL");
private static final String DB_USER = System.getenv("DB_USER");
private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        if (!password.equals(confirmPassword)) {
            response.getWriter().println("<h2>Passwords do not match!</h2>");
            response.getWriter().println("<a href='register.html'>Try Again</a>");
            return;
        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    URL,
                    DB_USER,
                    DB_PASSWORD
            );

            String checkSql =
                    "SELECT * FROM users WHERE username = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, username);

            ResultSet result = checkStatement.executeQuery();

            if (result.next()) {

                response.getWriter().println(
                        "<h2>Username already exists!</h2>"
                );

                response.getWriter().println(
                        "<a href='register.html'>Try Again</a>"
                );

            } else {

                String insertSql =
                        "INSERT INTO users (username, password) VALUES (?, ?)";

                PreparedStatement insertStatement =
                        connection.prepareStatement(insertSql);

                insertStatement.setString(1, username);
                insertStatement.setString(2, password);

                insertStatement.executeUpdate();

                response.getWriter().println(
                        "<h2>Registration Successful!</h2>"
                );

                response.getWriter().println(
                        "<p>Your account has been created.</p>"
                );

                response.getWriter().println(
                        "<a href='index.html'>Go to Login</a>"
                );

                insertStatement.close();
            }

            result.close();
            checkStatement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "<h2>Registration Failed</h2>"
            );

            response.getWriter().println(
                    "<p>Error: " + e.getMessage() + "</p>"
            );
        }
    }
}