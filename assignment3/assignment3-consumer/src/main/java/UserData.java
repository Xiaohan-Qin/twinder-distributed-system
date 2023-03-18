import java.util.ArrayList;
import java.util.List;

public class UserData {

  private int numLikes;
  private int numDislikes;
  private final List<Integer> likedUserIds;

  public UserData() {
    this.numLikes = 0;
    this.numDislikes = 0;
    this.likedUserIds = new ArrayList<>();
  }

  public int getNumLikes() {
    return numLikes;
  }

  public void incrementLikes() {
    this.numLikes += 1;
  }

  public int getNumDislikes() {
    return this.numDislikes;
  }

  public void incrementDislikes() {
    this.numDislikes += 1;
  }

  public void addLikedUserId(int swipeeId) {
    this.likedUserIds.add(swipeeId);
  }

  public List<Integer> getLikedUserIds() {
    int upperBound = Math.min(100, this.likedUserIds.size());
    List<Integer> topLikedUserIds = new ArrayList<>();
    for (int i = 0; i < upperBound; i++) {
      topLikedUserIds.add(this.likedUserIds.get(i));
    }
    return topLikedUserIds;
  }

}
