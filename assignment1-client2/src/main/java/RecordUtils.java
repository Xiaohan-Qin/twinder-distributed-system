import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

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
  public void outputStatistics() throws IOException {
    double minLatency = Double.POSITIVE_INFINITY;
    double maxLatency = Double.NEGATIVE_INFINITY;
    double sumLatency = 0;
    double medianLatency = Utils.records.get((int)(0.5 * Utils.records.size())).getLatency();
    double p99Latency = Utils.records.get((int)(0.99 * Utils.records.size())).getLatency();
    Collections.sort(Utils.records);
    for (Record record : Utils.records) {
      writeRecord(record);
      maxLatency = Math.max(record.getLatency(), maxLatency);
      minLatency = Math.min(record.getLatency(), minLatency);
      sumLatency += record.getLatency();
    }
    double meanLatency = sumLatency / Utils.records.size();
    System.out.println(
        "Mean response time: " + meanLatency + "\n"
        + "Median response time: " + meanLatency + "\n"
        + "p99 response time: " + p99Latency + "\n"
        + "Max response time: " + maxLatency + "\n"
        + "Min response time: " + minLatency);
  }
}
