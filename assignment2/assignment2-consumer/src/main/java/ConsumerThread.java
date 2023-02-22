import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumerThread implements Runnable {
  private final String queueName;
  private final Connection con;
  private final Gson gson = new Gson();
  private final ConsumerUtils consumerUtils;

  public ConsumerThread(String queueName, Connection con, ConsumerUtils consumerUtils){
    this.queueName = queueName;
    this.con = con;
    this.consumerUtils = consumerUtils;
  }

  // thread per channel model
  @Override
  public void run() {
    try {
      Channel channel = con.createChannel();
      channel.queueDeclare(queueName, false, false, false, null);
      channel.basicQos(10); // max 10 message per receiver

      final DeliverCallback deliverCallback = (consumerTag, delivery)-> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        SwipeMessage swipeMessage = gson.fromJson(message, SwipeMessage.class);
        consumerUtils.processSwipeMessage(swipeMessage);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        System.out.println(Thread.currentThread().getId() + " - thread received " + message);
      };
      channel.basicConsume(this.queueName, false ,deliverCallback, (consumerTag) -> {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
