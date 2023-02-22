import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    Consumer consumer = new Consumer();
    ExecutorService pool = Executors.newFixedThreadPool(consumer.getNumThreads());
    for (int i = 0; i < consumer.getNumThreads(); i++) {
      pool.execute(new ConsumerThread(
          consumer.getQueueName(), consumer.getCon(), consumer.getConsumerUtils())
      );
    }
  }
}
