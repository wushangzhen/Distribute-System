package Servlets;

import model.StatisticsResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ResponseMsg;
import utils.ReqResUtils;

@WebServlet(name = "StatisticsServlet")
public class StatisticsServlet extends HttpServlet {
  private ReqResUtils reqResUtils = new ReqResUtils();
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

  }
  //  curl -X GET http://localhost:8080/cs6650lab/statistics/day/1/skier/5119/type/post
  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (reqResUtils.isUrlEmpty(urlPath, res)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("Invalid URL");
      reqResUtils.sendResponse(responseMsg, res);
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
      ConcurrentHashMap<String, ConcurrentHashMap<String, Queue<Long>>> apiToLatency =
          SkierServlet.apiToLatency;
      String dayID = urlParts[2];
      String skierID = urlParts[4];
      String type = urlParts[6];
      String key = dayID + " " + skierID;
      ConcurrentHashMap<String, Queue<Long>> latencies = apiToLatency.get(key);
      if (latencies != null) {
        Queue<Long> q = latencies.get(type);
        if (q != null) {
          List<Long> latency = new ArrayList<>(q);
          Collections.sort(latency);
          Long min = latency.get(0);
          Long max = latency.get(latency.size() - 1);
          long sum = 0;
          for (long l : latency) {
            sum += l;
          }
          long avg = sum / latency.size();
          reqResUtils.sendResponse(new StatisticsResult(min / 1_000_000, max / 1_000_000, avg / 1_000_000, type.toUpperCase(), "/skiers/*"), res);
          res.setStatus(HttpServletResponse.SC_OK);
          return;
        }
      }
      reqResUtils.sendResponse(null, res);
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
    private boolean isUrlValid(String[] urlPath) {
      // TODO: validate the request url path according to the API spec
      if (urlPath.length == 7 && urlPath[0].length() == 0 && urlPath[1].equals("day") && urlPath[3].equals("skier") && urlPath[5].equals("type")) {
          return true;
        }
      return false;
    }
}
