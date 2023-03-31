package ConnectionManagers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnectionPool {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionPool.class);
  private static  PGConnectionPoolDataSource dataSource;

  private static void readProperties() {
    Properties props = new Properties();
    try (InputStream is = DatabaseConnectionPool.class.getClassLoader().getResourceAsStream("postgresql.properties")) {
      props.load(is);
      if (is == null) {
        LOGGER.error("postgresql.properties file not found.");
        return;
      }
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
    return dataSource.getConnection();
  }
}





