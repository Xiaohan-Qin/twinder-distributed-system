import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.*;
import java.util.concurrent.*;

public class Consumer {
  private Connection con;
  private Integer numThreads;
  private String queueName;
  private ConsumerUtils consumerUtils;

  public Consumer(){
    consumerUtils = new ConsumerUtils();
    ConnectionFactory conFactory = new ConnectionFactory();
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("rabbitmq.conf");
    assert is != null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    System.out.println("Connecting...");
    try {
      if(!reader.ready()) return;
      conFactory.setHost(reader.readLine());
      conFactory.setPort(Integer.parseInt(reader.readLine()));
      conFactory.setUsername(reader.readLine());
      conFactory.setPassword(reader.readLine());
      conFactory.setVirtualHost(reader.readLine());
      setNumThreads(Integer.valueOf(reader.readLine()));
      setQueueName(reader.readLine());
      con = conFactory.newConnection();
      System.out.println("Connect successfully");
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

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public Connection getCon() {
    return con;
  }

  public ConsumerUtils getConsumerUtils() {
    return consumerUtils;
  }

  public void setConsumerUtils(ConsumerUtils consumerUtils) {
    this.consumerUtils = consumerUtils;
  }
}

