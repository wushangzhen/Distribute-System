package databases;

import constant.SQLConstant;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DbWrite implements Runnable {
  ConcurrentLinkedQueue<String> concurrentLinkedQueue;
  Statement stmt;
  int batchSize = 5000;
  AtomicInteger count;
  public DbWrite(ConcurrentLinkedQueue<String> concurrentLinkedQueue, Statement stmt) {
    this.concurrentLinkedQueue = concurrentLinkedQueue;
    this.stmt = stmt;
    count = new AtomicInteger(0);
  }
  @Override
  public void run() {
    try {
        while (true) {
          StringBuilder sb = new StringBuilder(SQLConstant.INSERT_BATCH_SQL);
          for (int i = 0; i < batchSize && !concurrentLinkedQueue.isEmpty(); i++) {
            sb.append(concurrentLinkedQueue.poll()).append(",");
          }
          if (sb.length() == SQLConstant.INSERT_BATCH_SQL.length()) continue;
          sb.setLength(sb.length() - 1);
          sb.append(";");
          int rows = stmt.executeUpdate(sb.toString());
          System.out.println(rows);
          Thread.sleep(1000);
        }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
