import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {
  public static AtomicInteger successCount = new AtomicInteger(0);
  public static AtomicInteger failureCount = new AtomicInteger(0);
  public static List<Record> records = new ArrayList<>();
  public static Queue<Record> recordsQueue = new ConcurrentLinkedQueue<>();
}
