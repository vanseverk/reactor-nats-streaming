package be.vanseverk.reactor.natsstreaming;

import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;

public class ConsumeOptions {

  private Integer maxInFlight;

  private FluxSink.OverflowStrategy overflowStrategy = FluxSink.OverflowStrategy.BUFFER;

  public ConsumeOptions maxInFlight(int maxInFlight) {
    this.maxInFlight = maxInFlight;
    return this;
  }

  public ConsumeOptions overflowStrategy(FluxSink.OverflowStrategy overflowStrategy) {
    this.overflowStrategy = overflowStrategy;
    return this;
  }

  public Integer getMaxInFlight() {
    return maxInFlight;
  }

  public OverflowStrategy getOverflowStrategy() {
    return overflowStrategy;
  }
}
