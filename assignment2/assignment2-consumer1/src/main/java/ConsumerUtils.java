import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerUtils {
  private final Map<Integer, UserData> userDataMap;

  public ConsumerUtils() {
    this.userDataMap =  new ConcurrentHashMap<>();
  }

  public void processSwipeMessage(SwipeMessage message) {
    int swiperId = message.getSwiperId();
    String leftOrRight = message.getLeftOrRight();
    UserData userData = userDataMap.get(swiperId);
    if (userData == null) {
      userData = new UserData();
      userDataMap.put(swiperId, userData);
    }
    if (leftOrRight.equals("right")) {
      userData.incrementLikes();
    } else if (leftOrRight.equals("left")) {
      userData.incrementDislikes();
    }
  }

  public int getLikesForUser(int swiperId) {
    UserData userData = userDataMap.get(swiperId);
    if (userData != null) {
      return userData.getNumLikes();
    }
    return 0;
  }

  public int getDislikesForUser(int swiperId) {
    UserData userData = userDataMap.get(swiperId);
    if (userData != null) {
      return userData.getNumDislikes();
    }
    return 0;
  }
}
