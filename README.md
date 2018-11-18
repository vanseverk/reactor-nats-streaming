#Reactor NATS Streaming

An integration between [NATS Streaming](https://nats.io/documentation/streaming/nats-streaming-intro/) and [Project Reactor](https://projectreactor.io/)

Usage:
* See samples
* Requires NATS Streaming server instance

TODO's and challenges:
* Option for Durable subscription
* More manualAck receive options
* Expand on Sender functionality
* StreamingConnection
  * Set up a default connection automatically?
  * How to handle closing the connection?
* Extra Reactive hooks
  * hookBeforeEmitBiFunction
  * stopConsumingBiFunction
* ExceptionHandlers for increased resilience
* Sending manual acks should be non-blocking
  * Blocker: Not possible in current java-nats-streaming 2.1.0
    * Solution (Prefered): PR to https://github.com/nats-io/java-nats-streaming
    * Solution: Extending Message to allow non-blocking manual ack
