package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReceivingQueue {

  private static final String RECEIVING_QUEUE_SUBJECT = "RECEIVING_QUEUE_SUBJECT";
  private static final String RECEIVING_QUEUE_QUEUE_GROUP_NAME = "RECEIVING_QUEUE_QUEUE_GROUP_NAME";

  public static void main(String[] args) throws Exception {
    StreamingConnection sc = new StreamingConnectionFactory("test-cluster", "client-" + new Random().nextInt()).createConnection();

    Receiver receiver = ReactiveNatsStreaming.createReceiver(sc);
    Sender sender = ReactiveNatsStreaming.createSender(sc);

    receiver.receive(RECEIVING_QUEUE_SUBJECT, RECEIVING_QUEUE_QUEUE_GROUP_NAME)
        .flatMap(msg -> handleMessage(1, msg))
        .subscribe();

    receiver.receive(RECEIVING_QUEUE_SUBJECT, RECEIVING_QUEUE_QUEUE_GROUP_NAME)
        .flatMap(msg -> handleMessage(2, msg))
        .subscribe();

    Flux.interval(Duration.ofSeconds(1)).take(5)
        .flatMap(i -> sender.publish(RECEIVING_QUEUE_SUBJECT, ("Queue Group message  " + i).getBytes())).subscribe();

    new CountDownLatch(1).await(5, TimeUnit.SECONDS);
    sc.close();
  }

  private static Mono<NatsMessage> handleMessage(int receiverNb, NatsMessage msg) {
    System.out.printf("Receiver %s Received a message: %s\n", receiverNb, new String(msg.getData()));
    return Mono.just(msg);
  }
}