public class UserData {

  private int numLikes;
  private int numDislikes;

  public UserData() {
    this.numLikes = 0;
    this.numDislikes = 0;
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

}
