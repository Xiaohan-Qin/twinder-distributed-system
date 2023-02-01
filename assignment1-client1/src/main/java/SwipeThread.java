import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;


class SwipeThread implements Runnable {
  private final int taskId;

  public SwipeThread(int taskId) {
    this.taskId = taskId;
  }

  @Override
  public void run() {
    String BASE_PATH = "http://localhost:8080/hw1_war/";
  }
}

