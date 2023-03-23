package Servlets;

import Constants.Constant;
import Models.Stats;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {
//  private Hashtable<String, List<String>> matchTable;
  private static final Logger LOGGER = LoggerFactory.getLogger(MatchesServlet.class);
  private final PGSimpleDataSource dataSource = new PGSimpleDataSource();
  private Connection connection;


  public void init() throws ServletException {
    super.init();
    File confFile = new File(Objects.requireNonNull(this.getClass()
        .getClassLoader().getResource("cockroachdb.conf")).getFile());
    try {
      Scanner cin = new Scanner(confFile);
      dataSource.setServerNames(new String[] {cin.nextLine()});
      dataSource.setPortNumbers(new int[] {Integer.parseInt(cin.nextLine())});
      dataSource.setDatabaseName(cin.nextLine());
      dataSource.setUser(cin.nextLine());
      dataSource.setPassword(cin.nextLine());
      this.connection = dataSource.getConnection();
      LOGGER.info("connect to cockroachDB successfully");
    } catch (FileNotFoundException | SQLException e) {
      e.printStackTrace();
    }

//    matchTable = new Hashtable<String, List<String>>();
//    matchTable.put("1", new ArrayList<String>());
//    matchTable.get("1").add("123");
//    matchTable.get("1").add("256");
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
      PreparedStatement stmt = connection.prepareStatement(
          "SELECT matched_users FROM user_data WHERE user_id = ?");
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

//    String userId = urlParts[1];
//    boolean isValidUserId = matchTable.containsKey(userId);
//    if (isValidUserId) {
//      List<String> matches = matchTable.get(userId);
//      response.setStatus(HttpServletResponse.SC_OK);
//      response.getWriter().write(String.valueOf(matches));
//    } else {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      response.getWriter().write(Constant.USER_NOT_FOUND);
//    }
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
