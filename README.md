### 8) Kafka with below features:
#### Publish-Subscribe Messaging:
######      Utilize Kafka's publish-subscribe model to enable multiple consumers to subscribe to the same topic and receive copies of messages.
#### Message Serialization:
######      Configure message serialization and deserialization to convert complex data structures to and from the byte streams that Kafka uses for communication.
#### Message Partitioning:(produces message in different partitions, by default round robin and also sent to specific partitions)
######      Use partitioning to distribute messages across multiple brokers for scalability and parallel processing. Implement custom partitioners if needed.
#### Consumer Groups:( created multiple instances of same consumer)
######      Utilize consumer groups to scale horizontally and distribute message processing workload across multiple instances of a consumer within the same consumer group.
#### Offset Management: ( explained in theory)
######      Handle offset management to keep track of the position of the last consumed message in each partition. This ensures that consumers can resume from where they left off.
#### Dead Letter Queues: ( retry and recovered failed records)
######      Implement dead letter queues or error-handling mechanisms for messages that couldn't be successfully processed by consumers.
#### Batch Processing:( fetching using poll(Timeunit.seconds))
######      Use Kafka for batch processing scenarios by consuming messages from topics that store batches of data.
