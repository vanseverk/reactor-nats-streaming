package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReceivingManualAck {

  private static final String RECEIVING_MANUAL_ACK_SUBJECT = "RECEIVING_MANUAL_ACK_SUBJECT";

  public static void main(String[] args) throws Exception {
    StreamingConnection sc = new StreamingConnectionFactory("test-cluster", "client-" + new Random().nextInt()).createConnection();

    Receiver receiver = ReactiveNatsStreaming.createReceiver(sc);
    Sender sender = ReactiveNatsStreaming.createSender(sc);

    receiver.receiveManualAck(RECEIVING_MANUAL_ACK_SUBJECT)
        .flatMap(msg -> handleMessage(msg))
        .flatMap(msg -> msg.ack())
        .subscribe();

    Flux.interval(Duration.ofSeconds(1)).take(5)
        .flatMap(i -> sender.publish(RECEIVING_MANUAL_ACK_SUBJECT, ("Manual Acknowledged message " + i).getBytes())).subscribe();

    new CountDownLatch(1).await(5, TimeUnit.SECONDS);

    sc.close();
  }

  private static Mono<AcknowledgableNatsMessage> handleMessage(AcknowledgableNatsMessage msg) {
    System.out.printf("Received a message: %s\n", new String(msg.getData()));
    return Mono.just(msg);
  }
}