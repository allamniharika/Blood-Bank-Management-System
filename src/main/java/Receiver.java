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

@WebServlet("/Receiver")
public class Receiver extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String URL =
           System.getenv("DB_URL");

    private static final String DB_USER =
            "root";

    private static final String DB_PASSWORD =
            System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String FullName =
                request.getParameter("fullName");

        String ReceiverID =
                request.getParameter("ReceiverID");

        String Email =
                request.getParameter("Email");

        String Phone =
                request.getParameter("Phone");

        String Address =
                request.getParameter("Address");

        String BloodGroup =
                request.getParameter("BloodGroup");

        String DateOfBirth =
                request.getParameter("DateOfBirth");

        String Gender =
                request.getParameter("Gender");

        String HospitalName =
                request.getParameter("HospitalName");

        String ReasonForBloodRequest =
                request.getParameter(
                    "ReasonForBloodRequest"
                );

        String PreferredDateForReception =
                request.getParameter(
                    "PreferredDateForReception"
                );

        String Allergies =
                request.getParameter("Allergies");

        String EmergencyContact =
                request.getParameter(
                    "EmergencyContact"
                );

        response.setContentType(
            "text/html;charset=UTF-8"
        );

        PrintWriter out =
                response.getWriter();

        try {

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

                // =================================================
                // INSERT RECEIVER
                // =================================================

                String sqlInsert =
                        "INSERT INTO receiver " +
                        "(FullName, ReceiverID, Email, Phone, Address, " +
                        "BloodGroup, DateOfBirth, Gender, HospitalName, " +
                        "ReasonForBloodRequest, PreferredDateForReception, " +
                        "Allergies, EmergencyContact) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (
                    PreparedStatement preparedStatementInsert =
                        connection.prepareStatement(
                            sqlInsert
                        )
                ) {

                    preparedStatementInsert.setString(
                        1,
                        FullName
                    );

                    preparedStatementInsert.setString(
                        2,
                        ReceiverID
                    );

                    preparedStatementInsert.setString(
                        3,
                        Email
                    );

                    preparedStatementInsert.setString(
                        4,
                        Phone
                    );

                    preparedStatementInsert.setString(
                        5,
                        Address
                    );

                    preparedStatementInsert.setString(
                        6,
                        BloodGroup
                    );

                    preparedStatementInsert.setString(
                        7,
                        DateOfBirth
                    );

                    preparedStatementInsert.setString(
                        8,
                        Gender
                    );

                    preparedStatementInsert.setString(
                        9,
                        HospitalName
                    );

                    preparedStatementInsert.setString(
                        10,
                        ReasonForBloodRequest
                    );

                    preparedStatementInsert.setString(
                        11,
                        PreferredDateForReception
                    );

                    preparedStatementInsert.setString(
                        12,
                        Allergies
                    );

                    preparedStatementInsert.setString(
                        13,
                        EmergencyContact
                    );

                    preparedStatementInsert.executeUpdate();
                }

                // =================================================
                // PAGE
                // =================================================

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");

                out.println(
                    "<meta charset='UTF-8'>"
                );

                out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
                );

                out.println(
                    "<title>Blood Donor Match</title>"
                );

                out.println(
                    "<link href='" +
                    "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' " +
                    "rel='stylesheet'>"
                );

                out.println("<style>");

                out.println(
                    "table{" +
                    "width:100%;" +
                    "border-collapse:collapse;" +
                    "margin-top:20px;" +
                    "}"
                );

                out.println(
                    "th,td{" +
                    "border:1px solid #ddd;" +
                    "padding:10px;" +
                    "text-align:center;" +
                    "}"
                );

                out.println(
                    "th{" +
                    "background-color:#007bff;" +
                    "color:white;" +
                    "}"
                );

                out.println(
                    "tr:nth-child(even){" +
                    "background-color:#f2f2f2;" +
                    "}"
                );

                out.println(
                    ".container{" +
                    "margin:20px;" +
                    "}"
                );

                out.println("</style>");

                out.println("</head>");

                out.println(
                    "<body class='text-center mt-3'>"
                );

                out.println(
                    "<div class='container text-center mt-3'>"
                );

                out.println(
                    "<h2>Matching Donors</h2>"
                );

                // =================================================
                // ONLY AVAILABLE MATCHING DONORS
                // =================================================

                String sqlSelect =
                        "SELECT * FROM blooddonar " +
                        "WHERE BloodGroup = ? " +
                        "AND Availability = 'Available' " +
                        "ORDER BY id DESC";

                try (
                    PreparedStatement preparedStatementSelect =
                        connection.prepareStatement(
                            sqlSelect
                        )
                ) {

                    preparedStatementSelect.setString(
                        1,
                        BloodGroup
                    );

                    try (
                        ResultSet resultSet =
                            preparedStatementSelect.executeQuery()
                    ) {

                        if (!resultSet.next()) {

                            out.println(
                                "<p class='alert alert-warning'>" +
                                "No available donors found with Blood Group: " +
                                BloodGroup +
                                "</p>"
                            );

                        } else {

                            out.println(
                                "<table class='table table-bordered table-hover'>"
                            );

                            out.println(
                                "<tr>" +
                                "<th>Full Name</th>" +
                                "<th>Email</th>" +
                                "<th>Phone</th>" +
                                "<th>Address</th>" +
                                "<th>Blood Group</th>" +
                                "<th>Age</th>" +
                                "<th>Date</th>" +
                                "<th>Gender</th>" +
                                "<th>Emergency Contact</th>" +
                                "</tr>"
                            );

                            do {

                                out.println("<tr>");

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "Name"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "Email"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "Phone"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "Address"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "BloodGroup"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    resultSet.getInt(
                                        "Age"
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    resultSet.getDate(
                                        "Date"
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "Gender"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println(
                                    "<td>" +
                                    safe(
                                        resultSet.getString(
                                            "EmergencyContact"
                                        )
                                    ) +
                                    "</td>"
                                );

                                out.println("</tr>");

                            } while (resultSet.next());

                            out.println("</table>");
                        }
                    }
                }

                out.println("<div>");

                out.println(
                    "<a href='javascript:history.back()' " +
                    "class='btn btn-secondary m-2'>" +
                    "Go Back" +
                    "</a>"
                );

                out.println(
                    "<a href='main.html' " +
                    "class='btn btn-primary m-2'>" +
                    "Home" +
                    "</a>"
                );

                out.println("</div>");

                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
            }

        } catch (ClassNotFoundException | SQLException e) {

            out.println(
                "<p class='alert alert-danger'>" +
                "Error: " +
                safe(e.getMessage()) +
                "</p>"
            );

            e.printStackTrace();
        }
    }

    private static String safe(String value) {

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