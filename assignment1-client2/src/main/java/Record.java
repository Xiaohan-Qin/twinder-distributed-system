public class Record implements Comparable<Record>{
  private String requestType;
  private long startTime;
  private long endTime;
  private long latency;
  private int respondCode;

  public Record(String requestType, long startTime, long endTime, long latency, int respondCode) {
    this.requestType = requestType;
    this.startTime = startTime;
    this.endTime = endTime;
    this.latency = latency;
    this.respondCode = respondCode;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getLatency() {
    return latency;
  }

  public void setLatency(long latency) {
    this.latency = latency;
  }

  public int getRespondCode() {
    return respondCode;
  }

  public void setRespondCode(int respondCode) {
    this.respondCode = respondCode;
  }

  @Override
  public String toString() {
    return requestType + "," + startTime + "," + endTime + "," + latency + "," + respondCode + "\n";
  }

  @Override
  public int compareTo (Record otherRecord) {
    return Long.compare(this.latency, otherRecord.getLatency());
  }
}
