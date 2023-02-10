package xiaohan.cs6650.assignment1springserver;


import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwipeController {

  private boolean isValidURL(@PathVariable String leftorright) {
    return leftorright.equals("left") || leftorright.equals("right");
  }

  @PostMapping("/swipe/{leftorright}")
  public ResponseEntity<Object> doPost(@PathVariable String leftorright, @RequestBody @Valid SwipeReqBody requestBody) {
    boolean isValidURL = isValidURL(leftorright);
    if (isValidURL) {
      return ResponseEntity.ok("Swipe processed successfully");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid url");
  }
}


