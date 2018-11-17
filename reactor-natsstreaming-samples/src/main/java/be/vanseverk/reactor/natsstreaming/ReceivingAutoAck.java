package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import java.time.Duration;
import java.util.Random;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReceivingAutoAck {

  private static final String RECEIVING_AUTO_ACK_SUBJECT = "RECEIVING_AUTO_ACK_SUBJECT";

  public static void main(String[] args) throws Exception {
    StreamingConnection sc = new StreamingConnectionFactory("test-cluster", "client-" + new Random().nextInt()).createConnection();

    Receiver receiver = ReactiveNatsStreaming.createReceiver(sc);
    Sender sender = ReactiveNatsStreaming.createSender(sc);

    receiver.receive(RECEIVING_AUTO_ACK_SUBJECT)
        .map(msg -> handleMessage(msg)).subscribe();

    Flux.interval(Duration.ofSeconds(1)).take(5)
        .flatMap(i -> sender.publish(RECEIVING_AUTO_ACK_SUBJECT, ("Auto Ackno1wledged message " + i).getBytes())).subscribe();

    sc.close();
  }

  private static Mono<NatsMessage> handleMessage(NatsMessage msg) {
    System.out.printf("Received a message: %s\n", new String(msg.getData()));
    return Mono.just(msg);
  }
}