package Servlets;

import static javax.print.attribute.standard.ReferenceUriSchemesSupported.HTTP;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import constant.SQLConstant;
import databases.DBConnectServer;
import databases.DbWrite;
import databases.LiftRideDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ResponseMsg;
import model.SkierVertical;
import model.SkierVerticalResorts;
import utils.ReqResUtils;
import java.sql.DriverManager;

@WebServlet(name = "Servlets.SkierServlet")
public class SkierServlet extends HttpServlet {
  private ReqResUtils reqResUtils = new ReqResUtils();
  private final String DB_URL = "jdbc:mysql://database-2.cdgot1ydqh5o.us-west-2.rds.amazonaws.com:3306";
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private String POST = "post";
  private String GET = "get";
  private int N = 100;
//  private ConcurrentLinkedQueue<String> concurrentLinkedQueue;
//  private ConcurrentHashMap<String, Integer> verticalMap;
  public static ConcurrentHashMap<String, ConcurrentHashMap<String, Queue<Long>>> apiToLatency = new ConcurrentHashMap<>();

  //  Database credentials
  private static final String USER = "root";
//  private static final String USER = "database-2.cdgot1ydqh5o.us-west-2.rds.amazonaws.com";
  private static final String PASS = "0120352wsz";
  Connection conn = null;
  Statement stmt = null;

