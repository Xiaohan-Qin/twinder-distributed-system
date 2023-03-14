package Servlets;

import Constants.Constant;
import Models.LikesAndDislikes;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "StatsServlet", value = "/StatsServlet")
public class StatsServlet extends HttpServlet {
  private Hashtable<String, LikesAndDislikes> matchTable;
  private final LikesAndDislikes likesAndDislikes = new LikesAndDislikes(5, 0);

    public void init() throws ServletException {
      matchTable = new Hashtable<String, LikesAndDislikes>();
      matchTable.put("1", likesAndDislikes);
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
      String userId = urlParts[1];
      boolean isValidUserId = matchTable.containsKey(userId);
      if (isValidUserId) {
        LikesAndDislikes likesAndDislikes = matchTable.get(userId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(likesAndDislikes.toString());
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(Constant.USER_NOT_FOUND);
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

