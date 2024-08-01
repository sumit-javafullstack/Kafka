package com.kafka.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class LibraryEventManagemnetProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventManagemnetProducerApplication.class, args);
	}

}
