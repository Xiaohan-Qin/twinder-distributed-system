package ConnectionManagers;

import Constants.Constant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RabbitmqConnectionManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqConnectionManager.class);
  private Connection rabbitmqConnection;

  public RabbitmqConnectionManager() {
    ConnectionFactory conFactory = new ConnectionFactory();
    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(
        "rabbitmq.properties")) {
      if (is == null) {
        LOGGER.error("rabbitmq.properties file not found.");
        return;
      }
      Properties props = new Properties();
      props.load(is);
      String host = props.getProperty("host");
      int port = Integer.parseInt(props.getProperty("port"));
      String username = props.getProperty("username");
      String password = props.getProperty("password");
      String virtualHost = props.getProperty("virtualhost");

      conFactory.setHost(host);
      conFactory.setPort(port);
      conFactory.setUsername(username);
      conFactory.setPassword(password);
      conFactory.setVirtualHost(virtualHost);

      rabbitmqConnection = conFactory.newConnection();
      LOGGER.info("Connected to RabbitMQ successfully");
    } catch (IOException | TimeoutException | NumberFormatException e) {
      LOGGER.error("Failed to connect to RabbitMQ: ", e);
    }
  }

  public Connection getConnection() {
    return rabbitmqConnection;
  }

  public BlockingQueue<Channel> createChannelPool(int numChannels) {
    BlockingQueue<Channel> channelPool = new LinkedBlockingQueue<>();
    try {
      for (int i = 0; i < numChannels; i++) {
        Channel channel = rabbitmqConnection.createChannel();
        channel.queueDeclare(Constant.QUEUE_NAME, true, false, false, null); // durable queue
        channelPool.add(channel);
      }
    } catch (IOException e) {
      LOGGER.error("Failed to create channel pool: ", e);
    }
    return channelPool;
  }
}
