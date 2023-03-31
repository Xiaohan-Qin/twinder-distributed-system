import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  private static final int numPostThreads = Constant.NUM_POST_THREADS;
  private static final int numPostTasks = Constant.NUM_POST_TASKS;
  private static final int numGetThreads = Constant.NUM_GET_THREADS;
  private static final int numGetTasks = Constant.NUM_GET_TASKS;

  public static void main(String[] args) throws InterruptedException {
    // start post threads
    ExecutorService postPool = Executors.newFixedThreadPool(numPostThreads);
    CountDownLatch postLatch = new CountDownLatch(numPostThreads);
    for (int i = 0; i < numPostThreads; i++) {
      postPool.submit(new SwipeThread(postLatch, numPostTasks / numPostThreads));
    }
    // start get thread
    ExecutorService getPool = Executors.newFixedThreadPool(numGetThreads);
    CountDownLatch getLatch = new CountDownLatch(numGetThreads);
    for (int i = 0; i < numGetThreads; i++) {
      getPool.submit(new GetThread(getLatch, numGetTasks / numGetThreads));
    }
    // Wait for all posting threads to finish
    postLatch.await();
    // terminates get thread
    getPool.shutdown();
  }
}

