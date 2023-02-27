import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerUtils {
  private final Map<Integer, UserData> userDataMap;

  public ConsumerUtils() {
    this.userDataMap =  new ConcurrentHashMap<>();
  }

  public void processSwipeMessage(SwipeMessage message) {
    int swiperId = message.getSwiperId();
    int swipeeId = message.getSwipeeId();
    String leftOrRight = message.getLeftOrRight();
    UserData userData = userDataMap.get(swiperId);
    if (userData == null) {
      userData = new UserData();
      userDataMap.put(swiperId, userData);
    }
    if (leftOrRight.equals("right")) {
      userData.addLikedUserId(swipeeId);
    }
  }

  public List<Integer> getTopLikedUsersForUser(int swiperId) {
    UserData userData = userDataMap.get(swiperId);
    if (userData != null) {
      return userData.getLikedUserIds();
    }
    return Collections.emptyList();
  }
}
