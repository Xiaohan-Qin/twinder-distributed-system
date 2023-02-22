import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumerUtils {
  private final Map<Integer, UserData> userDataMap;

  public ConsumerUtils() {
    this.userDataMap = new HashMap<>();
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
      userData.incrementLikes();
      userData.addLikedUserId(swipeeId);
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

  public List<Integer> getTopLikedUsersForUser(int swiperId) {
    UserData userData = userDataMap.get(swiperId);
    if (userData != null) {
      return userData.getLikedUserIds();
    }
    return Collections.emptyList();
  }
}
