package Servlets;

import Constants.Constant;
import ConnectionManagers.DynamoDBConnectionManager;
import Models.Stats;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@WebServlet(name = "StatsServlet", value = "/StatsServlet")
public class StatsServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatsServlet.class);
  private DynamoDbClient dynamoDbClient;

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
      GetItemRequest getItemRequest = GetItemRequest.builder()
          .tableName("user_data_denormalized")
          .key(Map.of("userid", AttributeValue.builder().n(String.valueOf(userId)).build()))
          .build();

      GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);

      if (!getItemResponse.item().isEmpty()) {
        AttributeValue numLikesValue = getItemResponse.item().get("num_likes");
        AttributeValue numDislikesValue = getItemResponse.item().get("num_dislikes");

        if (numLikesValue != null && numDislikesValue != null) {
          int numLikes = Integer.parseInt(numLikesValue.n());
          int numDislikes = Integer.parseInt(numDislikesValue.n());
          Stats stats = new Stats(numLikes, numDislikes);
          LOGGER.info("User stats for user " + userId + ": " + stats);
          response.setContentType("text/plain");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().write("User stats for user " + userId + ": " + stats);
        } else {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          response.getWriter().write(Constant.USER_NOT_FOUND);
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