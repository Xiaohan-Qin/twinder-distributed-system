package xiaohan.cs6650.assignment1springserver;



import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class SwipeReqBody {
  @NotNull
  @Min(value=Constant.SWIPER_LOWER_BOUND)
  @Max(value=Constant.SWIPER_UPPER_BOUND)
  private int swiper;
  @NotNull
  @Min(value=Constant.SWIPEE_LOWER_BOUND)
  @Max(value=Constant.SWIPEE_UPPER_BOUND)
  private int swipee;
  @NotNull
  @Size(min=1, max=Constant.COMMENT_MAX_LENGTH)
  private String comment;
}
