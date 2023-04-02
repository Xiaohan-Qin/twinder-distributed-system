import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class RecordUtils {
  private final FileWriter csvWriter;

  public RecordUtils(String filePath) throws IOException {
    this.csvWriter = new FileWriter(filePath);
    csvWriter.append("requestType, startTime, endTime, latency, responseCode\n");
  }

  public void writeRecord(Record record) throws IOException {
    this.csvWriter.append(record.toString());
  }

  // write records to csv file and print statistics
  public void outputStatistics(String requestType, Queue<Record> recordsQueue) throws IOException {
    List<Record> records = new ArrayList<>(recordsQueue);
    Collections.sort(records);
    double minLatency = Double.POSITIVE_INFINITY;
    double maxLatency = Double.NEGATIVE_INFINITY;
    double sumLatency = 0;
    double medianLatency = records.size()==0? 0 : records.get((int)(0.5 * records.size())).getLatency();
    double p99Latency = records.size()==0? 0 : records.get((int)(0.99 * records.size())).getLatency();
    for (Record record : records) {
      writeRecord(record);
      maxLatency = Math.max(record.getLatency(), maxLatency);
      minLatency = Math.min(record.getLatency(), minLatency);
      sumLatency += record.getLatency();
    }
    this.csvWriter.flush();
    double meanLatency = sumLatency / records.size();
    System.out.println(
        "Request type: " + requestType + "\n"
        + "Mean response time: " + meanLatency + "\n"
        + "Median response time: " + medianLatency + "\n"
        + "p99 response time: " + p99Latency + "\n"
        + "Max response time: " + maxLatency + "\n"
        + "Min response time: " + minLatency + "\n");
  }
}
