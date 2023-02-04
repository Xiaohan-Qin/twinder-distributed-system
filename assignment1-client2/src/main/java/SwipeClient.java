import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwipeClient {

  public static void main(String[] args) throws InterruptedException, IOException {
    long startTime = System.currentTimeMillis();

    // create thread pool and submit tasks
    ExecutorService threadPool = Executors.newFixedThreadPool(Constant.NUM_THREADS);
    CountDownLatch latch = new CountDownLatch(Constant.NUM_TASKS);
    for (int i = 0; i < Constant.NUM_TASKS; i++) {
      threadPool.submit(new SwipeThread(latch));
    }
    // wait for all threads to finish
    latch.await();
    long endTime = System.currentTimeMillis();
    long totalExecutionTime = endTime - startTime;
    long throughPut = Constant.NUM_TASKS / (totalExecutionTime / 1000);
    threadPool.shutdown();

    System.out.println(
        "Total execution time in milliseconds: " + totalExecutionTime + "\n"
        + "Total Successful requests: " + Utils.successCount.get() + "\n"
        + "Total failed requests: " + Utils.failureCount.get() + "\n"
        + "Total throughput in requests per second: " + throughPut
    );
    RecordUtils recordUtil = new RecordUtils("../../../res/records/"
        + Constant.NUM_THREADS + "_" + Constant.NUM_TASKS + ".csv");
    recordUtil.outputStatistics();
  }
}
