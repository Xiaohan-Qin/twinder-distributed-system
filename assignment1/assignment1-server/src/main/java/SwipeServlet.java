import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
  private static final int SWIPER_LOWER_BOUND = 1;
  private static final int SWIPER_UPPER_BOUND = 5000;
  private static final int SWIPEE_LOWER_BOUND = 1;
  private static final int SWIPEE_UPPER_BOUND = 1000000;


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    request.setCharacterEncoding("UTF-8");

    // urlPath: "/left"
    String urlPath = request.getPathInfo();

    // check we have an url
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }

    // validate url parts
    // urlParts: ["", "left"]
    String[] urlParts = urlPath.split("/");

    if (urlParts.length != 2 || !(urlParts[1].equals("left") || urlParts[1].equals("right"))) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("invalid url path");
      return;
    }

    // read json string from request body
    StringBuilder sb = new StringBuilder();
    String s;
    while ((s = request.getReader().readLine()) != null) {
      sb.append(s);
    }

    // transform request body from json to java object
    Gson gson = new Gson();
    SwipeReqBody swipeReqBody = gson.fromJson(sb.toString(), SwipeReqBody.class);

    try {
      // check if swiper id is out of range
      // if swiper id can not be parsed to integer, NumberFormatException in
      // the catch clause will handle it
      if (Integer.parseInt(swipeReqBody.getSwiper()) < SWIPER_LOWER_BOUND ||
          Integer.parseInt(swipeReqBody.getSwiper()) > SWIPER_UPPER_BOUND ) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("invalid swiper id");
        response.getWriter().flush();
        return;
      }
      // check if swipee id is out of range
      // if swiper id can not be parsed to integer, NumberFormatException in
      // the catch clause will handle it
      if (Integer.parseInt(swipeReqBody.getSwipee()) < SWIPEE_LOWER_BOUND ||
          Integer.parseInt(swipeReqBody.getSwipee()) > SWIPEE_UPPER_BOUND ) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("invalid swipee id");
        response.getWriter().flush();
        return;
      }
      // check if comment is absent or out of range
      if (swipeReqBody.getComment() == null || swipeReqBody.getComment().length() > 256) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("invalid comment");
        response.getWriter().flush();
        return;
      }
    } catch(NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Invalid swiper or swipee id. IDs must be integer.");
      response.getWriter().flush();
      return;
    }

    // we are here because request is valid
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write(sb.toString());
    response.getWriter().flush();
  }

  // urlParts: ["", "left"]
//  public boolean isValidUrl(String[] urlParts) {
//    if (urlParts.length != 2) {
//      return false;
//    } else {
//      return (urlParts[1].equals("left") || urlParts[1].equals("right"));
//    }
//  }
}

