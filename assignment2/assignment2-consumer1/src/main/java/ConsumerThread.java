import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerThread implements Runnable {
  private final Connection connection;
  private final String exchangeName;
  private final String queueName;
  private final Gson gson = new Gson();
  private final ConsumerUtils consumerUtils;
  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThread.class);

  public ConsumerThread(Connection connection, String exchangeName, String queueName, ConsumerUtils consumerUtils){
    this.connection = connection;
    this.exchangeName = exchangeName;
    this.queueName = queueName;
    this.consumerUtils = consumerUtils;
  }

  // thread per channel model
  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(this.exchangeName, "fanout");
      channel.queueDeclare(this.queueName, false, false, false, null);
      channel.queueBind(this.queueName, this.exchangeName, "");
      channel.basicQos(10); // max 10 message per receiver
      final DeliverCallback deliverCallback = (consumerTag, delivery)-> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        SwipeMessage swipeMessage = gson.fromJson(message, SwipeMessage.class);
        consumerUtils.processSwipeMessage(swipeMessage);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        LOGGER.info(Thread.currentThread().getId() + " - thread received " + message);
      };
      channel.basicConsume(this.queueName, false ,deliverCallback, (consumerTag) -> {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
