package Servlets;

import Constants.Constant;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(MatchesServlet.class);
  private Connection connection;


  public void init() throws ServletException {
    super.init();
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      LOGGER.warn(e.toString());
    }
    File confFile = new File(Objects.requireNonNull(this.getClass()
        .getClassLoader().getResource("postgresql.conf")).getFile());
    try {
      Scanner cin = new Scanner(confFile);
      String hostname = cin.nextLine();
      String port = cin.nextLine();
      String userName = cin.nextLine();
      String password = cin.nextLine();
      String dbName = cin.nextLine();
      String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
      LOGGER.trace("Getting remote connection with connection string from environment variables.");
      this.connection = DriverManager.getConnection(jdbcUrl);
      LOGGER.info("Remote database connection successful.");
    } catch (FileNotFoundException | SQLException e) {
      LOGGER.warn(e.toString());
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType(Constant.CONTENT_TYPE);
    String urlPath = request.getPathInfo();  //urlPath "/{userID}"
    if (!isValidUrlPath(urlPath)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(Constant.MISSING_PARAMS);
      return;
    }
    String[] urlParts = urlPath.split("/"); // urlParts ["", "{userId}"]
    if (!(isValidUrlParts(urlParts))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(Constant.INVALID_PARAMS);
      return;
    }
    // request url validation completed
    int userId = Integer.parseInt(urlParts[1]);
    try {
      String queryString = "SELECT matched_users FROM users.user_data WHERE userid = ?";
      PreparedStatement stmt = connection.prepareStatement(queryString);
      stmt.setInt(1, userId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        Array matchedUsersArray = rs.getArray("matched_users");
        String matchedUsersString = Arrays.toString((Object[]) matchedUsersArray.getArray());
        LOGGER.info("Matched users for user " + userId + ": " + matchedUsersString);
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Matched users for user " + userId + ": " + matchedUsersString);
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(Constant.USER_NOT_FOUND);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Constant.DB_ERROR);
    }
  }

  /*
  Check if user ID is empty
   */
  private boolean isValidUrlPath(String urlPath) {
    return urlPath != null && !urlPath.isEmpty();
  }

  /*
  Check if user ID is integer
   */
  private boolean isValidUrlParts(String[] urlParts) {
    String userId = urlParts[1];
    try {
      Integer.parseInt(userId);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
