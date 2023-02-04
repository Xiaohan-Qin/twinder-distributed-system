import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

class SwipeThread implements Runnable {

  private final CountDownLatch latch;

  public SwipeThread(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void run() {
    String BASE_PATH = "http://54.201.143.101:8080/assignment1-server_war/";
    SwipeApi swipeInstance = new SwipeApi();
    ApiClient swipeClient = swipeInstance.getApiClient();
    swipeClient.setBasePath(BASE_PATH);

    // Generate random swiper ID and transform it to string
    int swiperId =
        ThreadLocalRandom.current().nextInt(Constant.SWIPER_UPPER_BOUND + 1) + Constant.SWIPER_LOWER_BOUND;
    String swiperString = Integer.toString(swiperId);

    // Generate random swipee ID and transform it to string
    int swipeeId =
        ThreadLocalRandom.current().nextInt(Constant.SWIPEE_UPPER_BOUND + 1) + Constant.SWIPEE_LOWER_BOUND;
    String swipeeString = Integer.toString(swipeeId);

    // Generate random comment string with length less than 256
    int randomLength = ThreadLocalRandom.current().nextInt(Constant.COMMENT_MAX_LENGTH + 1);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < randomLength; i++) {
      sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
    }
    String commentString = sb.toString();

    // Create swipe request body
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

    // execute post request
    int curTurn = 0;
    while (curTurn < Constant.RETRIES) {
      try {
        long startTime = System.currentTimeMillis();
        ApiResponse<Void> response = swipeInstance.swipeWithHttpInfo(swipeDetails, leftorright);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        // add current record to list of records
        Record curRecord = new Record("POST", startTime, endTime, latency, response.getStatusCode());
        Utils.records.add(curRecord);
        break;
      } catch (ApiException e) {
        curTurn++;
        try {
          Thread.sleep(5);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
    if (curTurn < Constant.RETRIES) {
      Utils.successCount.incrementAndGet();
    }
    else{
      Utils.failureCount.incrementAndGet();
    }
    this.latch.countDown();
  }
}


