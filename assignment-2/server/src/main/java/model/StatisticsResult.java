package model;

public class StatisticsResult {
  private Long min;
  private Long max;
  private Long mean;
  private String operation;
  private String URL;

  public StatisticsResult(Long min, Long max, Long mean, String operation, String URL) {
    this.min = min;
    this.max = max;
    this.mean = mean;
    this.operation = operation;
    this.URL = URL;
  }
}
