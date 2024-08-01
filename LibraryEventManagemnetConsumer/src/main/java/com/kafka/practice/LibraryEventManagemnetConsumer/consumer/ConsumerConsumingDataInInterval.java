//package com.kafka.practice.LibraryEventManagemnetConsumer.consumer;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kafka.practice.LibraryEventManagemnetConsumer.model.LibraryEvent;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@Service
//@Slf4j
//public class ConsumerConsumingDataInInterval {
//
//  private final List<LibraryEvent> messageList = new CopyOnWriteArrayList<>();
//  private ObjectMapper mapper = new ObjectMapper();
//
//  @KafkaListener(
//      topics = {"library-events"},
//      groupId = "library-events-listener-group",
//      containerFactory = "kafkaListenerconsumerFactoryConsumeInInterval")
//  public void consumerMessage(ConsumerRecord<Integer, String> messages)
//      throws JsonProcessingException {
//    LibraryEvent libraryEvent = mapper.readValue(messages.value(), LibraryEvent.class);
//    messageList.add(libraryEvent);
//    log.info("message consumed successfully");
//  }
//
//  @Scheduled(fixedRate = 15000)
//  public void executeConsumedMessage(){
//      if (!messageList.isEmpty()) {
//          System.out.println("Processing messages...");
//          messageList.forEach(System.out::println);
//          messageList.clear();
//      }
//
//  }
//}
