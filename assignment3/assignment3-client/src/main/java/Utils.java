import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
  public static final AtomicInteger SWIPE_SUCCESS_COUNT = new AtomicInteger(0);
  public static final AtomicInteger SWIPE_FAILURE_COUNT = new AtomicInteger(0);
  public static final AtomicInteger GET_SUCCESS_COUNT = new AtomicInteger(0);
  public static final AtomicInteger GET_FAILURE_COUNT = new AtomicInteger(0);
  public static Queue<Record> postRecordsQueue = new ConcurrentLinkedQueue<>();
  public static Queue<Record> getRecordsQueue = new ConcurrentLinkedQueue<>();
}

