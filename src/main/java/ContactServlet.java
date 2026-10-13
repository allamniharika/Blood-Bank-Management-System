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

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // MySQL details
   private static final String URL = System.getenv("DB_URL");
private static final String DB_USER = System.getenv("DB_USER");
private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    URL,
                    DB_USER,
                    DB_PASSWORD
            );

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                // LOGIN SUCCESS
                response.getWriter().println("<!DOCTYPE html>");
                response.getWriter().println("<html>");
                response.getWriter().println("<head>");
                response.getWriter().println("<title>Login Successful</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body {");
                response.getWriter().println("margin:0;");
                response.getWriter().println("font-family:Arial,sans-serif;");
                response.getWriter().println("background:#f2f2f2;");
                response.getWriter().println("display:flex;");
                response.getWriter().println("justify-content:center;");
                response.getWriter().println("align-items:center;");
                response.getWriter().println("min-height:100vh;");
                response.getWriter().println("}");

                response.getWriter().println(".box {");
                response.getWriter().println("width:350px;");
                response.getWriter().println("padding:35px;");
                response.getWriter().println("background:white;");
                response.getWriter().println("border-radius:10px;");
                response.getWriter().println("box-shadow:0 4px 15px rgba(0,0,0,0.2);");
                response.getWriter().println("text-align:center;");
                response.getWriter().println("}");

                response.getWriter().println(".box h2 {");
                response.getWriter().println("color:green;");
                response.getWriter().println("}");

                response.getWriter().println(".button {");
                response.getWriter().println("display:inline-block;");
                response.getWriter().println("margin-top:20px;");
                response.getWriter().println("padding:10px 20px;");
                response.getWriter().println("background:#007bff;");
                response.getWriter().println("color:white;");
                response.getWriter().println("text-decoration:none;");
                response.getWriter().println("border-radius:5px;");
                response.getWriter().println("}");

                response.getWriter().println("</style>");
                response.getWriter().println("</head>");

                response.getWriter().println("<body>");

                response.getWriter().println("<div class='box'>");
                response.getWriter().println("<h2>Login Successful!</h2>");
                response.getWriter().println("<p>Welcome, " + username + "</p>");
                response.getWriter().println("<a class='button' href='main.html'>Continue</a>");
                response.getWriter().println("</div>");

                response.getWriter().println("</body>");
                response.getWriter().println("</html>");

            } else {

                // LOGIN FAILED
                response.getWriter().println("<!DOCTYPE html>");
                response.getWriter().println("<html>");
                response.getWriter().println("<head>");
                response.getWriter().println("<title>Login Failed</title>");

                response.getWriter().println("<style>");
                response.getWriter().println("body {");
                response.getWriter().println("margin:0;");
                response.getWriter().println("font-family:Arial,sans-serif;");
                response.getWriter().println("background:#f2f2f2;");
                response.getWriter().println("display:flex;");
                response.getWriter().println("justify-content:center;");
                response.getWriter().println("align-items:center;");
                response.getWriter().println("min-height:100vh;");
                response.getWriter().println("}");

                response.getWriter().println(".box {");
                response.getWriter().println("width:350px;");
                response.getWriter().println("padding:35px;");
                response.getWriter().println("background:white;");
                response.getWriter().println("border-radius:10px;");
                response.getWriter().println("box-shadow:0 4px 15px rgba(0,0,0,0.2);");
                response.getWriter().println("text-align:center;");
                response.getWriter().println("}");

                response.getWriter().println(".box h2 {");
                response.getWriter().println("color:red;");
                response.getWriter().println("}");

                response.getWriter().println(".button {");
                response.getWriter().println("display:inline-block;");
                response.getWriter().println("margin-top:20px;");
                response.getWriter().println("padding:10px 20px;");
                response.getWriter().println("background:#007bff;");
                response.getWriter().println("color:white;");
                response.getWriter().println("text-decoration:none;");
                response.getWriter().println("border-radius:5px;");
                response.getWriter().println("}");

                response.getWriter().println("</style>");
                response.getWriter().println("</head>");

                response.getWriter().println("<body>");

                response.getWriter().println("<div class='box'>");
                response.getWriter().println("<h2>Login Failed</h2>");
                response.getWriter().println("<p>Invalid username or password.</p>");
                response.getWriter().println("<a class='button' href='index.html'>Try Again</a>");
                response.getWriter().println("</div>");

                response.getWriter().println("</body>");
                response.getWriter().println("</html>");
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            response.getWriter().println("<h2>Database Connection Error</h2>");
            response.getWriter().println("<p>Check MySQL connection details.</p>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.getWriter().println("<h1>ContactServlet is working!</h1>");
    }
}