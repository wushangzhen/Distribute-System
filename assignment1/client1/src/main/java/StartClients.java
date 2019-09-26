import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResortsList;
import java.security.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class StartClients {
  public static int maxThreads = 64;
  public static int numOfSkiers = 1024;
  public static int numOfSkierLift = 40;
  public static int meanNumberOfSkiLift = 10;
  public static String port = "http://localhost:8080/cs6650lab";
  public static void main(String[] args) {
//    if (args.length == 5) {
//      maxThreads = Integer.parseInt(args[0]);
//      numOfSkiers = Integer.parseInt(args[1]);
//      numOfSkierLift = Integer.parseInt(args[2]);
//      meanNumberOfSkiLift = Integer.parseInt(args[3]);
//      port = args[4];
//    }

    try {
      long startTime = System.nanoTime();
      AtomicInteger numOfSuccessfulReq = new AtomicInteger(0);
      AtomicInteger numOfReq = new AtomicInteger(0);
      // Phase 1
      int numOfThread_P1 = maxThreads / 4;
      int skiersPerThread_P1 = numOfSkiers / numOfThread_P1;
      int count_P1 = (int) (numOfThread_P1 * 0.1);
      CountDownLatch latch_P1 = new CountDownLatch(count_P1);
      CountDownLatch latchTotal_P1 = new CountDownLatch(numOfThread_P1);
      int startTime_P1 = 1;
      int endTime_P1 = 90;
      double rate_P1 = 0.1;
      for (int i = 0; i < numOfThread_P1; i++) {
        Request request = new Request(i, port, 1, "1", "1",
            skiersPerThread_P1, meanNumberOfSkiLift, startTime_P1, endTime_P1, numOfSkierLift,
            latch_P1, latchTotal_P1, rate_P1, numOfSuccessfulReq, numOfReq);
        Thread thread = new Thread(request);
        thread.start();
      }
      latch_P1.await();
      // Phase 2
      System.out.println("Start Phase 2");
      int numOfThread_P2 = maxThreads;
      int skiersPerThread_P2 = numOfSkiers / numOfThread_P2;
      int startTime_P2 = 91;
      int endTime_P2 = 360;
      int count_P2 = (int) (numOfThread_P2 * 0.1);
      CountDownLatch latch2 = new CountDownLatch(count_P2);
      CountDownLatch latchTotal_P2 = new CountDownLatch(numOfThread_P2);
      double rate_P2 = 0.8;
      for (int i = 0; i < numOfThread_P2; i++) {
        Request request = new Request(i, port, 1, "1", "1",
            skiersPerThread_P2, meanNumberOfSkiLift, startTime_P2, endTime_P2, numOfSkierLift,
            latch2, latchTotal_P2, rate_P2, numOfSuccessfulReq, numOfReq);
        Thread thread = new Thread(request);
        thread.start();
      }
      latch2.await();
      System.out.println("Start Phase 3");
      int numOfThread_P3 = maxThreads / 4;
      int skiersPerThread_P3 = numOfSkiers / numOfThread_P3;
      int startTime_P3 = 361;
      int endTime_P3 = 420;
      double rate_P3 = 0.1;
      CountDownLatch latch3 = new CountDownLatch(numOfThread_P3);
      CountDownLatch latchTotal_P3 = new CountDownLatch(numOfThread_P3);
      for (int i = 0; i < numOfThread_P3; i++) {
        Request request = new Request(i, port, 1, "1", "1",
            skiersPerThread_P3, meanNumberOfSkiLift, startTime_P3, endTime_P3, numOfSkierLift,
            latch3, latchTotal_P3, rate_P3, numOfSuccessfulReq, numOfReq);
        Thread thread = new Thread(request);
        thread.start();
      }
      latchTotal_P1.await();
      latchTotal_P2.await();
      latchTotal_P3.await();
      long endTime = System.nanoTime();
      long duration = (endTime - startTime);
      int totalNumberOfReq = numOfReq.get();
      int successfulReq = numOfSuccessfulReq.get();
      System.out.println("Number of Successful Requests: " + successfulReq);
      System.out.println("Number of Unsuccessful Requests: " + (totalNumberOfReq - successfulReq));
      System.out.println("Number of Requests: " + totalNumberOfReq);
      System.out.println("Execution Time: " + duration / 1_000_000_000 + "s");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
