package Models;

public class SwipeReqBody {
  private int swiper;
  private int swipee;
  private String comment;
  private String leftOrRight;

  public SwipeReqBody(int swiper, int swipee, String comment, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }

  public int getSwiper() {
    return swiper;
  }

  public void setSwiper(int swiper) {
    this.swiper = swiper;
  }

  public int getSwipee() {
    return swipee;
  }

  public void setSwipee(int swipee) {
    this.swipee = swipee;
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
