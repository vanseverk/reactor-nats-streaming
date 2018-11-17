package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.AckHandler;
import io.nats.streaming.StreamingConnection;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class Sender {

  private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

  private final StreamingConnection sc;

  public Sender(StreamingConnection sc) {
    this.sc = sc;
  }

  public Mono<Void> publish(String subject, byte[] bytes) {
    return Mono.create(callback -> {

      AckHandler ackHandler = (s, e) -> {
        if (e == null) {
          callback.success();
        } else {
          callback.error(e);
        }
      };

      try {
        sc.publish(subject, bytes, ackHandler);
      } catch (IOException | InterruptedException | TimeoutException e) {
        callback.error(e);
      }
    });
  }

}
