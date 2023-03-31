import java.sql.*;
import java.sql.PreparedStatement;
import ConnectionManagers.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to store messages to database
 */
public class ConsumerUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUtils.class);

  public ConsumerUtils() {
  }

  public void processSwipeMessage(SwipeMessage message) {
    Connection dbConnection = null;
    try {
      // connect to db
      dbConnection = DatabaseConnectionPool.getConnection();
      dbConnection.setAutoCommit(false); // start transaction
      PreparedStatement stmt = dbConnection.prepareStatement(
          "SELECT * FROM users.user_data WHERE userid = ?");
      stmt.setInt(1, message.getSwiperId());
      ResultSet rs = stmt.executeQuery();
      boolean userInDatabase = rs.next();
      if (!userInDatabase) {
        stmt = dbConnection.prepareStatement(
            "INSERT INTO users.user_data (userid, num_likes, num_dislikes, matched_users) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, message.getSwiperId());
        stmt.setInt(2, message.getLeftOrRight().equals("right") ? 1 : 0);
        stmt.setInt(3, message.getLeftOrRight().equals("left") ? 1 : 0);
        stmt.setArray(4, dbConnection.createArrayOf("integer", new Integer[0]));
      } else {
        stmt = dbConnection.prepareStatement(
            "SELECT * FROM users.user_data WHERE userid = ? AND ? = ANY(matched_users)");
        stmt.setInt(1, message.getSwiperId());
        stmt.setInt(2, message.getSwipeeId());
        rs = stmt.executeQuery();
        boolean alreadySwiped = rs.next();
        if (message.getLeftOrRight().equals("right")) {
          if (alreadySwiped) {
            // If the swiper has already swiped on the swipee, don't update the database
            dbConnection.commit();
            return;
          }
          stmt = dbConnection.prepareStatement(
              "UPDATE users.user_data SET num_likes = num_likes + 1, matched_users = array_append(matched_users, ?) WHERE userid = ?");
          stmt.setInt(1, message.getSwipeeId());
          stmt.setInt(2, message.getSwiperId());
        } else {
          // If the swiper swiped left, increment num_dislikes
          stmt = dbConnection.prepareStatement(
              "UPDATE users.user_data SET num_dislikes = num_dislikes + 1 WHERE userid = ?");
          stmt.setInt(1, message.getSwiperId());
        }
      }
      stmt.executeUpdate();
      dbConnection.commit();
    } catch (SQLException e) {
      LOGGER.error("Failed to update database: ", e);
      try {
        assert dbConnection != null;
        dbConnection.rollback(); // undo changes
      } catch (SQLException ex) {
        LOGGER.error("Failed to rollback transaction: ", ex);
      }
    }
  }
}
