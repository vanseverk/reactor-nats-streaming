package be.vanseverk.reactor.natsstreaming;

import java.time.Duration;
import java.time.Instant;

public class Replay {

  private Long startAtSequenceNumber;
  private Instant startAtTime;
  private Duration startAtTimeDelta;

  // Replay all available
  public Replay() {
  }

  public Replay(long startAtSequenceNumber) {
    this.startAtSequenceNumber = startAtSequenceNumber;
  }

  public Replay(Instant startAtTime) {
    this.startAtTime = startAtTime;
  }

  public Replay(Duration startAtTimeDelta) {
    this.startAtTimeDelta = startAtTimeDelta;
  }

  Long getStartAtSequenceNumber() {
    return startAtSequenceNumber;
  }


  public Instant getStartAtTime() {
    return startAtTime;
  }

  public void setStartAtTime(Instant startAtTime) {
    this.startAtTime = startAtTime;
  }

  public Duration getStartAtTimeDelta() {
    return startAtTimeDelta;
  }

  public void setStartAtTimeDelta(Duration startAtTimeDelta) {
    this.startAtTimeDelta = startAtTimeDelta;
  }
}
