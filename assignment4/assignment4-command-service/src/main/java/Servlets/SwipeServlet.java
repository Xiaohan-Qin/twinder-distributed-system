package Servlets;
import ConnectionManagers.RabbitmqConnectionManager;
import Constants.Constant;
import Models.SwipeReqBody;

import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import java.util.concurrent.BlockingQueue;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(SwipeServlet.class);
  private final Gson gson = new Gson();
  public final Integer numChannel = Constant.NUM_CHANNEL;
  private BlockingQueue<Channel> channelPool;


  @Override
  public void init() throws ServletException {
    super.init();
    RabbitmqConnectionManager rabbitmqConnectionManager = new RabbitmqConnectionManager();
    channelPool = rabbitmqConnectionManager.createChannelPool(numChannel);
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType(Constant.CONTENT_TYPE);
    request.setCharacterEncoding("UTF-8");

    String urlPath = request.getPathInfo(); // urlPath "/left", "/right"
    if (!isValidUrlPath(urlPath)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(Constant.MISSING_PARAMS);
      return;
    }
    String[] urlParts = urlPath.split("/"); // urlParts ["", "left"]
    if (!(isValidUrlParts(urlParts))) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(Constant.INVALID_PARAMS);
      return;
    }
    // include leftOrRight into the message and validate the constraints of request
    StringBuilder payload = new StringBuilder();
    String line;
    while ((line = request.getReader().readLine()) != null) {
      payload.append(line);
    }
    JsonObject jsonObject = gson.fromJson(payload.toString(), JsonObject.class);
    String leftOrRight = urlParts[1];
    jsonObject.addProperty("leftOrRight", leftOrRight);
    String message = gson.toJson(jsonObject);
    try {
      SwipeReqBody swipeReqBody = gson.fromJson(message, SwipeReqBody.class);
      if (!isValidReqBody(swipeReqBody)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(Constant.INVALID_REQ_BODY);
        return;
      }
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write(Constant.INVALID_REQ_BODY);
      return;
    }
    // send to rabbitMQ
    Channel channel = null;
    try {
      channel = channelPool.take();
    } catch (InterruptedException e) {
      LOGGER.error("Program has been interrupted.");
    }
    if (channel != null) {
      channel.basicPublish(
          "",
          Constant.QUEUE_NAME,
          MessageProperties.PERSISTENT_BASIC,
          message.getBytes());
      response.setStatus(HttpServletResponse.SC_CREATED);
      LOGGER.info("Sent " + message + " to rabbitmq");
      response.getWriter().write("Sent " + message + " to rabbitmq");
      channelPool.add(channel);
    } else {
      response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
    }
  }

  private boolean isValidUrlPath(String urlPath) {
    return urlPath != null && !urlPath.isEmpty();
  }

  private boolean isValidUrlParts(String[] urlParts) {
    if (urlParts.length == 2) {
      return (urlParts[1].equals("left") || urlParts[1].equals("right"));
    }
    return false;
  }

  private boolean isValidReqBody(SwipeReqBody swipeReqBody) {
    if (swipeReqBody.getSwiperId() < Constant.SWIPER_LOWER_BOUND ||
        swipeReqBody.getSwiperId() > Constant.SWIPER_UPPER_BOUND) {
      return false;
    }
    if (swipeReqBody.getSwipeeId() < Constant.SWIPEE_LOWER_BOUND ||
        swipeReqBody.getSwipeeId() > Constant.SWIPEE_UPPER_BOUND) {
      return false;
    }
    return swipeReqBody.getComment() != null
        && swipeReqBody.getComment().length() >= Constant.COMMENT_MIN_LENGTH &&
        swipeReqBody.getComment().length() <= Constant.COMMENT_MAX_LENGTH;
  }
}
