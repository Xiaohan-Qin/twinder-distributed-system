import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwipeClient {
  final static private int NUM_THREADS = 300;
  final static private int NUM_TASKS = 500;

  public static void main(String[] args) throws InterruptedException {
    // record start time
    long startTime = System.currentTimeMillis();

    // Create thread pool
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    CountDownLatch latch = new CountDownLatch(NUM_TASKS);

    // Submit tasks to thread pool
    for (int i = 0; i < NUM_TASKS; i++) {
      threadPool.submit(new SwipeThread(latch));
    }

    // Wait for all threads to finish
    latch.await();

    // record end time
    long endTime = System.currentTimeMillis();
    long totalExecutionTime = endTime - startTime;

    // Shut down the thread pool
    threadPool.shutdown();

    System.out.println("Total execution time: " + totalExecutionTime + " milliseconds");
    System.out.println("Successful requests: " + Arguments.successCount.get());
    System.out.println("Failed requests: " + Arguments.failureCount.get());

  }
}
