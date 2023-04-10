package Models;

public class SwipeReqBody {
  private int swiperId;
  private int swipeeId;
  private String comment;
  private String leftOrRight;

  public SwipeReqBody(int swiperId, int swipee, String comment, String leftOrRight) {
    this.swiperId = swiperId;
    this.swipeeId = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }

  public int getSwiperId() {
    return swiperId;
  }

  public void setSwiperId(int swiperId) {
    this.swiperId = swiperId;
  }

  public int getSwipeeId() {
    return swipeeId;
  }

  public void setSwipeeId(int swipeeId) {
    this.swipeeId = swipeeId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getLeftOrRight() {
    return leftOrRight;
  }

  public void setLeftOrRight(String leftOrRight) {
    this.leftOrRight = leftOrRight;
  }
}

