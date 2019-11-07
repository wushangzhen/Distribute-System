package model;

public class LiftRide {

  public short getTime() {
    return time;
  }

  public void setTime(short time) {
    this.time = time;
  }

  public short getLiftID() {
    return liftID;
  }

  public void setLiftID(short liftID) {
    this.liftID = liftID;
  }

  public short time;
  public short liftID;

  public LiftRide(short time, short liftID) {
    this.time = time;
    this.liftID = liftID;
  }

}
