package com.kafka.practice.LibraryEventManagemnetConsumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.practice.LibraryEventManagemnetConsumer.model.CustomeException;
import com.kafka.practice.LibraryEventManagemnetConsumer.model.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class LibraryEventsConsumer {

  private final List<LibraryEvent> messages = new CopyOnWriteArrayList<>(); // assync call
  private ObjectMapper mapper = new ObjectMapper();
  private LibraryEventsConsumer libraryEventsConsumer;

  @KafkaListener(
      topics = {"library-events"},
      groupId = "library-events-listener-group",
      containerFactory = "kafkaListenerContainerFactoryPullDataWhenPartitionsUpdates")
  public void listen(ConsumerRecord<Integer, String> consumerRecord, Consumer<?, ?> consumer)
      throws JsonProcessingException {

    LibraryEvent libraryEvent = mapper.readValue(consumerRecord.value(), LibraryEvent.class);
    //LibraryEvent libraryEvent = new LibraryEvent();
    log.info(
        "topic : library-events message {} consumed from partition {} and consumer is {} ",
        consumerRecord.value(),
        consumerRecord.partition(),
        consumerRecord.headers());

    messages.add(libraryEvent);

    // Log the partitions assigned to this consumer
    consumer
        .assignment()
        .forEach(
            topicPartition -> {
              System.out.printf(
                  "Consumer assigned to partition: %s-%d%n",
                  topicPartition.topic(), topicPartition.partition());
            });

    if (!libraryEvent.getLibraryEventType().toString().equalsIgnoreCase("NEW")) {
       errorHandler(libraryEvent);
    }
  }

  private void errorHandler(LibraryEvent libraryEvent) {

    if (libraryEvent.getLibraryeventId() == null) {
      throw new IllegalArgumentException("Argument not valid");
    } else if(libraryEvent.getLibraryeventId()%2==0){
      throw new CustomeException("Recoverable exceptions occured");
    }
  }


}