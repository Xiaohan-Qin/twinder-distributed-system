import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import java.io.File;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final ConnectionFactory conFactory = new ConnectionFactory();
    public final Integer NUM_CHANNEL = 10;
    private BlockingQueue<Channel> channelPool;

    @Override
    public void init() throws ServletException {
        super.init();
        File confFile = new File(Objects.requireNonNull(this.getClass()
            .getClassLoader().getResource("rabbitmq.conf")).getFile());
        try {
            Scanner cin = new Scanner(confFile);
            conFactory.setHost(cin.nextLine());
            conFactory.setPort(Integer.parseInt(cin.nextLine()));
            conFactory.setUsername(cin.nextLine());
            conFactory.setPassword(cin.nextLine());
            conFactory.setVirtualHost("myTwinderApp");
            channelPool = new LinkedBlockingQueue<>();
            Connection connection = conFactory.newConnection();
            for(int i = 0; i < NUM_CHANNEL; i++) {
                Channel channel = connection.createChannel();
                channel.queueDeclare(Constant.QUEUE_NAME, false, false, false, null);
                channelPool.add(channel);
            };
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        StringBuilder payload = new StringBuilder();
        String line;
        while ((line= request.getReader().readLine()) != null) {
            payload.append(line);
        }
        JsonObject jsonObject = gson.fromJson(payload.toString(), JsonObject.class);
        String leftOrRight = urlParts[1];
        jsonObject.addProperty("leftOrRight", leftOrRight);
        String message = gson.toJson(jsonObject);
        SwipeReqBody swipeReqBody = gson.fromJson(message, SwipeReqBody.class);
        // gson will raise NumberFormatException if IDs can not be parsed into integer.
        if (!isValidReqBody(swipeReqBody)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Constant.INVALID_REQ_BODY);
        }
        Channel channel = null;
        try {
            channel = channelPool.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (channel != null) {
            channel.basicPublish("", Constant.QUEUE_NAME, null, message.getBytes());
            response.setStatus(HttpServletResponse.SC_CREATED);
            System.out.println("Sent " + message + " to rabbitmq");
            response.getWriter().write("Sent " + message + " to rabbitmq");
            channelPool.add(channel);
        } else{
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    private boolean isValidUrlPath (String urlPath){
        return urlPath != null && !urlPath.isEmpty();
    }

    private boolean isValidUrlParts (String[]urlParts){
        if (urlParts.length == 2) {
            return (urlParts[1].equals("left") || urlParts[1].equals("right"));
        }
        return false;
    }

    private boolean isValidReqBody(SwipeReqBody swipeReqBody) {
        if (swipeReqBody.getSwiper() < Constant.SWIPER_LOWER_BOUND ||
            swipeReqBody.getSwiper() > Constant.SWIPER_UPPER_BOUND) {
            return false;
        }
        if (swipeReqBody.getSwipee() < Constant.SWIPEE_LOWER_BOUND ||
            swipeReqBody.getSwipee() > Constant.SWIPEE_UPPER_BOUND) {
            return false;
        }
        return swipeReqBody.getComment().length() >= Constant.COMMENT_MIN_LENGTH &&
            swipeReqBody.getComment().length() <= Constant.COMMENT_MAX_LENGTH;
    }
}
