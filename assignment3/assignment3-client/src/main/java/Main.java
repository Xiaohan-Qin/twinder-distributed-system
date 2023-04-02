import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final int numPostThreads = Constant.NUM_POST_THREADS;
  private static final int numPostTasks = Constant.NUM_POST_TASKS;
  private static final int numGetThreads = Constant.NUM_GET_THREADS;
  private static final int numGetTasks = Constant.NUM_GET_TASKS;

  public static void main(String[] args) throws InterruptedException, IOException {
    long startTime = System.currentTimeMillis();
    // start post threads
    ExecutorService postPool = Executors.newFixedThreadPool(numPostThreads);
    CountDownLatch postLatch = new CountDownLatch(numPostThreads);
    for (int i = 0; i < numPostThreads; i++) {
      postPool.submit(new SwipeThread(postLatch, numPostTasks / numPostThreads));
      LOGGER.info("post thread " + i + " started");
    }
    // Wait for all post threads to start
    postLatch.await();
    // start get thread
    ExecutorService getPool = Executors.newFixedThreadPool(numGetThreads);
    CountDownLatch getLatch = new CountDownLatch(numGetThreads);
    for (int i = 0; i < numGetThreads; i++) {
      getPool.submit(new GetThread(getLatch, numGetTasks / numGetThreads));
      LOGGER.info("get thread " + i + " started");
    }
    // Wait for all posting threads to finish
    postPool.shutdown();
    LOGGER.info("Post threads finished");

    // Signal the get threads to stop
    getLatch.countDown();
    getPool.shutdownNow();
    LOGGER.info("Get thread terminated");

    long endTime = System.currentTimeMillis();
    long totalExecutionTime = endTime - startTime;
    int numExecutedGetTasks = Utils.GET_FAILURE_COUNT.get() + Utils.GET_SUCCESS_COUNT.get();
    long throughPut = (numExecutedGetTasks + numPostTasks) / (totalExecutionTime);
    System.out.println(
            "Successful post requests: " + Utils.SWIPE_SUCCESS_COUNT.get() + "\n"
            + "Failed post requests: " + Utils.SWIPE_FAILURE_COUNT.get() + "\n"
            + "Successful get requests: " + Utils.GET_SUCCESS_COUNT.get() + "\n"
            + "Failed get requests: " + Utils.GET_FAILURE_COUNT.get() + "\n"
            + "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
            + "Total throughput in requests per second: " + throughPut + "\n"
    );
    RecordUtils postRecordUtil = new RecordUtils("./res/records/"
        + "post" + "_" + numPostThreads + "_" + numPostTasks + ".csv");
    RecordUtils getRecordUtil = new RecordUtils("./res/records/"
        + "get" + "_" + numGetThreads + "_" + numExecutedGetTasks + ".csv");
    postRecordUtil.outputStatistics("POST", Utils.postRecordsQueue);
    getRecordUtil.outputStatistics("GET", Utils.postRecordsQueue);
  }
}

