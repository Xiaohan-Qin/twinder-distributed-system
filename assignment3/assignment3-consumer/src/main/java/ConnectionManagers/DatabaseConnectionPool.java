package ConnectionManagers;

import org.postgresql.ds.PGConnectionPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {

  private static PGConnectionPoolDataSource dataSource;

  static {
    dataSource = new PGConnectionPoolDataSource();
    dataSource.setServerNames(
        new String[]{"twinder-app-database-1.capui128h8ul.us-west-2.rds.amazonaws.com"});
    dataSource.setPortNumbers(new int[] {5432});
    dataSource.setDatabaseName("my_twinder_app");
    dataSource.setUser("master");
    dataSource.setPassword("cs6650spring");
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}






