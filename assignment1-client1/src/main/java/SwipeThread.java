import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

class SwipeThread implements Runnable {

  public static final int RETRIES = 5;
  public static final int SWIPER_LOWER_BOUND = 1;
  public static final int SWIPER_UPPER_BOUND = 5000;
  public static final int SWIPEE_LOWER_BOUND = 1;
  public static final int SWIPEE_UPPER_BOUND = 1000000;
  public static final int COMMENT_MAX_LENGTH = 256;

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
        ThreadLocalRandom.current().nextInt(SWIPER_UPPER_BOUND + 1) + SWIPER_LOWER_BOUND;
    String swiperString = Integer.toString(swiperId);

    // Generate random swipee ID and transform it to string
    int swipeeId =
        ThreadLocalRandom.current().nextInt(SWIPEE_UPPER_BOUND + 1) + SWIPEE_LOWER_BOUND;
    String swipeeString = Integer.toString(swipeeId);

    // Generate random comment string with length less than 256
    int randomLength = ThreadLocalRandom.current().nextInt(COMMENT_MAX_LENGTH + 1);
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
    while (curTurn < RETRIES) {
      try {
        swipeInstance.swipe(swipeDetails, leftorright);
        break;
      } catch (ApiException e) {
        curTurn++;
        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
    if (curTurn < RETRIES) {
      Arguments.successCount.incrementAndGet();
    }
    else{
      Arguments.failureCount.incrementAndGet();}
    this.latch.countDown();
  }
}

