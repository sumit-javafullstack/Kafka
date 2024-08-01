package com.kafka.practice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.practice.producer.LibraryEventProducerAsync;
import com.kafka.practice.model.LibraryEvent;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class LibraryEventController {

    private final LibraryEventProducerAsync libraryEventProducer;

    public LibraryEventController(LibraryEventProducerAsync libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    @PostMapping("/insert-record")
    public ResponseEntity<?> publishBook(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        libraryEventProducer.sendLibraryEventApproach3(libraryEvent);
        log.info("LibraryEventController:publishBook-Producer successfully written message to topic");
        return ResponseEntity.status(200).body(libraryEvent);
    }


}
