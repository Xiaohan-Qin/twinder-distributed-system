import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {
  public static final AtomicInteger successCount = new AtomicInteger(0);
  public static final AtomicInteger failureCount = new AtomicInteger(0);
  public static final CopyOnWriteArrayList<Record> records = new CopyOnWriteArrayList<>();
}
