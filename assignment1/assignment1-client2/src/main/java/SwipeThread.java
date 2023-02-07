import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

class SwipeThread implements Runnable {

  private final CountDownLatch latch;
  private final int numRequests;

  public SwipeThread(CountDownLatch latch, int numRequests) {
    this.latch = latch;
    this.numRequests = numRequests;
  }

  @Override
  public void run() {
    final String BASE_PATH = "http://" + Constant.SERVER_IP + ":8080/assignment1-server_war/";
    SwipeApi swipeInstance = new SwipeApi();
    ApiClient swipeClient = swipeInstance.getApiClient();
    swipeClient.setBasePath(BASE_PATH);

    // Generate random swiper ID and transform it to string
    int swiperId =
        ThreadLocalRandom.current().nextInt(Constant.SWIPER_UPPER_BOUND + 1)
            + Constant.SWIPER_LOWER_BOUND;
    String swiperString = Integer.toString(swiperId);

    // Generate random swipee ID and transform it to string
    int swipeeId =
        ThreadLocalRandom.current().nextInt(Constant.SWIPEE_UPPER_BOUND + 1)
            + Constant.SWIPEE_LOWER_BOUND;
    String swipeeString = Integer.toString(swipeeId);

    // Generate random comment string with length less than 256
    int randomLength = ThreadLocalRandom.current().nextInt(Constant.COMMENT_MAX_LENGTH + 1) + 1;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < randomLength; i++) {
      sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
    }
    String commentString = sb.toString();

    SwipeDetails swipeDetails = new SwipeDetails();
    swipeDetails.setSwiper(swiperString);
    swipeDetails.setSwipee(swipeeString);
    swipeDetails.setComment(commentString);

    String leftorright = "left";
    int randInt = ThreadLocalRandom.current().nextInt(2);
    if (randInt == 1) {
      leftorright = "right";
    }
    // execute post request
    for (int i = 0; i < numRequests; i++) {
      int curTurn = 0;
      int statusCode = 0;
      long startTime = System.currentTimeMillis();
      while (curTurn < Constant.RETRIES) {
        try {
//          ApiResponse<Void> response = swipeInstance.swipeWithHttpInfo(swipeDetails, leftorright);
//          statusCode = response.getStatusCode();
          statusCode = swipeInstance.swipeWithHttpInfo(swipeDetails, leftorright).getStatusCode();
          break;
        } catch (ApiException apiExp) {
          curTurn++;
          statusCode = apiExp.getCode();
          try {
            Thread.sleep(5);
          } catch (InterruptedException interruptExp) {
            interruptExp.printStackTrace();
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
//      Record curRecord = new Record("POST", startTime, endTime, endTime - startTime,
//          statusCode);
      Utils.recordsQueue.add(curRecord);
      if (curTurn < Constant.RETRIES) {
        Utils.successCount.incrementAndGet();
      } else {
        Utils.failureCount.incrementAndGet();
      }
    }
    this.latch.countDown();
  }
}


