import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class GetThread implements Runnable {
  private final CountDownLatch latch;
  private final int numRequests;

  public GetThread(CountDownLatch latch, int numRequests) {
    this.latch = latch;
    this.numRequests = numRequests;
  }

  @Override
  public void run() {
    String MATCHES_PATH =
        "http://" + Constant.SERVER_IP + ":8080/assignment3_server_war_exploded/matches/";
    String STATS_PATH =
        "http://" + Constant.SERVER_IP + ":8080/assignment3_server_war_exploded/stats/";

    for (int i = 0; i < numRequests; i++) {
      String BASE_PATH = STATS_PATH;
      int randInt = ThreadLocalRandom.current().nextInt(2);
      if (randInt == 1) {
        BASE_PATH = MATCHES_PATH;
      }
      int userId = ThreadLocalRandom.current().nextInt(Constant.SWIPER_UPPER_BOUND + 1)
          + Constant.SWIPER_LOWER_BOUND;
      String userString = Integer.toString(userId);
      try {
        URL url = new URL(BASE_PATH + userString + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        Thread.sleep(200); // send 5 requests per second
      } catch (Exception e) {
        System.out.println("GET request failed: " + e.getMessage());
      }
    }
    this.latch.countDown();
  }
}
