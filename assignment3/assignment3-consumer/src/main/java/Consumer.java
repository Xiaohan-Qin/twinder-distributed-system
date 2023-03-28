import ConnectionManagers.DatabaseConnectionManager;
import ConnectionManagers.RabbitMQConnectionManager;
import ConnectionManagers.DatabaseConnectionPool;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Consumer {
  private final Connection rabbitmqConnection;
  private String queueName;
  private Integer numThreads;
  private final ConsumerUtils consumerUtils;

  public Consumer(){
    RabbitMQConnectionManager rabbitmqConnectionManager = new RabbitMQConnectionManager();
    rabbitmqConnection = rabbitmqConnectionManager.getConnection();
    setNumThreads(Constant.NUM_THREADS);
    setQueueName(Constant.QUEUE_NAME);
    consumerUtils = new ConsumerUtils();
  }

  public Integer getNumThreads() {
    return numThreads;
  }

  public void setNumThreads(Integer numThreads) {
    this.numThreads = numThreads;
  }

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public Connection getRabbitmqConnection() {
    return rabbitmqConnection;
  }

  public ConsumerUtils getConsumerUtils() {
    return consumerUtils;
  }

}
