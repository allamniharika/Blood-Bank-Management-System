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

@WebServlet("/BloodDonars")
public class BloodDonars extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String URL =
            System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD =
            System.getenv("DB_PASSWORD");

    private Connection getConnection()
            throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        if (DB_PASSWORD == null || DB_PASSWORD.isEmpty()) {
            throw new SQLException(
                    "DB_PASSWORD environment variable is not set."
            );
        }

        return DriverManager.getConnection(
                URL, DB_USER, DB_PASSWORD
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isInventoryLoggedIn(request)) {
            response.sendRedirect("inventory.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (Connection connection = getConnection();
             PrintWriter out = response.getWriter()) {

            printHeader(out);
            printAvailableDonors(connection, out);
            printIssueForm(connection, out);
            printHistory(connection, out);
            printFooter(out);

        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html><html><body>");
                out.println("<h2>Database Error</h2>");
                out.println("<p>" + escape(e.getMessage()) + "</p>");
                out.println(
                        "<p><a href='inventory-dashboard.html'>" +
                        "Back to Inventory</a></p>"
                );
                out.println("</body></html>");
            }
        }
    }

    private void printHeader(PrintWriter out) {

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'>");

        out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
        );

        out.println("<title>Blood Donor Management</title>");

        out.println(
                "<link href='https://cdn.jsdelivr.net/npm/" +
                "bootstrap@5.3.3/dist/css/bootstrap.min.css' " +
                "rel='stylesheet'>"
        );

        out.println("<style>");
        out.println("body{background:#f5f7fb;}");
        out.println(".container{max-width:1200px;}");
        out.println(".card{border-radius:15px;margin-top:25px;}");
        out.println("th{background:#007bff!important;color:white!important;}");
        out.println(".section-title{color:#007bff;font-weight:700;}");
        out.println("</style>");

        out.println("</head><body>");
        out.println("<div class='container py-4'>");
        out.println(
                "<h2 class='text-center mb-4'>" +
                "Blood Donor Management</h2>"
        );
    }

    private void printAvailableDonors(
            Connection connection,
            PrintWriter out) throws SQLException {

        out.println(
                "<div class='card shadow p-4'>" +
                "<h4 class='section-title mb-3'>Available Donors</h4>"
        );

        String sql =
                "SELECT id, Name, Email, Phone, BloodGroup, Age " +
                "FROM blooddonar " +
                "WHERE Availability = 'Available' " +
                "ORDER BY id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            out.println(
                    "<div class='table-responsive'>" +
                    "<table class='table table-striped table-bordered'>"
            );

            out.println(
                    "<tr><th>ID</th><th>Name</th><th>Email</th>" +
                    "<th>Phone</th><th>Blood Group</th>" +
                    "<th>Age</th><th>Availability</th></tr>"
            );

            boolean found = false;

            while (rs.next()) {
                found = true;

                out.println(
                        "<tr>" +
                        "<td>" + rs.getInt("id") + "</td>" +
                        "<td>" + escape(rs.getString("Name")) + "</td>" +
                        "<td>" + escape(rs.getString("Email")) + "</td>" +
                        "<td>" + escape(rs.getString("Phone")) + "</td>" +
                        "<td>" + escape(rs.getString("BloodGroup")) + "</td>" +
                        "<td>" + rs.getInt("Age") + "</td>" +
                        "<td><span class='badge bg-success'>Available</span></td>" +
                        "</tr>"
                );
            }

            out.println("</table></div>");

            if (!found) {
                out.println(
                        "<div class='alert alert-warning'>" +
                        "No available donors found.</div>"
                );
            }
        }

        out.println("</div>");
    }

    private void printIssueForm(
            Connection connection,
            PrintWriter out) throws SQLException {

        out.println(
                "<div class='card shadow p-4'>" +
                "<h4 class='section-title mb-3'>" +
                "Issue Blood to Receiver</h4>"
        );

        out.println("<form method='POST' action='BloodDonars'>");
        out.println("<label class='form-label'>Select Donor</label>");
        out.println(
                "<select name='donor_id' class='form-select mb-3' required>"
        );
        out.println("<option value=''>Select donor</option>");

        String donorSql =
                "SELECT id, Name, BloodGroup FROM blooddonar " +
                "WHERE Availability = 'Available' ORDER BY Name";

        try (PreparedStatement ps =
                     connection.prepareStatement(donorSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.println(
                        "<option value='" + rs.getInt("id") + "'>" +
                        escape(rs.getString("Name")) + " - " +
                        escape(rs.getString("BloodGroup")) +
                        " (ID: " + rs.getInt("id") + ")</option>"
                );
            }
        }

        out.println("</select>");

        out.println("<label class='form-label'>Select Receiver</label>");
        out.println(
                "<select name='receiver_id' class='form-select mb-3' required>"
        );
        out.println("<option value=''>Select receiver</option>");

        String receiverSql =
                "SELECT id, FullName, BloodGroup FROM receiver " +
                "ORDER BY FullName";

        try (PreparedStatement ps =
                     connection.prepareStatement(receiverSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.println(
                        "<option value='" + rs.getInt("id") + "'>" +
                        escape(rs.getString("FullName")) + " - " +
                        escape(rs.getString("BloodGroup")) +
                        " (ID: " + rs.getInt("id") + ")</option>"
                );
            }
        }

        out.println("</select>");

        out.println("<label class='form-label'>Units</label>");
        out.println(
                "<input type='number' name='units' " +
                "class='form-control mb-3' min='1' max='10' " +
                "value='1' required>"
        );

        out.println(
                "<button type='submit' class='btn btn-primary'>" +
                "Issue Blood</button>"
        );

        out.println("</form></div>");
    }

    private void printHistory(
            Connection connection,
            PrintWriter out) throws SQLException {

        out.println(
                "<div class='card shadow p-4'>" +
                "<h4 class='section-title mb-3'>Blood Issue History</h4>"
        );

        String sql =
                "SELECT bt.id, d.Name AS donor_name, " +
                "r.FullName AS receiver_name, bt.blood_group, " +
                "bt.units, bt.status, bt.issued_at " +
                "FROM blood_transactions bt " +
                "JOIN blooddonar d ON bt.donor_id = d.id " +
                "JOIN receiver r ON bt.receiver_id = r.id " +
                "ORDER BY bt.issued_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            out.println(
                    "<div class='table-responsive'>" +
                    "<table class='table table-striped table-bordered'>"
            );

            out.println(
                    "<tr><th>ID</th><th>Donor</th><th>Receiver</th>" +
                    "<th>Blood Group</th><th>Units</th>" +
                    "<th>Status</th><th>Issued At</th></tr>"
            );

            boolean historyFound = false;

            while (rs.next()) {
                historyFound = true;

                out.println("<tr>");

                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println(
                        "<td>" + escape(rs.getString("donor_name")) + "</td>"
                );
                out.println(
                        "<td>" + escape(rs.getString("receiver_name")) + "</td>"
                );
                out.println(
                        "<td>" + escape(rs.getString("blood_group")) + "</td>"
                );
                out.println("<td>" + rs.getInt("units") + "</td>");

                out.println(
                        "<td><span class='badge bg-danger'>" +
                        escape(rs.getString("status")) +
                        "</span></td>"
                );

                out.println(
                        "<td>" +
                        escape(String.valueOf(rs.getTimestamp("issued_at"))) +
                        "</td>"
                );

                out.println("</tr>");
            }

            out.println("</table></div>");

            if (!historyFound) {
                out.println(
                        "<div class='alert alert-info'>" +
                        "No blood issue transactions yet.</div>"
                );
            }
        }

        out.println("</div>");
    }

    private void printFooter(PrintWriter out) {

        out.println("<div class='text-center mt-4'>");

        out.println(
                "<a href='inventory-dashboard.html' " +
                "class='btn btn-secondary me-2'>" +
                "Back to Inventory</a>"
        );

        out.println(
                "<a href='main.html' class='btn btn-primary'>Home</a>"
        );

        out.println("</div></div></body></html>");
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isInventoryLoggedIn(request)) {
            response.sendRedirect("inventory.html");
            return;
        }

        Connection connection = null;

        try {
            int donorId =
                    Integer.parseInt(request.getParameter("donor_id"));

            int receiverId =
                    Integer.parseInt(request.getParameter("receiver_id"));

            int units =
                    Integer.parseInt(request.getParameter("units"));

            if (units < 1 || units > 10) {
                throw new IllegalArgumentException(
                        "Units must be between 1 and 10."
                );
            }

            connection = getConnection();
            connection.setAutoCommit(false);

            String bloodGroup;

            String donorSql =
                    "SELECT BloodGroup, Availability " +
                    "FROM blooddonar WHERE id = ? FOR UPDATE";

            try (PreparedStatement ps =
                         connection.prepareStatement(donorSql)) {

                ps.setInt(1, donorId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Donor not found.");
                    }

                    bloodGroup = rs.getString("BloodGroup");

                    if (!"Available".equalsIgnoreCase(
                            rs.getString("Availability"))) {
                        throw new SQLException(
                                "This donor's blood is already issued."
                        );
                    }
                }
            }

            String receiverSql =
                    "SELECT id FROM receiver WHERE id = ?";

            try (PreparedStatement ps =
                         connection.prepareStatement(receiverSql)) {

                ps.setInt(1, receiverId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Receiver not found.");
                    }
                }
            }

            String insertSql =
                    "INSERT INTO blood_transactions " +
                    "(donor_id, receiver_id, blood_group, units, status) " +
                    "VALUES (?, ?, ?, ?, 'Issued')";

            try (PreparedStatement ps =
                         connection.prepareStatement(insertSql)) {

                ps.setInt(1, donorId);
                ps.setInt(2, receiverId);
                ps.setString(3, bloodGroup);
                ps.setInt(4, units);
                ps.executeUpdate();
            }

            String updateSql =
                    "UPDATE blooddonar SET Availability = 'Issued' " +
                    "WHERE id = ?";

            try (PreparedStatement ps =
                         connection.prepareStatement(updateSql)) {

                ps.setInt(1, donorId);
                ps.executeUpdate();
            }

            connection.commit();
            response.sendRedirect("BloodDonars");

        } catch (NumberFormatException e) {
            rollbackQuietly(connection);
            showError(
                    response,
                    "Invalid Input",
                    "Please select a donor, receiver, and valid units."
            );

        } catch (IllegalArgumentException |
                 ClassNotFoundException |
                 SQLException e) {

            rollbackQuietly(connection);
            showError(response, "Blood Issue Failed", e.getMessage());

        } catch (Exception e) {
            rollbackQuietly(connection);
            showError(response, "Blood Issue Failed", e.getMessage());

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                    // Connection cleanup
                }
            }
        }
    }

    private void showError(
            HttpServletResponse response,
            String title,
            String message) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println(
                    "<!DOCTYPE html><html><head>" +
                    "<meta charset='UTF-8'><title>" +
                    escape(title) + "</title></head><body>"
            );

            out.println("<h2>" + escape(title) + "</h2>");
            out.println("<p>" + escape(message) + "</p>");
            out.println("<p><a href='BloodDonars'>Go Back</a></p>");
            out.println("</body></html>");
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
                // Rollback cleanup
            }
        }
    }

    private boolean isInventoryLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null &&
                Boolean.TRUE.equals(
                        session.getAttribute("inventoryLoggedIn")
                );
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}