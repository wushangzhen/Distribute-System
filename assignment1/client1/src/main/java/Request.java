import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Request implements Runnable {
  private final int SC_CREATED = 201;
  private String port;
  private Integer resortID;
  private String seasonID;
  private String dayID;
  private Integer meanNumberOfSkiLift;
  private int skiersPerThread;
  private int startTime;
  private int endTime;
  private Random rand;
  private int numOfSkierLift;
  private int kthThread;
  private CountDownLatch countDownLatch;
  private CountDownLatch countDownLatchTotal;
  private double rate;
  private AtomicInteger numOfSuccessfulReq;
  private AtomicInteger numOfReq;
  public Request(int kthThread, String port, Integer resortID, String seasonID, String dayID,
      int skiersPerThread, int meanNumberOfSkiLift, int startTime, int endTime, int numOfSkierLift,
      CountDownLatch countDownLatch, CountDownLatch countDownLatchTotal, double rate,
      AtomicInteger numOfSuccessfulReq, AtomicInteger numOfReq) {
    this.port = port;
    this.resortID = resortID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.meanNumberOfSkiLift = meanNumberOfSkiLift;
    this.skiersPerThread = skiersPerThread;
    this.startTime = startTime;
    this.endTime = endTime;
    this.rand = new Random();
    this.numOfSkierLift = numOfSkierLift;
    this.kthThread = kthThread;
    this.countDownLatch = countDownLatch;
    this.rate = rate;
    this.countDownLatchTotal = countDownLatchTotal;
    this.numOfSuccessfulReq = numOfSuccessfulReq;
    this.numOfReq = numOfReq;
  }

  @Override
  public void run() {
    String basePath = port;
    SkiersApi apiInstance = new SkiersApi();
    ApiClient client = apiInstance.getApiClient();
    client.setBasePath(basePath);
    int numOfPosts = (int)(meanNumberOfSkiLift * rate * skiersPerThread);
    for (int i = 0; i < numOfPosts; i++) {
      try {
        LiftRide liftRide = new LiftRide();
        int time = startTime + rand.nextInt(endTime - startTime);
        liftRide.time(time);
        int liftID = rand.nextInt(numOfSkierLift) + 1;
        liftRide.liftID(liftID);
        int skierID = kthThread * skiersPerThread + rand.nextInt(skiersPerThread);
        ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(liftRide, resortID, seasonID, dayID, skierID);
        if (response.getStatusCode() == SC_CREATED) {
          numOfSuccessfulReq.getAndIncrement();
        }
        numOfReq.getAndIncrement();
        if (time < 91) {
          System.out.println("phase 1");
        } else if (time < 361) {
          System.out.println("phase 2");
        } else if (time < 420) {
          System.out.println("phase 3");
        }
      } catch (Exception e) {
        System.err.println("Exception when calling SkiersApi#writeNewLiftRide");
        e.printStackTrace();
      }
    }
    countDownLatch.countDown();
    countDownLatchTotal.countDown();
  }
}
