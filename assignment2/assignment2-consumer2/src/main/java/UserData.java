import java.util.ArrayList;
import java.util.List;

public class UserData {
  private final List<Integer> likedUserIds;

  public UserData() {
    this.likedUserIds = new ArrayList<>();
  }

  public List<Integer> getLikedUserIds() {
    return likedUserIds;
  }

  public void addLikedUserId(int swipeeId) {
    this.likedUserIds.add(swipeeId);
  }

  public List<Integer> getTopLikedUserIds() {
    int upperBound = Math.min(100, this.likedUserIds.size());
    List<Integer> topLikedUserIds = new ArrayList<>();
    for (int i = 0; i < upperBound; i++) {
      topLikedUserIds.add(this.likedUserIds.get(i));
    }
    return topLikedUserIds;
  }
}
