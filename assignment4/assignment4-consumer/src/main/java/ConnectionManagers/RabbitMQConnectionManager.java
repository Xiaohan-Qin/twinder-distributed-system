package ConnectionManagers;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

public class RabbitMQConnectionManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConnectionManager.class);
  private Connection rabbitmqConnection;

  public RabbitMQConnectionManager() {
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
}