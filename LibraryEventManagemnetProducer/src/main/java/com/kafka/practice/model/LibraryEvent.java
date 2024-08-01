package com.kafka.practice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEvent (Integer libraryeventId,
                            LibraryEventType libraryEventType,
                            @NotNull  @Valid  Book book){
    //we can have here static method and instance method id =f we need ny just like normal class
}
