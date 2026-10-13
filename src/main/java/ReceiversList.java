import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ReceiversList")
public class ReceiversList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // MySQL Database Connection
    private static final String URL =
           System.getenv("DB_URL");

    private static final String DB_USER = System.getenv("DB_USER");

    private static final String DB_PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Check inventory login session
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("inventoryLoggedIn") == null ||
            !Boolean.TRUE.equals(
                    session.getAttribute("inventoryLoggedIn"))) {

            response.sendRedirect("inventory.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String sql = "SELECT * FROM receiver";

        try {

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL
            try (
                Connection con =
                        DriverManager.getConnection(
                                URL,
                                DB_USER,
                                DB_PASSWORD
                        );

                PreparedStatement stmt =
                        con.prepareStatement(sql);

                ResultSet resultSet =
                        stmt.executeQuery()
            ) {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");

                out.println("<meta charset='UTF-8'>");

                out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
                );

                out.println("<title>Receiver Data</title>");

                out.println(
                    "<link href=" +
                    "'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' " +
                    "rel='stylesheet'>"
                );

                out.println("<style>");

                out.println(
                    "body { " +
                    "font-family: Arial, sans-serif; " +
                    "background-color: #f8f9fa; " +
                    "}"
                );

                out.println(
                    ".table-container { " +
                    "width: 100%; " +
                    "overflow-x: auto; " +
                    "margin-top: 20px; " +
                    "}"
                );

                out.println(
                    "table { " +
                    "width: 100%; " +
                    "min-width: 1500px; " +
                    "}"
                );

                out.println(
                    "th { " +
                    "background-color: #4CAF50 !important; " +
                    "color: white !important; " +
                    "white-space: nowrap; " +
                    "}"
                );

                out.println(
                    "td { white-space: nowrap; }"
                );

                out.println(
                    "tr:hover { " +
                    "background-color: #f1f1f1; " +
                    "}"
                );

                out.println("</style>");

                out.println("</head>");

                out.println("<body>");

                out.println(
                    "<div class='container-fluid mt-4'>"
                );

                out.println(
                    "<h2 class='text-center mb-4'>" +
                    "Registered Receivers" +
                    "</h2>"
                );

                out.println(
                    "<div class='table-container'>"
                );

                out.println(
                    "<table class='table table-bordered " +
                    "table-hover text-center'>"
                );

                out.println("<thead>");

                out.println("<tr>");

                out.println("<th>ID</th>");
                out.println("<th>Full Name</th>");
                out.println("<th>Receiver ID</th>");
                out.println("<th>Email</th>");
                out.println("<th>Phone</th>");
                out.println("<th>Address</th>");
                out.println("<th>Blood Group</th>");
                out.println("<th>Date of Birth</th>");
                out.println("<th>Gender</th>");
                out.println("<th>Hospital Name</th>");
                out.println("<th>Reason for Blood Request</th>");
                out.println("<th>Preferred Date</th>");
                out.println("<th>Allergies</th>");
                out.println("<th>Emergency Contact</th>");

                out.println("</tr>");

                out.println("</thead>");

                out.println("<tbody>");

                boolean hasResults = false;

                while (resultSet.next()) {

                    hasResults = true;

                    out.println("<tr>");

                    out.println(
                        "<td>" +
                        resultSet.getInt("id") +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("FullName")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("ReceiverID")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("Email")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("Phone")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("Address")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("BloodGroup")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        resultSet.getDate("DateOfBirth") +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("Gender")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("HospitalName")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString(
                                "ReasonForBloodRequest"
                            )
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        resultSet.getDate(
                            "PreferredDateForReception"
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString("Allergies")
                        ) +
                        "</td>"
                    );

                    out.println(
                        "<td>" +
                        escapeHtml(
                            resultSet.getString(
                                "EmergencyContact"
                            )
                        ) +
                        "</td>"
                    );

                    out.println("</tr>");
                }

                if (!hasResults) {

                    out.println(
                        "<tr>" +
                        "<td colspan='14' " +
                        "class='text-center'>" +
                        "No receiver data available" +
                        "</td>" +
                        "</tr>"
                    );
                }

                out.println("</tbody>");

                out.println("</table>");

                out.println("</div>");

                out.println(
                    "<div class='text-center mt-4 mb-4'>"
                );

                out.println(
                    "<a href='inventory-dashboard.html' " +
                    "class='btn btn-secondary'>" +
                    "Back to Inventory" +
                    "</a>"
                );

                out.println("</div>");

                out.println("</div>");

                out.println("</body>");

                out.println("</html>");
            }

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

            out.println(
                "<div class='container mt-4'>" +
                "<div class='alert alert-danger'>" +
                "<h4>MySQL Driver Error</h4>" +
                "<p>" +
                escapeHtml(e.getMessage()) +
                "</p>" +
                "</div>" +
                "</div>"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            out.println(
                "<div class='container mt-4'>" +
                "<div class='alert alert-danger'>" +
                "<h4>Database Error</h4>" +
                "<p>" +
                escapeHtml(e.getMessage()) +
                "</p>" +
                "</div>" +
                "</div>"
            );

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                "<div class='container mt-4'>" +
                "<div class='alert alert-danger'>" +
                "<h4>Error</h4>" +
                "<p>" +
                escapeHtml(e.getMessage()) +
                "</p>" +
                "</div>" +
                "</div>"
            );
        }
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