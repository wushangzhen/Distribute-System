package Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Resort;
import model.ResortsList;
import model.ResponseMsg;
import utils.ReqResUtils;

@WebServlet(name = "Servlets.ResortsServlet")
public class ResortsServlet extends HttpServlet {
  ReqResUtils reqResUtils = new ReqResUtils();

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {
    String urlPath = req.getPathInfo();

    // check we have a URL!
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("Invalid URL");
      reqResUtils.sendResponse(responseMsg, res);
    } else {
      BufferedReader bf = req.getReader();
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = bf.readLine()) != null) {
        sb.append(line);
      }
      res.setStatus(HttpServletResponse.SC_OK);
      reqResUtils.sendResponse(sb.toString(), res);
    }

  }

  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {
    String urlPath = req.getPathInfo();

    // check we have a URL!
    System.out.println(urlPath);

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("Invalid URL");
      reqResUtils.sendResponse(responseMsg, res);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      List<Resort> resorts = new ArrayList<>();
      Resort resort1 = new Resort("Hao Zhao", 0);
      resorts.add(resort1);
      ResortsList resortsList = new ResortsList(resorts);
      List<ResortsList> listRes = new ArrayList<>();
      listRes.add(resortsList);

      reqResUtils.sendResponse(listRes, res);
    }
  }
  private boolean isUrlValid(String[] urlParts) {
    if (urlParts.length == 0) {
      return true;
    } else if (urlParts.length == 3) {
      if (urlParts[0].length() == 0 && urlParts[2].equals("seasons")) {
        return true;
      }
    }
    return false;
  }
}
