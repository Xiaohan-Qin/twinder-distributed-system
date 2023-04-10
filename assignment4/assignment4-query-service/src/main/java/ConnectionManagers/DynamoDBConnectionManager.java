package ConnectionManagers;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DynamoDBConnectionManager {

  private static DynamoDbClient dynamoDbClient;

  private static void readPropertiesAndInitializeClient() {
    Properties props = new Properties();
    try (InputStream is = DynamoDBConnectionManager.class.getClassLoader()
        .getResourceAsStream("dynamodb.properties")) {
      if (is == null) {
        throw new RuntimeException("dynamodb.properties file not found.");
      }
      props.load(is);
      String accessKey = props.getProperty("access_key");
      String secretKey = props.getProperty("secret_key");
      String region = props.getProperty("region");

      AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
      StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
          awsBasicCredentials);

      dynamoDbClient = DynamoDbClient.builder()
          .credentialsProvider(credentialsProvider)
          .region(Region.of(region))
          .build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize DynamoDB client: ", e);
    }
  }

  public static synchronized DynamoDbClient getClient() {
    if (dynamoDbClient == null) {
      readPropertiesAndInitializeClient();
    }
    return dynamoDbClient;
  }
}