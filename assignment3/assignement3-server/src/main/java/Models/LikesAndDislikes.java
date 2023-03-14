package Models;

public class LikesAndDislikes {
  private int likes;
  private int dislikes;

  public LikesAndDislikes(int likes, int dislikes) {
    this.likes = likes;
    this.dislikes = dislikes;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public int getDislikes() {
    return dislikes;
  }

  public void setDislikes(int dislikes) {
    this.dislikes = dislikes;
  }

  @Override
  public java.lang.String toString() {
    return "LikesAndDislikes{" +
        "likes=" + likes +
        ", dislikes=" + dislikes +
        '}';
  }
}
