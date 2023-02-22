public class SwipeReqBody {
  private int swiper;

  private int swipee;

  private String leftOrRight;

  public SwipeReqBody(int swiper, int swipee, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
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

  public String getLeftOrRight() {
    return leftOrRight;
  }

  public void setLeftOrRight(String leftOrRight) {
    this.leftOrRight = leftOrRight;
  }
}
