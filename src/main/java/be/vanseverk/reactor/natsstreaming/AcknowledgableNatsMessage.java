package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.Message;
import java.io.IOException;
import reactor.core.publisher.Mono;

public class AcknowledgableNatsMessage extends NatsMessage {

  private Message message;

  public AcknowledgableNatsMessage(Message message) {
    super(message);
    this.message = message;
  }

  public Mono<Void> ack() {
    try {
      message.ack();
      return Mono.empty();
    } catch (IOException e) {
      return Mono.error(e);
    }
  }

}
