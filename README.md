#Reactor NATS Streaming

An integration between [NATS Streaming](https://nats.io/documentation/streaming/nats-streaming-intro/) and [Project Reactor](https://projectreactor.io/)

Usage:
* See samples
* Requires NATS Streaming server instance

TODO's and challenges:
* StreamingConnection
  * Set up a default connection automatically?
  * How to handle closing the connection?
* Extra Reactive hooks
  * hookBeforeEmitBiFunction
  * stopConsumingBiFunction
* ExceptionHandlers for increased resilience
* Absolutely no possibility to make sending manual acks non-blocking?
