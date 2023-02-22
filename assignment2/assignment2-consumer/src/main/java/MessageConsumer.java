import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class MessageConsumer {

  private static final String EXCHANGE_NAME = "likes";
  private static final String QUEUE_NAME = "likes_queue";

  private Map<String, Map<String, Integer>> userLikes = new HashMap<>();
  private Map<String, List<String>> userMatches = new HashMap<>();

  public MessageConsumer() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
    channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      processMessage(message);
    }, consumerTag -> {
    });
  }

  private void processMessage(String message) {
    String[] parts = message.split(":");
    String userId = parts[0];
    String otherUserId = parts[1];
    String action = parts[2];
    if (action.equals("left")) {
      addLike(userId, otherUserId);
      addMatch(userId, otherUserId);
    } else if (action.equals("right")) {
      addDislike(userId, otherUserId);
    }
  }

  private void addLike(String userId, String otherUserId) {
    Map<String, Integer> likes = userLikes.computeIfAbsent(userId, k -> new HashMap<>());
    likes.put(otherUserId, 1);
  }

  private void addDislike(String userId, String otherUserId) {
    Map<String, Integer> likes = userLikes.computeIfAbsent(userId, k -> new HashMap<>());
    likes.put(otherUserId, -1);
  }

  private void addMatch(String userId, String otherUserId) {
    List<String> matches = userMatches.computeIfAbsent(userId, k -> new ArrayList<>());
    matches.add(otherUserId);
  }

  public int getLikes(String userId) {
    int likes = 0;
    Map<String, Integer> userLikesMap = userLikes.get(userId);
    if (userLikesMap != null) {
      for (int like : userLikesMap.values()) {
        if (like == 1) {
          likes++;
        }
      }
    }
    return likes;
  }

  public int getDislikes(String userId) {
    int dislikes = 0;
    Map<String, Integer> userLikesMap = userLikes.get(userId);
    if (userLikesMap != null) {
      for (int like : userLikesMap.values()) {
        if (like == -1) {
          dislikes++;
        }
      }
    }
    return dislikes;
  }

  public List<String> getMatches(String userId) {
    List<String> matches = userMatches.get(userId);
    if (matches == null) {
      matches = new ArrayList<>();
    }
    return matches.subList(0, Math.min(matches.size(), 100));
  }

  public static void main(String[] args) throws IOException, TimeoutException {
    try {
      MessageConsumer consumer = new MessageConsumer();
      System.out.println("Waiting for");
    } finally {
      // do something
    }
  }
}