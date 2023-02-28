import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
  private Connection connection;
  private Integer numThreads;
  private String queueName;
  private String exchangeName;
  private final ConsumerUtils consumerUtils;
  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

  public Consumer(){
    consumerUtils = new ConsumerUtils();
    ConnectionFactory conFactory = new ConnectionFactory();
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("rabbitmq.conf");
    assert is != null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    LOGGER.info("Connecting to rabbitMQ...");
    try {
      if(!reader.ready()) return;
      conFactory.setHost(reader.readLine());
      conFactory.setPort(Integer.parseInt(reader.readLine()));
      conFactory.setUsername(reader.readLine());
      conFactory.setPassword(reader.readLine());
      conFactory.setVirtualHost(reader.readLine());
      setNumThreads(Constant.NUM_THREADS);
      setExchangeName(Constant.EXCHANGE_NAME);
      setQueueName(Constant.QUEUE_NAME);
      connection = conFactory.newConnection();
      LOGGER.info("Connect to rabbitMQ successfully");
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  public Integer getNumThreads() {
    return numThreads;
  }

  public void setNumThreads(Integer numThreads) {
    this.numThreads = numThreads;
  }

  public String getExchangeName() {
    return exchangeName;
  }

  public void setExchangeName(String exchangeName) {
    this.exchangeName = exchangeName;
  }

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public Connection getConnection() {
    return connection;
  }

  public ConsumerUtils getConsumerUtils() {
    return consumerUtils;
  }
}

