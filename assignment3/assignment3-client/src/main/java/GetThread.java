import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetThread implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetThread.class);
  private final CountDownLatch latch;
  private final int numRequests;

  public GetThread(CountDownLatch latch, int numRequests) {
    this.latch = latch;
    this.numRequests = numRequests;
  }

  @Override
  public void run() {
//    String MATCHES_PATH =
//        "http://" + Constant.SERVER_IP + ":8080/assignment3_server_war_exploded/matches/";
//    String STATS_PATH =
//        "http://" + Constant.SERVER_IP + ":8080/assignment3_server_war_exploded/stats/";
    String MATCHES_PATH =
        "http://" + Constant.SERVER_IP + ":8080/assignment3-server/matches/";
    String STATS_PATH =
        "http://" + Constant.SERVER_IP + ":8080/assignment3-server/stats/";

    for (int i = 0; i < numRequests; i++) {
      String BASE_PATH = STATS_PATH;
      int randInt = ThreadLocalRandom.current().nextInt(2);
      if (randInt == 1) {
        BASE_PATH = MATCHES_PATH;
      }
      int userId = ThreadLocalRandom.current().nextInt(Constant.SWIPER_UPPER_BOUND + 1)
          + Constant.SWIPER_LOWER_BOUND;
      String userString = Integer.toString(userId);
      long startTime = System.currentTimeMillis();
      long endTime;
      int statusCode = -1;
      try {
        URL url = new URL(BASE_PATH + userString + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        statusCode = connection.getResponseCode();
        endTime = System.currentTimeMillis();
        Utils.GET_SUCCESS_COUNT.incrementAndGet();
//        LOGGER.info(Thread.currentThread().getId() + " - GET request " + i + " succeeds");
      } catch (Exception e) {
        endTime = System.currentTimeMillis();
        Utils.GET_FAILURE_COUNT.incrementAndGet();
//        LOGGER.warn(Thread.currentThread().getId() + " - GET request " + i + " failed");
      }
      Record curRecord= Record.getRecord();
      curRecord.setRequestType("GET");
      curRecord.setStartTime(startTime);
      curRecord.setEndTime(endTime);
      curRecord.setLatency(endTime-startTime);
      curRecord.setRespondCode(statusCode);
      Utils.getRecordsQueue.add(curRecord);
      try {
        Thread.sleep(200); // send 5 requests per second
      } catch (InterruptedException ignore){
        return;
      }
    }
    this.latch.countDown();
  }
}
