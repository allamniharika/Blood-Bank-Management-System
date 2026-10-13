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

@WebServlet("/BloodConfirmation")
public class BloodConfirmation extends HttpServlet {

    private static final long serialVersionUID = 1L;

private static final String URL =
        System.getenv("DB_URL");

private static final String DB_USER =
        System.getenv("DB_USER");

private static final String DB_PASSWORD =
        System.getenv("DB_PASSWORD");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Blood Confirmation</title>");

        out.println(
            "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
        );

        out.println("</head>");
        out.println("<body class='bg-light'>");

        out.println("<div class='container mt-5'>");

        out.println("<div class='card shadow p-4 mx-auto' style='max-width:550px;'>");

        out.println("<h2 class='text-center mb-4'>Blood Donation Confirmation</h2>");

        out.println("<form method='POST' action='BloodConfirmation'>");

        out.println("<label class='form-label'>Transaction ID</label>");

        out.println(
            "<input type='number' name='transaction_id' " +
            "class='form-control mb-3' required>"
        );

        out.println("<label class='form-label'>I am</label>");

        out.println(
            "<select name='role' class='form-select mb-3' required>"
        );

        out.println(
            "<option value=''>Select</option>"
        );

        out.println(
            "<option value='donor'>Donor</option>"
        );

        out.println(
            "<option value='receiver'>Receiver</option>"
        );

        out.println("</select>");

        out.println(
            "<button type='submit' class='btn btn-primary w-100'>"
        );

        out.println("Confirm");

        out.println("</button>");

        out.println("</form>");

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String transactionText =
                request.getParameter("transaction_id");

        String role =
                request.getParameter("role");

        try {

            int transactionId =
                    Integer.parseInt(transactionText);

            Class.forName(
                "com.mysql.cj.jdbc.Driver"
            );

            try (
                Connection connection =
                    DriverManager.getConnection(
                        URL,
                        DB_USER,
                        DB_PASSWORD
                    )
            ) {

                String checkSql =
                        "SELECT donor_confirmed, " +
                        "receiver_confirmed " +
                        "FROM blood_transactions " +
                        "WHERE id = ?";

                boolean donorConfirmed;
                boolean receiverConfirmed;

                try (
                    PreparedStatement ps =
                        connection.prepareStatement(checkSql)
                ) {

                    ps.setInt(1, transactionId);

                    try (
                        ResultSet rs =
                            ps.executeQuery()
                    ) {

                        if (!rs.next()) {

                            out.println(
                                "<h3>Transaction not found.</h3>"
                            );

                            return;
                        }

                        donorConfirmed =
                            rs.getBoolean(
                                "donor_confirmed"
                            );

                        receiverConfirmed =
                            rs.getBoolean(
                                "receiver_confirmed"
                            );
                    }
                }

                String updateSql;

                if ("donor".equalsIgnoreCase(role)) {

                    updateSql =
                        "UPDATE blood_transactions " +
                        "SET donor_confirmed = TRUE " +
                        "WHERE id = ?";

                } else if ("receiver".equalsIgnoreCase(role)) {

                    updateSql =
                        "UPDATE blood_transactions " +
                        "SET receiver_confirmed = TRUE " +
                        "WHERE id = ?";

                } else {

                    out.println(
                        "<h3>Invalid role.</h3>"
                    );

                    return;
                }

                try (
                    PreparedStatement ps =
                        connection.prepareStatement(
                            updateSql
                        )
                ) {

                    ps.setInt(1, transactionId);

                    ps.executeUpdate();
                }

                // Check both confirmations again

                String finalCheckSql =
                        "SELECT donor_confirmed, " +
                        "receiver_confirmed " +
                        "FROM blood_transactions " +
                        "WHERE id = ?";

                try (
                    PreparedStatement ps =
                        connection.prepareStatement(
                            finalCheckSql
                        )
                ) {

                    ps.setInt(1, transactionId);

                    try (
                        ResultSet rs =
                            ps.executeQuery()
                    ) {

                        if (rs.next()) {

                            donorConfirmed =
                                rs.getBoolean(
                                    "donor_confirmed"
                                );

                            receiverConfirmed =
                                rs.getBoolean(
                                    "receiver_confirmed"
                                );

                            if (
                                donorConfirmed &&
                                receiverConfirmed
                            ) {

                                String completeSql =
                                    "UPDATE blood_transactions " +
                                    "SET status = 'Completed' " +
                                    "WHERE id = ?";

                                try (
                                    PreparedStatement completePs =
                                        connection.prepareStatement(
                                            completeSql
                                        )
                                ) {

                                    completePs.setInt(
                                        1,
                                        transactionId
                                    );

                                    completePs.executeUpdate();
                                }

                                out.println(
                                    "<h2 class='text-success'>" +
                                    "Blood transaction completed." +
                                    "</h2>"
                                );

                            } else {

                                out.println(
                                    "<h2>Confirmation saved.</h2>"
                                );

                                out.println(
                                    "<p>Waiting for the other person to confirm.</p>"
                                );
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {

            out.println(
                "<h3 class='text-danger'>Error: " +
                e.getMessage() +
                "</h3>"
            );
        }
    }
}