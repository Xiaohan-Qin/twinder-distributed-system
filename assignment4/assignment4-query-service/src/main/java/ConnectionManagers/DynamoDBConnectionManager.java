package ConnectionManagers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.regions.Regions;

public class DynamoDBConnectionManager {

  private static AmazonDynamoDB dynamoDbClient;

  private static void InitializeClient() {
    dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.US_WEST_2)
        .build();
  }

  public static synchronized AmazonDynamoDB getClient() {
    if (dynamoDbClient == null) {
      InitializeClient();
    }
    return dynamoDbClient;
  }
}