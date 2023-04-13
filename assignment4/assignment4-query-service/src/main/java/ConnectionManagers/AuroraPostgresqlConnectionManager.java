package ConnectionManagers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuroraPostgresqlConnectionManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuroraPostgresqlConnectionManager.class);
  private static PGConnectionPoolDataSource dataSource;
  private static final Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<>();

  private static void readProperties() {
    Properties props = new Properties();
    try (InputStream is = AuroraPostgresqlConnectionManager.class.getClassLoader().getResourceAsStream("aurora_postgresql.properties")) {
      if (is == null) {
        LOGGER.error("aurora_postgresql.properties file not found.");
        return;
      }
      props.load(is);
      dataSource.setServerNames(new String[]{props.getProperty("hostname")});
      dataSource.setPortNumbers(new int[]{Integer.parseInt(props.getProperty("port"))});
      dataSource.setDatabaseName(props.getProperty("dbname"));
      dataSource.setUser(props.getProperty("username"));
      dataSource.setPassword(props.getProperty("password"));
    } catch (IOException e) {
      LOGGER.error("Failed to connect to database: ", e);
    }
  }

  public static Connection getConnection() throws SQLException {
    if (dataSource == null) {
      dataSource = new PGConnectionPoolDataSource();
      readProperties();
    }
    if (connectionQueue.isEmpty()) {
      for (int i = 0; i < 3; i++) {
        connectionQueue.add(dataSource.getConnection());
      }
    }
    Connection connection = connectionQueue.poll();
    connectionQueue.add(connection);
    return connection;
  }
}
