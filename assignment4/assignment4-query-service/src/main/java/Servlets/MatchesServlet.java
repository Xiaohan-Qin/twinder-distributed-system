package Servlets;

import Constants.Constant;
import ConnectionManagers.DynamoDBConnectionManager;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(MatchesServlet.class);
  private AmazonDynamoDB dynamoDbClient;

  public void init() throws ServletException {
    super.init();
    dynamoDbClient = DynamoDBConnectionManager.getClient();
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
      Map<String, AttributeValue> key = new HashMap<>();
      key.put("userid", new AttributeValue().withN(String.valueOf(userId)));

      GetItemRequest getItemRequest = new GetItemRequest()
          .withTableName("user_data_denormalized")
          .withKey(key);

      Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(getItemRequest).getItem();

      if (returnedItem != null) {
        AttributeValue matchedUsersValue = returnedItem.get("matched_users");
        if (matchedUsersValue != null) {
          List<Integer> matchedUsers = matchedUsersValue.getNS().stream()
              .map(Integer::parseInt)
              .collect(Collectors.toList());
          LOGGER.info("Matched users for user " + userId + ": " + matchedUsers);
          response.setContentType("text/plain");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("Matched users for user " + userId + ": " + matchedUsers);
        } else {
          LOGGER.info("No matched users for user " + userId);
          response.setContentType("text/plain");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("No matched users for user " + userId);
        }
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(Constant.USER_NOT_FOUND);
      }
    } catch (Exception e) {
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