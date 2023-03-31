package Servlets;

import Constants.Constant;
import ConnectionManagers.DatabaseConnectionPool;
import Models.Stats;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "StatsServlet", value = "/StatsServlet")
public class StatsServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatsServlet.class);
  private Connection connection;

    public void init() throws ServletException {
      super.init();
      try {
        connection = DatabaseConnectionPool.getConnection();
      } catch (SQLException e) {
        LOGGER.warn(e.toString());
      }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        String queryString = "SELECT num_likes, num_dislikes FROM users.user_data WHERE userid = ?";
        PreparedStatement stmt = connection.prepareStatement(queryString);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          int numLike = rs.getInt("num_likes");
          int numDislike = rs.getInt("num_dislikes");
          Stats stats = new Stats(numLike, numDislike);
          LOGGER.info("User stats for user " + userId + ": " + stats);
          response.setContentType("text/plain");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("User stats for user " + userId + ": " + stats);
        } else {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          response.getWriter().write(Constant.USER_NOT_FOUND);
        }
      } catch (SQLException e) {
        LOGGER.warn(e.toString());
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

