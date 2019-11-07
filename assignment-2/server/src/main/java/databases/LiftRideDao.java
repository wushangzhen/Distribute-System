package databases;

import constant.SQLConstant;
import java.sql.*;
import model.LiftRide;

public class LiftRideDao {
//  private Connection conn;
//  private PreparedStatement preparedStatement;

  public LiftRideDao() {
    try {
//      conn = DBConnectServer.getInstance().getConnection();
//      conn = null;
//      preparedStatement = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createLiftRide(String sql) throws SQLException {
//    String insertQueryStatement = "INSERT INTO skiers_lift_record(skierId, resortId, seasonId, dayId, time, liftId) " +
//        "VALUES (?,?,?,?,?,?)";
    try {
      Connection conn = null;
      PreparedStatement preparedStatement = null;
      conn = DBConnectServer.getInstance().getConnection();
      preparedStatement = conn.prepareStatement(sql);
//      preparedStatement.setInt(1, newLiftRide.getSkierId());
//      preparedStatement.setInt(2, newLiftRide.getResortId());
//      preparedStatement.setInt(3, newLiftRide.getSeasonId());
//      preparedStatement.setInt(4, newLiftRide.getDayId());
//      preparedStatement.setInt(5, newLiftRide.getTime());
//      preparedStatement.setInt(6, newLiftRide.getLiftId());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
      preparedStatement.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public Integer getLiftRide(String sql, int resortID, String seasonID, String dayID, int SkierID) throws SQLException {
    Integer vertical = null;
    try {
      Connection conn = null;
      PreparedStatement preparedStatement = null;
      conn = DBConnectServer.getInstance().getConnection();
      preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setInt(1, resortID);
      preparedStatement.setString(2, seasonID);
      preparedStatement.setString(3, dayID);
      preparedStatement.setInt(4, SkierID);
//      preparedStatement.setInt(4, newLiftRide.getDayId());
//      preparedStatement.setInt(5, newLiftRide.getTime());
//      preparedStatement.setInt(6, newLiftRide.getLiftId());

      // execute insert SQL statement
      ResultSet rs = preparedStatement.executeQuery();
//      Integer vertical = null;
      while (rs.next()) {
        vertical = rs.getInt("vertical");
      }
      rs.close();
      preparedStatement.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return vertical;
  }
}
