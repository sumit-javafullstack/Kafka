package com.kafka.practice.LibraryEventManagemnetConsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryEventManagemnetConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventManagemnetConsumerApplication.class, args);
	}

}
