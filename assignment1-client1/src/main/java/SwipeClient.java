import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwipeClient {

  public static void main(String[] args) throws InterruptedException {
    // record start time
    long startTime = System.currentTimeMillis();

    // Create thread pool
    ExecutorService threadPool = Executors.newFixedThreadPool(Constant.NUM_THREADS);
    CountDownLatch latch = new CountDownLatch(Constant.NUM_THREADS);

    // Submit tasks to thread pool
    for (int i = 0; i < Constant.NUM_THREADS; i++) {
      threadPool.submit(new SwipeThread(latch, Constant.NUM_TASKS/Constant.NUM_THREADS));
    }

    // Wait for all threads to finish
    latch.await();

    // record end time
    long endTime = System.currentTimeMillis();
    long totalExecutionTime = endTime - startTime;

    long throughPut = Constant.NUM_TASKS / (totalExecutionTime / 1000);

    // Shut down the thread pool
    threadPool.shutdown();

    System.out.println(
        "Total run time (wall time) in ms: " + totalExecutionTime + "\n"
        + "Successful requests: " + Utils.successCount.get() + "\n"
        + "Failed requests: " + Utils.failureCount.get() + "\n"
        + "Total throughput in requests per second: " + throughPut
    );
  }
}
