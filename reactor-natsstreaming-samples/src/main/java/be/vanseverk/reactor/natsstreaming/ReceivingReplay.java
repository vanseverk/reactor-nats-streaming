package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import java.sql.SQLOutput;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReceivingReplay {

  private static final String RECEIVING_REPLAY_SUBJECT = "RECEIVING_REPLAY_SUBJECT";

  public static void main(String[] args) throws Exception {
    StreamingConnection sc = new StreamingConnectionFactory("test-cluster", "client-" + new Random().nextInt()).createConnection();

    Sender sender = ReactiveNatsStreaming.createSender(sc);

    Flux.interval(Duration.ofSeconds(1))
        .flatMap(i -> sender.publish(RECEIVING_REPLAY_SUBJECT, ("Replayed message " + i).getBytes())).take(10).subscribe();

    ReactiveNatsStreaming.createReceiver(sc).receive(RECEIVING_REPLAY_SUBJECT)
        .flatMap(msg -> handleMessage(1, msg))
        .subscribe();

    // Let's check if we can replay the value of 5 seconds ago
    new CountDownLatch(1).await(5, TimeUnit.SECONDS);

    ReactiveNatsStreaming.createReceiver(sc)
        .receive(RECEIVING_REPLAY_SUBJECT, new Replay())
        .flatMap(msg -> handleMessage(2, msg))
        .subscribe();

    sc.close();
  }

  private static Mono<NatsMessage> handleMessage(int receiverNb, NatsMessage msg) {
    System.out.printf("Receiver %s Received a message: %s\n", receiverNb, new String(msg.getData()));
    return Mono.just(msg);
  }
}