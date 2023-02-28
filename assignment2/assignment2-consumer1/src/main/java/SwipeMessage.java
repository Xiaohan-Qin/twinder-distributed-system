public class SwipeMessage {
  private int swiperId;
  private int swipeeId;
  private String leftOrRight;

  public SwipeMessage(int swiperId, int swipeeId, String leftOrRight) {
    this.swiperId = swiperId;
    this.swipeeId = swipeeId;
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

  public String getLeftOrRight() {
    return leftOrRight;
  }

  public void setLeftOrRight(String leftOrRight) {
    this.leftOrRight = leftOrRight;
  }
}
