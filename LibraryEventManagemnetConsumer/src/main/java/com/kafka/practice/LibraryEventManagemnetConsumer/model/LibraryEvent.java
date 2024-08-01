package com.kafka.practice.LibraryEventManagemnetConsumer.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEvent {

  Integer libraryeventId;
  LibraryEventType libraryEventType;
  Book book;

  // we can have here static method and instance method id =f we need ny just like normal class
}
