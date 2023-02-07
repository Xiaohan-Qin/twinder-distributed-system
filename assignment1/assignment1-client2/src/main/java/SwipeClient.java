import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwipeClient {

  public static void main(String[] args) throws InterruptedException, IOException {
    long startTime = System.currentTimeMillis();

    ExecutorService threadPool = Executors.newFixedThreadPool(Constant.NUM_THREADS);
    CountDownLatch latch = new CountDownLatch(Constant.NUM_THREADS);
    for (int i = 0; i < Constant.NUM_THREADS; i++) {
      threadPool.submit(new SwipeThread(latch, Constant.NUM_TASKS/Constant.NUM_THREADS));
    }
    latch.await();
    long endTime = System.currentTimeMillis();
    long totalExecutionTime = endTime - startTime;
    long throughPut = Constant.NUM_TASKS / (totalExecutionTime / 1000);
    threadPool.shutdown();

    System.out.println(
        "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
        + "Total Successful requests: " + Utils.successCount.get() + "\n"
        + "Total failed requests: " + Utils.failureCount.get() + "\n"
        + "Total throughput in requests per second: " + throughPut
    );
    RecordUtils recordUtil = new RecordUtils("./res/records/"
        + Constant.NUM_THREADS + "_" + Constant.NUM_TASKS + ".csv");
    recordUtil.outputStatistics();
  }
}
