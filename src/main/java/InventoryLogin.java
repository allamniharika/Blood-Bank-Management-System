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
import jakarta.servlet.http.HttpSession;

@WebServlet("/InventoryLogin")
public class InventoryLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String URL =
           System.getenv("DB_URL");

    private static final String DB_USER = System.getenv("DB_USER");

    private static final String DB_PASSWORD = "root";

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql =
                "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con =
                         DriverManager.getConnection(
                                 URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        HttpSession session = request.getSession();
                        session.setAttribute("inventoryLoggedIn", true);
                        session.setAttribute("username", username);

                        response.sendRedirect(
                                "inventory-dashboard.html"
                        );

                    } else {

                        response.setContentType(
                                "text/html;charset=UTF-8"
                        );

                        response.getWriter().println(
                                "<h2>Incorrect username or password</h2>"
                        );

                        response.getWriter().println(
                                "<a href='inventory.html'>Try Again</a>"
                        );
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println(
                    "<h2>Database Error</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}