  public SkierServlet() {
    try {
      String sql = "SHOW DATABASES";
      conn = DBConnectServer.getInstance().getConnection();

      stmt = conn.createStatement();
      System.out.println("Dropping database...");
      String dropDatabase = "DROP DATABASE IF EXISTS Skiers";
      stmt.executeUpdate(dropDatabase);
      String createDatabase = "CREATE DATABASE Skiers";
      stmt.executeUpdate(createDatabase);
      String useDatabase = "USE Skiers";
      stmt.executeUpdate(useDatabase);
      String createSkierLiftTable = "CREATE TABLE skiers_lift_record(" +
          "id INTEGER NOT NULL AUTO_INCREMENT, " +
          "resortID INTEGER, " +
          "seasonID VARCHAR(255), " +
          "dayID VARCHAR(255), " +
          "skierID INTEGER, " +
          "time SMALLINT, " +
          "liftID SMALLINT, " +
          "PRIMARY KEY (id))";
      stmt.executeUpdate(createSkierLiftTable);
      String createSkierVerticalTable = "CREATE TABLE skiers_vertical_record(" +
          "resortID INTEGER, " +
          "seasonID VARCHAR(255), " +
          "dayID VARCHAR(255), " +
          "skierID INTEGER, " +
          "vertical BIGINT, " +
          "PRIMARY KEY (resortID, seasonID, dayID, skierID))";
      stmt.executeUpdate(createSkierVerticalTable);
//      new Thread(new DbWrite(concurrentLinkedQueue, stmt)).start();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {
    try {
      String urlPath = req.getPathInfo();
      // check we have a URL!
      if (reqResUtils.isUrlEmpty(urlPath, res)) {
        return;
      }
      String[] urlParts = urlPath.split("/");
      if (!isUrlValidForPost(urlParts)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        ResponseMsg responseMsg = new ResponseMsg("Invalid URL");
        reqResUtils.sendResponse(responseMsg, res);
      } else {
        BufferedReader bf = req.getReader();
        StringBuffer sb = new StringBuffer();
        String line;
        int resortID = Integer.parseInt(urlParts[1]);
        String seasonID = urlParts[3];
        String dayID = urlParts[5];
        int skierID = Integer.parseInt(urlParts[7]);
        while ((line = bf.readLine()) != null) {
          sb.append(line);
        }
        JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
        short time = jsonObject.get("time").getAsShort();
        short liftID = jsonObject.get("liftID").getAsShort();
        String insertLiftData = SQLConstant.INSERT_BATCH_SQL +
            "("
                + resortID +", '"
                + seasonID + "', '"
                + dayID + "', "
                + skierID + ", "
                + time + ", "
                + liftID + ")";
//        concurrentLinkedQueue.add(insertLiftData);
        long startTimeStamp = System.nanoTime();
        LiftRideDao liftRideDao = new LiftRideDao();
        liftRideDao.createLiftRide(insertLiftData);

        int vertical = liftID * 10;
        String updateVertical = "INSERT INTO skiers_vertical_record(resortId, seasonId, dayID, skierID, vertical)"
            + " VALUES(" + resortID + "," + seasonID + "," + dayID + "," + skierID + "," + vertical + ")"
            + " ON DUPLICATE KEY UPDATE vertical=" + "VALUES(vertical)+" + vertical;

        liftRideDao = new LiftRideDao();
        liftRideDao.createLiftRide(updateVertical);
        long endTimeStamp = System.nanoTime();
        String key = dayID + " " + skierID;
        long latency = endTimeStamp - startTimeStamp;
        apiToLatency.putIfAbsent(key, new ConcurrentHashMap<String, Queue<Long>>());
        ConcurrentHashMap<String, Queue<Long>> latencies = apiToLatency.get(key);
        latencies.putIfAbsent(POST, new LinkedList<Long>());
        latencies.get(POST).add(latency);
        if (latencies.get(POST).size() > N) {
          latencies.get(POST).poll();
        }
        res.setStatus(HttpServletResponse.SC_CREATED);
      }
    } catch (Exception e) {
      e.printStackTrace();
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {
    try {
      String urlPath = req.getPathInfo();

      // check we have a URL!
      if (reqResUtils.isUrlEmpty(urlPath, res)) {
        return;
      }

      String[] urlParts = urlPath.split("/");
      // and now validate url path and return the response status code
      // (and maybe also some value if input is valid)

      if (!isUrlValid(urlParts)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        ResponseMsg responseMsg = new ResponseMsg("Invalid URL");
        reqResUtils.sendResponse(responseMsg, res);
      } else {
        // do any sophisticated processing with urlParts which contains all the url params
        // TODO: process url params in `urlParts`
        if (urlParts.length == 3) {
          SkierVerticalResorts skierVerticalResorts = new SkierVerticalResorts("1", 1);
          List<SkierVerticalResorts> skierVerticalResortsList = new ArrayList<>();
          skierVerticalResortsList.add(skierVerticalResorts);
          SkierVertical skierVertical = new SkierVertical(skierVerticalResortsList);
          reqResUtils.sendResponse(skierVertical, res);
        } else {
          int resortID = Integer.parseInt(urlParts[1]);
          String seasonID = urlParts[3];
          String dayID = urlParts[5];
          int skierID = Integer.parseInt(urlParts[7]);
//          Integer vertical = verticalMap.get(key);
          String getVertical = "SELECT vertical FROM skiers_vertical_record WHERE resortID=? && seasonID=? && dayID=? && skierID=?";
          long startTimeStamp = System.nanoTime();
          LiftRideDao liftRideDao = new LiftRideDao();
          Integer vertical = liftRideDao.getLiftRide(getVertical, resortID, seasonID, dayID, skierID);
          long endTimeStamp = System.nanoTime();
          String key = dayID + " " + skierID;
          long latency = endTimeStamp - startTimeStamp;
          apiToLatency.putIfAbsent(key, new ConcurrentHashMap<String, Queue<Long>>());
          ConcurrentHashMap<String, Queue<Long>> latencies = apiToLatency.get(key);
          latencies.putIfAbsent(GET, new LinkedList<Long>());
          latencies.get(GET).add(latency);
          if (latencies.get(GET).size() > N) {
            latencies.get(GET).poll();
          }
          res.setStatus(HttpServletResponse.SC_OK);
          reqResUtils.sendResponse(vertical, res);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private boolean isUrlValidForPost(String[] urlPath) {
    try {
      if (urlPath.length == 8) {
        if (urlPath[0].length() == 0 && urlPath[2].equals("seasons") && urlPath[4].equals("days")
            && urlPath[6].equals("skiers")) {
          int resortID = Integer.parseInt(urlPath[1]);
          int dayID = Integer.parseInt(urlPath[5]);
          if (dayID < 1 || dayID > 365) {
            return false;
          }
          int skierID = Integer.parseInt(urlPath[7]);
          return true;
        }
      }
      return false;
    } catch (NumberFormatException | NullPointerException nfe) {
      return false;
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    if (urlPath.length == 8) {
      if (urlPath[0].length() == 0 && urlPath[2].equals("seasons") && urlPath[4].equals("days")
        && urlPath[6].equals("skiers")) {
          return true;
      }
    } else if (urlPath.length == 3){
      if (urlPath[0].length() == 0 && urlPath[2].equals("vertical")) {
        return true;
      }
    }
    return false;
  }
}
