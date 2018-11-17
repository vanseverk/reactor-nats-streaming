package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.Message;
import io.nats.streaming.MessageHandler;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.Subscription;
import io.nats.streaming.SubscriptionOptions;
import io.nats.streaming.SubscriptionOptions.Builder;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class Receiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

  private final StreamingConnection sc;

  Receiver(StreamingConnection sc) {
    this.sc = sc;
  }

  public Flux<NatsMessage> receive(String subject) {
    return receive(subject, null, false, null, new ConsumeOptions()).map(message -> new NatsMessage(message));
  }

  public Flux<NatsMessage> receive(String subject, String queueGroupName) {
    return receive(subject, queueGroupName, false, null, new ConsumeOptions()).map(message -> new NatsMessage(message));
  }

  public Flux<NatsMessage> receive(String subject, Replay replay) {
    return receive(subject, null, false, replay, new ConsumeOptions()).map(message -> new NatsMessage(message));
  }

  public Flux<NatsMessage> receive(String subject, String queueGroupName, Replay replay) {
    return receive(subject, queueGroupName, false, replay, new ConsumeOptions()).map(message -> new NatsMessage(message));
  }

  public Flux<NatsMessage> receive(String subject, ConsumeOptions consumeOptions) {
    return receive(subject, null, false, null, consumeOptions).map(message -> new NatsMessage(message));
  }

  public Flux<AcknowledgableNatsMessage> receiveManualAck(String subject) {
    return receive(subject, null, true, null, new ConsumeOptions()).map(message -> new AcknowledgableNatsMessage(message));
  }

  public Flux<Message> receive(String subject, String queueGroupName, boolean manualAck, Replay replay, ConsumeOptions consumeOptions) {
    return Flux.create(emitter -> {
      try {
        MessageHandler msgHandler = (message -> emitter.next(message));

        final Subscription subscription = createSubscription(msgHandler, subject, queueGroupName, manualAck, replay, consumeOptions);

        AtomicBoolean cancelled = new AtomicBoolean(false);

        emitter.onDispose(() -> {
          if (cancelled.compareAndSet(false, true)) {
            try {
              if(subscription != null) {
                subscription.close();
              }
            } catch (IOException e) {
              LOGGER.warn("Error while closing channel: " + e.getMessage());
            }
          }
        });
      } catch (IOException | InterruptedException | TimeoutException e) {
        emitter.error(e);
      }
    }, consumeOptions.getOverflowStrategy());
  }

  private Subscription createSubscription(MessageHandler msgHandler, String subject, String queueGroupName, boolean manualAck, Replay replay, ConsumeOptions consumeOptions)
      throws InterruptedException, TimeoutException, IOException {
    if(queueGroupName != null) {
      return sc.subscribe(subject, queueGroupName, msgHandler, buildSubscriptionOptions(manualAck, replay, consumeOptions));
    } else {
      return sc.subscribe(subject, msgHandler, buildSubscriptionOptions(manualAck, replay, consumeOptions));
    }
  }

  private SubscriptionOptions buildSubscriptionOptions(boolean manualAck, Replay replay, ConsumeOptions consumeOptions) {
    Builder subscriptionOptionsBuilder = new Builder();

    if (replay != null) {
      if (replay.getStartAtSequenceNumber() != null) {
        subscriptionOptionsBuilder = subscriptionOptionsBuilder.startAtSequence(replay.getStartAtSequenceNumber());
      } else if (replay.getStartAtTime() != null) {
        subscriptionOptionsBuilder = subscriptionOptionsBuilder.startAtTime(replay.getStartAtTime());
      } else if (replay.getStartAtTimeDelta() != null) {
        subscriptionOptionsBuilder = subscriptionOptionsBuilder.startAtTimeDelta(replay.getStartAtTimeDelta());
      } else {
        subscriptionOptionsBuilder = subscriptionOptionsBuilder.deliverAllAvailable();
      }
    } else {
      subscriptionOptionsBuilder = subscriptionOptionsBuilder.startWithLastReceived();
    }

    if (manualAck) {
      subscriptionOptionsBuilder = subscriptionOptionsBuilder.manualAcks();
    }

    if (consumeOptions != null) {
      if (consumeOptions.getMaxInFlight() != null) {
        subscriptionOptionsBuilder = subscriptionOptionsBuilder.maxInFlight(consumeOptions.getMaxInFlight());
      }
    }

    return subscriptionOptionsBuilder.build();
  }

}
