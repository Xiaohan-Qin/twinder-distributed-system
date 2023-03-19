package Servlets;

import Constants.Constant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {
  private Hashtable<String, List<String>> matchTable;

  public void init() throws ServletException {
    matchTable = new Hashtable<String, List<String>>();
    matchTable.put("1", new ArrayList<String>());
    matchTable.get("1").add("123");
    matchTable.get("1").add("256");
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
      List<String> matches = matchTable.get(userId);
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write(String.valueOf(matches));
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
