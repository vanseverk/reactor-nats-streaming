package be.vanseverk.reactor.natsstreaming;

import io.nats.streaming.Message;

public class NatsMessage {

  private Message message;

  public NatsMessage(Message message) {
    this.message = message;
  }

  public byte[] getData() {
    return message.getData();
  }

  public int getCrc32() {
    return message.getCrc32();
  }

  public String getReplyTo() {
    return message.getReplyTo();
  }

  public long getSequence() {
    return message.getSequence();
  }

  public String getSubject() {
    return message.getSubject();
  }

  public long getTimestamp() {
    return message.getTimestamp();
  }

  public boolean isRedelivered() {
    return message.isRedelivered();
  }

  public void setData(byte[] data) {
    message.setData(data);
  }

  public void setData(byte[] data, int offset, int length) {
    message.setData(data, offset, length);
  }

  public void setReplyTo(String reply) {
    message.setReplyTo(reply);
  }

  public void setSubject(String subject) {
    message.setSubject(subject);
  }

}
