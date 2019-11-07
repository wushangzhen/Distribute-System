package utils;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import model.ResponseMsg;

public class ReqResUtils {
  public void sendResponse(Object obj, HttpServletResponse res) throws IOException {
    String jsonString = new Gson().toJson(obj);
    PrintWriter out = res.getWriter();
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    out.print(jsonString);
    out.flush();
  }
  public boolean isUrlEmpty(String urlPath, HttpServletResponse res) throws IOException {
    if (urlPath == null || urlPath.length() == 0) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMsg responseMsg = new ResponseMsg("missing parameters");
      sendResponse(responseMsg, res);
      return true;
    }
    return false;
  }

}
