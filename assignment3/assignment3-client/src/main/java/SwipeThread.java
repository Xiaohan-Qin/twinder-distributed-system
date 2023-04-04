import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwipeThread implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SwipeThread.class);
  private final CountDownLatch latch;
  private final int numRequests;

  public SwipeThread(CountDownLatch latch, int numRequests) {
    this.latch = latch;
    this.numRequests = numRequests;
  }

  @Override
  public void run() {
    String BASE_PATH = "http://" + Constant.SERVER_IP + ":8080/assignment3-server/";
    SwipeApi swipeInstance = new SwipeApi();
    ApiClient swipeClient = swipeInstance.getApiClient();
    swipeClient.setBasePath(BASE_PATH);

    for (int i = 0; i < numRequests; i++) {
      // Generate request body
      int swiperId =
          ThreadLocalRandom.current().nextInt(Constant.SWIPER_UPPER_BOUND)
              + Constant.SWIPER_LOWER_BOUND;
      String swiperString = Integer.toString(swiperId);
      int swipeeId =
          ThreadLocalRandom.current().nextInt(Constant.SWIPEE_UPPER_BOUND)
              + Constant.SWIPEE_LOWER_BOUND;
      String swipeeString = Integer.toString(swipeeId);
      int randomLength = ThreadLocalRandom.current().nextInt(Constant.COMMENT_MAX_LENGTH) + 1;
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < randomLength; j++) {
        sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
      }
      String commentString = sb.toString();
      SwipeDetails swipeDetails = new SwipeDetails();
      swipeDetails.setSwiper(swiperString);
      swipeDetails.setSwipee(swipeeString);
      swipeDetails.setComment(commentString);

      // Generate random left or right url path
      String leftorright = "left";
      int randInt = ThreadLocalRandom.current().nextInt(2);
      if (randInt == 1) {
        leftorright = "right";
      }

      // execute post requests
      int curTurn = 0;
      int statusCode = 0;
      long startTime = System.currentTimeMillis();
      while (curTurn < Constant.RETRIES) {
        try {
          statusCode = swipeInstance.swipeWithHttpInfo(swipeDetails, leftorright).getStatusCode();
//          LOGGER.info(Thread.currentThread().getId() + " - POST request " + i + " succeeds");
          break;
        } catch (ApiException apiExp) {
          curTurn++;
          statusCode = apiExp.getCode();
          LOGGER.warn("Code: "+ statusCode + ", Message: " +apiExp.getMessage());
          LOGGER.warn("Request body: " + swipeDetails);
          try {
            Thread.sleep(5);
          } catch (InterruptedException ignore) {
            return;
          }
        }
      }
      long endTime = System.currentTimeMillis();
      Record curRecord= Record.getRecord();
      curRecord.setRequestType("POST");
      curRecord.setStartTime(startTime);
      curRecord.setEndTime(endTime);
      curRecord.setLatency(endTime-startTime);
      curRecord.setRespondCode(statusCode);
      Utils.postRecordsQueue.add(curRecord);
      if (curTurn < Constant.RETRIES) {
        Utils.SWIPE_SUCCESS_COUNT.incrementAndGet();
      } else {
        Utils.SWIPE_FAILURE_COUNT.incrementAndGet();
      }
    }
    this.latch.countDown();
  }
}

