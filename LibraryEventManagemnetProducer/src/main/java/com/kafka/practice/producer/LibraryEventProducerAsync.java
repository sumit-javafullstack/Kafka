package com.kafka.practice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.practice.model.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LibraryEventProducerAsync {

  private final KafkaTemplate<Integer, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${spring.kafka.topic}")
  public String topic;

  public LibraryEventProducerAsync(
      KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach1(
      LibraryEvent libraryEvent)
      throws JsonProcessingException, ExecutionException, InterruptedException {

    Integer key = libraryEvent.libraryeventId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    var completableFuture = kafkaTemplate.send(topic, key, value);
    return completableFuture.whenComplete(
        (sendResult, throwable) -> {
          if (throwable != null) {
            handleFailure(throwable);
          } else {
            handleSuccess(sendResult);
          }
        });
  }

  public SendResult<Integer, String> sendLibraryEventApproach2(LibraryEvent libraryEvent) {

    Integer key = libraryEvent.libraryeventId();
    String value = null;
    SendResult<Integer, String> integerStringSendResult = null;
    try {
      value = objectMapper.writeValueAsString(libraryEvent);
      integerStringSendResult = kafkaTemplate.send(topic, key, value).get(5, TimeUnit.SECONDS);
    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    log.info(
        "LibraryEventProducerAsync:sendLibraryEventApproach2- message successfully written to topic");
    return integerStringSendResult;
  }

  public CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach3(
          LibraryEvent libraryEvent)
          throws JsonProcessingException, ExecutionException, InterruptedException {

    Integer key = libraryEvent.libraryeventId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);
    var completableFuture = kafkaTemplate.send(producerRecord);
    return completableFuture.whenComplete(
            (sendResult, throwable) -> {
              if (throwable != null) {
                handleFailure(throwable);
              } else {
                handleSuccess(sendResult);
              }
            });
  }
  private ProducerRecord<Integer, String> buildProducerRecord(
      Integer key, String value, String topic) {

    List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

    return new ProducerRecord<>(topic, null, key, value, recordHeaders);
  }

  private void handleSuccess(SendResult<Integer, String> sendResult) {
    log.info(
        "Message {} successfully written to the kafka topic {} in partion {} ",
        sendResult.getProducerRecord(),
        sendResult.getRecordMetadata().topic(),
        sendResult.getRecordMetadata().partition());
  }

  private void handleFailure(Throwable throwable) {
    log.error("Error {} occured in producer {} ", throwable, throwable.toString());
  }
}
